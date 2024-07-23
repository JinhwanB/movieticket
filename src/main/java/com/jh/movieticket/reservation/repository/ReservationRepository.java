package com.jh.movieticket.reservation.repository;

import com.jh.movieticket.reservation.domain.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Modifying
    @Query("DELETE Reservation r WHERE r.member.id = :parentId")
    void deleteReservationByMember(Long parentId); // 회원 소프트딜리트로 인한 예매 엔티티 삭제 메소드

    boolean existsByReservationNumber(String reservationNumber); // 예약 번호 중복 여부 확인

    @Query("select r from Reservation r where r.member.userId = :memberId and r.deleteDate is null")
    Page<Reservation> findAllByMemberId(String memberId,
        Pageable pageable); // 회원 아이디를 통해 예약 리스트 페이징하여 조회
}
