package com.jh.movieticket.member.service;

import com.jh.movieticket.config.CacheName;
import com.jh.movieticket.mail.service.MailService;
import com.jh.movieticket.member.domain.Member;
import com.jh.movieticket.member.domain.Role;
import com.jh.movieticket.member.dto.MemberSignInDto;
import com.jh.movieticket.member.dto.MemberSignUpDto;
import com.jh.movieticket.member.dto.MemberVerifyDto;
import com.jh.movieticket.member.dto.VerifyCodeDto;
import com.jh.movieticket.member.exception.MemberErrorCode;
import com.jh.movieticket.member.exception.MemberException;
import com.jh.movieticket.member.repository.MemberRepository;
import java.util.Random;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return memberRepository.findByUserIdAndDeleteDate(username, null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));
    }

    /**
     * 인증코드를 생성하여 이메일로 보낸다. 인증코드를 redis 에 저장하여 후에 인증코드를 알맞게 작성했는지 확인한다.
     *
     * @param email 회원 이메일
     */
    public void sendCode(String email) {

        if (memberRepository.existsByEmailAndDeleteDate(email, null)) {
            throw new MemberException(MemberErrorCode.EXIST_EMAIL);
        }

        String title = "영화 사이트 회원가입을 위한 인증코드";
        String code = createCode();
        String text = "인증코드 : " + code;
        mailService.sendEmail(email, title, text);
        redisTemplate.opsForValue().set(email, code); // redisTemplate 에 저장
    }

    /**
     * 입력한 코드가 올바른지 확인한다. redis 의 저장된 값이랑 비교
     *
     * @param request 사용자의 이메일과 입력한 인증코드
     * @return 올바르게 입력 시 true, 아니면 false
     */
    public boolean verifyCode(VerifyCodeDto.Request request) {

        String email = request.getEmail();
        String code = request.getCode();
        String originalCode = redisTemplate.opsForValue().get(email);
        if (code.equals(originalCode)) { // 인증 코드를 알맞게 작성한 경우
            redisTemplate.delete(email); // redis 에서 제거
            return true;
        }

        return false;
    }

    /**
     * 회원가입
     *
     * @param request 회원 아이디, 비밀번호, 이메일, 권한
     * @return 회원가입한 아이디
     */
    public String register(MemberSignUpDto.Request request) {

        String userId = request.getUserId();
        String userPw = request.getUserPw();
        String email = request.getEmail();
        Role role = request.getRole();

        if (memberRepository.existsByUserIdAndDeleteDate(userId, null)) { // 중복된 아이디인 경우
            throw new MemberException(MemberErrorCode.EXIST_USER_ID);
        }

        if (memberRepository.existsByEmailAndDeleteDate(email, null)) { // 중복된 이메일인 경우
            throw new MemberException(MemberErrorCode.EXIST_EMAIL);
        }

        String encodedPw = passwordEncoder.encode(userPw); // 비밀번호 암호화

        Member member = Member.builder()
            .userId(userId)
            .userPW(encodedPw)
            .email(email)
            .role(role)
            .build();
        memberRepository.save(member);

        return userId;
    }

    /**
     * 로그인
     *
     * @param request 아이디와 비밀번호
     * @return 아이디와 권한
     */
    public MemberSignInDto.Response login(MemberSignInDto.Request request) {

        String userId = request.getUserId();
        String userPw = request.getUserPw();

        Member member = memberRepository.findByUserIdAndDeleteDate(userId, null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        if (!passwordEncoder.matches(userPw, member.getUserPW())) { // 비밀번호가 다른 경우
            throw new MemberException(MemberErrorCode.NOT_MATCH_PASSWORD);
        }

        return member.toLoginResponse();
    }

    /**
     * 회원 조회
     *
     * @param userId 조회할 회원 아이디
     * @return 조회된 회원 정보
     */
    @Transactional(readOnly = true)
    @Cacheable(key = "#userId", value = CacheName.MEMBER_CACHE_NAME)
    public MemberVerifyDto.Response verifyMember(String userId) {

        Member member = memberRepository.findByUserIdAndDeleteDate(userId, null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        return member.toVerifyResponse();
    }

    /**
     * 이메일 인증을 위한 인증코드 생성 메서드
     *
     * @return 생성한 인증코드를 리턴
     */
    private String createCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        IntStream.range(0, 4)
            .forEach(i -> {
                int randomNum = random.nextInt(10); // 0~9 까지 랜덤 숫자
                sb.append(randomNum);
            });

        return sb.toString();
    }
}