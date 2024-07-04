package com.jh.movieticket.member.service;

import com.jh.movieticket.chat.repository.ChatRoomRepository;
import com.jh.movieticket.config.CacheName;
import com.jh.movieticket.grade.repository.GradeRepository;
import com.jh.movieticket.mail.service.MailService;
import com.jh.movieticket.member.domain.Member;
import com.jh.movieticket.member.domain.Role;
import com.jh.movieticket.member.dto.MemberModifyDto;
import com.jh.movieticket.member.dto.MemberServiceDto;
import com.jh.movieticket.member.dto.MemberSignInDto;
import com.jh.movieticket.member.dto.MemberSignUpDto;
import com.jh.movieticket.member.dto.VerifyCodeDto;
import com.jh.movieticket.member.exception.MemberErrorCode;
import com.jh.movieticket.member.exception.MemberException;
import com.jh.movieticket.member.repository.MemberRepository;
import com.jh.movieticket.reservation.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GradeRepository gradeRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RedisTemplate<String, Object> redisTemplate;

    private final String IS_EMAIL_AUTH = ":isAuth";

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
        redisTemplate.opsForValue().set(email + IS_EMAIL_AUTH, false); // 인증 진행 여부
    }

    /**
     * 입력한 코드가 올바른지 확인한다. redis 의 저장된 값이랑 비교
     *
     * @param request 사용자의 이메일과 입력한 인증코드
     */
    public void verifyCode(VerifyCodeDto.Request request) {

        String email = request.getEmail();
        String code = request.getCode();
        String originalCode = (String) redisTemplate.opsForValue().get(email);
        if (!code.equals(originalCode)) { // 인증 코드를 올바르지 않게 작성한 경우
            throw new MemberException(MemberErrorCode.NOT_MATCH_CODE);
        }

        redisTemplate.delete(email); // 인증코드 삭제
        redisTemplate.opsForValue().set(email + IS_EMAIL_AUTH, true); // 인증 여부 변경
    }

    /**
     * 회원가입
     *
     * @param request 회원 아이디, 비밀번호, 이메일, 권한
     * @return 회원가입한 아이디
     */
    public MemberServiceDto register(MemberSignUpDto.Request request) {

        String userId = request.getUserId();
        String userPw = request.getUserPw();
        String email = request.getEmail();
        Role role = request.getRole();

        if (Boolean.FALSE.equals(redisTemplate.hasKey(email + IS_EMAIL_AUTH))
            || Boolean.FALSE.equals(
            redisTemplate.opsForValue().get(email + IS_EMAIL_AUTH))) { // 이메일 인증을 진행하지 않은 경우
            throw new MemberException(MemberErrorCode.NOT_AUTH_OF_MAIL);
        }

        if (memberRepository.existsByUserIdAndDeleteDate(userId, null)) { // 중복된 아이디인 경우
            throw new MemberException(MemberErrorCode.EXIST_USER_ID);
        }

        if (memberRepository.existsByEmailAndDeleteDate(email, null)) { // 중복된 이메일인 경우
            throw new MemberException(MemberErrorCode.EXIST_EMAIL);
        }

        String encodedPw = passwordEncoder.encode(userPw); // 비밀번호 암호화
        redisTemplate.delete(email + IS_EMAIL_AUTH); // 모든 예외 상황 통과하면 인증 진행 여부 삭제

        Member member = Member.builder()
            .userId(userId)
            .userPW(encodedPw)
            .email(email)
            .role(role)
            .build();
        Member save = memberRepository.save(member);

        return save.toServiceDto();
    }

    /**
     * 로그인
     *
     * @param request 아이디와 비밀번호
     * @return 아이디와 권한
     */
    public MemberServiceDto login(MemberSignInDto.Request request) {

        String userId = request.getUserId();
        String userPw = request.getUserPw();

        Member member = memberRepository.findByUserIdAndDeleteDate(userId, null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        if (!passwordEncoder.matches(userPw, member.getUserPW())) { // 비밀번호가 다른 경우
            throw new MemberException(MemberErrorCode.NOT_MATCH_PASSWORD);
        }

        return member.toServiceDto();
    }

    /**
     * 회원 정보 수정
     *
     * @param userId  수정할 회원 아이디
     * @param request 수정할 정보
     * @return 수정된 정보
     */
    @CachePut(key = "#userId", value = CacheName.MEMBER_CACHE_NAME)
    public MemberServiceDto modifyMember(String userId, MemberModifyDto.Request request) {

        Member member = memberRepository.findByUserIdAndDeleteDate(userId, null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        String modifiedPw = request.getUserPw();
        String modifiedEmail = request.getEmail();

        // 기존의 이메일과 다른 경우
        if (!modifiedEmail.equals(member.getEmail())) {
            // 인증을 진행하지 않은 경우
            if (Boolean.FALSE.equals(redisTemplate.hasKey(modifiedEmail + IS_EMAIL_AUTH))
                || Boolean.FALSE.equals(
                redisTemplate.opsForValue().get(modifiedEmail + IS_EMAIL_AUTH))) {
                throw new MemberException(MemberErrorCode.NOT_AUTH_OF_MAIL);
            }

            if (memberRepository.existsByEmailAndDeleteDate( // 중복 이메일인 경우
                modifiedEmail, null)) {
                throw new MemberException(MemberErrorCode.EXIST_EMAIL);
            }
        }

        String encodedPw = passwordEncoder.encode(modifiedPw);

        Member modifiedMember = member.toBuilder()
            .userPW(encodedPw)
            .email(modifiedEmail)
            .build();
        Member save = memberRepository.save(modifiedMember);

        return save.toServiceDto();
    }

    /**
     * 회원 탈퇴, 연관 관계인 자식 엔티티 삭제
     *
     * @param userId 탈퇴할 회원 아이디
     */
    @CacheEvict(key = "#userId", value = CacheName.MEMBER_CACHE_NAME)
    public void deleteMember(String userId) {

        Member member = memberRepository.findByUserIdAndDeleteDate(userId, null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        // 자식 엔티티 하드 딜리트
        Long id = member.getId();
        gradeRepository.deleteGradeByMember(id);
        chatRoomRepository.deleteChatRoomByMember(id);
        reservationRepository.deleteReservationByMember(id);

        Member deletedMember = member.toBuilder()
            .deleteDate(LocalDateTime.now())
            .build();
        memberRepository.save(deletedMember);
    }

    /**
     * 회원 조회
     *
     * @param userId 조회할 회원 아이디
     * @return 조회된 회원 정보
     */
    @Transactional(readOnly = true)
    @Cacheable(key = "#userId", value = CacheName.MEMBER_CACHE_NAME)
    public MemberServiceDto verifyMember(String userId) {

        Member member = memberRepository.findByUserIdAndDeleteDate(userId, null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        return member.toServiceDto();
    }

    /**
     * 회원 전체 리스트 페이징하여 조회
     *
     * @param pageable 페이징 방법
     * @return 페이징된 전체 회원 리스트
     */
    @Transactional(readOnly = true)
    public Page<MemberServiceDto> allMembers(Pageable pageable) {

        Page<Member> members = memberRepository.findAllByDeleteDate(null, pageable);
        List<MemberServiceDto> list = members.getContent().stream()
            .map(Member::toServiceDto)
            .toList();

        return new PageImpl<>(list, pageable, list.size());
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