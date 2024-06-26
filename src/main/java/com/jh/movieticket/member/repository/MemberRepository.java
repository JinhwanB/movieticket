package com.jh.movieticket.member.repository;

import com.jh.movieticket.member.domain.Member;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserIdAndDeleteDate(String userId,
        LocalDateTime deleteDate); // 탈퇴하지 않은 회원 아이디를 통해 조회

    boolean existsByUserIdAndDeleteDate(String userId, LocalDateTime deleteDate); // 중복 아이디 확인

    boolean existsByEmailAndDeleteDate(String email, LocalDateTime deleteDate); // 중복 이메일 확인

    Page<Member> findAllByDeleteDate(LocalDateTime deleteDate,
        Pageable pageable); // 회원 리스트 페이징하여 조회
}
