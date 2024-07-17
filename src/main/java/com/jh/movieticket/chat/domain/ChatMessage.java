package com.jh.movieticket.chat.domain;

import com.jh.movieticket.chat.dto.ChatMessageServiceDto;
import com.jh.movieticket.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom; // 채팅방

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender; // 보낸 사람

    @Column(nullable = false, length = 5000)
    private String message; // 메시지 내용

    @Column
    private LocalDateTime deleteDate; // 삭제날짜

    /**
     * Entity -> ServiceDto
     *
     * @return ServiceDto
     */
    public ChatMessageServiceDto toServiceDto() {

        return ChatMessageServiceDto.builder()
            .id(id)
            .message(message)
            .senderId(sender.getUserId())
            .build();
    }
}
