package com.jh.movieticket.grade.repository;

import com.jh.movieticket.grade.domain.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    @Modifying
    @Query("DELETE Grade g WHERE g.member.id = :parentId")
    void deleteGradeByMember(Long parentId); // 회원 소프트딜리트로 인한 평점 엔티티 삭제 메소드
}
