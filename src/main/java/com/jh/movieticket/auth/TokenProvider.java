package com.jh.movieticket.auth;

import com.jh.movieticket.member.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    private static final String KEY_ROLES = "roles";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1hour
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 24 * 1000 * 60 * 60; // 24hour
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String COOKIE_NAME = "refreshToken";

    private final MemberService memberService;

    @Value("${spring.jwt.secret}")
    private String secret;

    // access 토큰 생성 발급
    public String generateAccessToken(String userName, List<String> roles) {

        return createToken(userName, roles, ACCESS_TOKEN_EXPIRE_TIME);
    }

    // refresh 토큰 생성 발급
    public void generateRefreshToken(String userName, List<String> roles, HttpServletResponse response) {

        String refreshToken = createToken(userName, roles, REFRESH_TOKEN_EXPIRE_TIME);
        tokenToCookie(refreshToken, response);
    }

    // accessToken 재발급
    public String reGenerateAccessToken(HttpServletRequest request, HttpServletResponse response){

        String refreshToken = getRefreshTokenFromCookie(request);

        if(!validateToken(refreshToken)){ // 리프레시 토큰이 만료된 경우
            throw new TokenException(TokenErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        String userName = getUserName(refreshToken);
        List<String> userRole = getUserRole(refreshToken);

        generateRefreshToken(userName, userRole, response); // 새로운 refreshToken을 쿠키에 저장

        return generateAccessToken(userName, userRole); // 새로운 accessToken 발급
    }

    // 로그아웃
    // access 토큰은 클라이언트에서 제거한다는 가정(서버에서 관리하지 않는다.)
    // refresh 토큰을 쿠키에서 지운다.
    public void logout(HttpServletRequest request, HttpServletResponse response){

        String refreshToken = getRefreshTokenFromCookie(request);
        deleteRefreshToken(refreshToken, response);
    }


    // jwt를 사용하여 사용자의 인증 정보 가져오는 메소드
    @Transactional
    public Authentication getAuthentication(String jwt) {

        UserDetails userDetails = memberService.loadUserByUsername(getUserName(jwt));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 유저 아이디 가져오기
    public String getUserName(String token) {

        return parseClaims(token).getSubject();
    }

    // 유저 권한 가져오기
    public List<String> getUserRole(String token){

        return List.of(String.valueOf(parseClaims(token).get(KEY_ROLES)));
    }

    // 헤더 정보의 토큰을 가져온다.
    public String resolveTokenFromRequest(HttpServletRequest request) {

        String token = request.getHeader(TOKEN_HEADER);

        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }

        return null;
    }

    // 토큰 검증
    public boolean validateToken(String token) {

        if (!StringUtils.hasText(token)) return false;

        Claims claims = parseClaims(token);

        return !claims.getExpiration().before(new Date());
    }

    // 토큰 정보 가져오기
    private Claims parseClaims(String token) {

        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            log.error("토큰 정보 에러 = {}", e.getMessage());
            throw new TokenException(TokenErrorCode.EXPIRED_TOKEN);
        }
    }

    // 토큰 생성
    private String createToken(String userName, List<String> role, long tokenExpiredTime){

        Claims claims = Jwts.claims().setSubject(userName);
        claims.put(KEY_ROLES, role);

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + tokenExpiredTime);

        return Jwts.builder()
            .setClaims(claims)
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuedAt(now) // 토큰 생성 시간
            .setExpiration(expireDate) // 토큰 만료 시간
            .signWith(SignatureAlgorithm.HS512, secret) // 사용할 암호화 알고리즘, 비밀키
            .compact();
    }

    // 토큰을 쿠키에 저장
    private void tokenToCookie(String refreshToken, HttpServletResponse response){

        Cookie cookie = new Cookie(COOKIE_NAME, refreshToken);

        // 쿠키 속성 설정
        cookie.setHttpOnly(true); // httpOnly 설정(js 접근 불가)
        cookie.setSecure(true); // https 설정 (https 외에 통신 불가)
        cookie.setPath("/"); // 모든 곳에서 쿠키 열람 가능
        cookie.setMaxAge(86400); // 24시간

        response.addCookie(cookie);
    }

    // refresh 토큰 쿠키에서 삭제
    private void deleteRefreshToken(String refreshToken, HttpServletResponse response){

        Cookie cookie = new Cookie(COOKIE_NAME, refreshToken);

        // 쿠키 속성 설정
        cookie.setPath("/"); // 모든 곳에서 쿠키 열람 가능
        cookie.setMaxAge(0); // 삭제

        response.addCookie(cookie);
    }

    // 쿠키에 저장된 리프레시 토큰 가져오기
    private String getRefreshTokenFromCookie(HttpServletRequest request){

        Cookie cookie = Arrays.stream(request.getCookies())
            .filter(c -> c.getName().equals(COOKIE_NAME))
            .findAny()
            .orElse(null);

        if(cookie == null){
            throw new TokenException(TokenErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }

        return cookie.getValue();
    }
}