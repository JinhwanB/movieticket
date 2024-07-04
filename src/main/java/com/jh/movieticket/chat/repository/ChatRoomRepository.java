package com.jh.movieticket.chat.repository;

import com.jh.movieticket.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Modifying
    @Transactional
    @Query("DELETE ChatRoom cr WHERE cr.member.id = :parentId")
    void deleteChatRoomByMember(Long parentId); // 회원 소프트딜리트로 인한 삭제 메소드
}
