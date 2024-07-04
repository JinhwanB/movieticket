package com.jh.movieticket.reservation.repository;

import com.jh.movieticket.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Modifying
    @Transactional
    @Query("DELETE Reservation r WHERE r.member.id = :parentId")
    void deleteReservationByMember(Long parentId); // 회원 소프트딜리트로 인한 예매 엔티티 삭제 메소드
}
