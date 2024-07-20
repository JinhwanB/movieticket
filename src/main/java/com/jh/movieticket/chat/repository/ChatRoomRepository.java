package com.jh.movieticket.chat.repository;

import com.jh.movieticket.chat.domain.ChatRoom;
import com.jh.movieticket.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Modifying
    @Query("DELETE ChatRoom cr WHERE cr.member.id = :parentId")
    void deleteChatRoomByMember(Long parentId); // 회원 소프트딜리트로 인한 삭제 메소드

    Optional<ChatRoom> findByMember(Member member); // 회원을 통해 조회

    boolean existsByMember(Member member); // 회원으로 채팅방 존재 유무 확인
}
