package com.jh.movieticket.chat.repository;

import com.jh.movieticket.chat.domain.ChatMessage;
import com.jh.movieticket.chat.domain.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Modifying
    @Query("delete ChatMessage cm where cm.sender.id = :parentId")
    void deleteChatMessageByMember(Long parentId); // 회원 소프트딜리트로 인한 채팅메시지 삭제 메소드

    List<ChatMessage> findAllByChatRoom(ChatRoom chatRoom); // 채팅방을 통해 채팅 메시지 리스트 조회
}
