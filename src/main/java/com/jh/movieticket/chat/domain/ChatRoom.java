package com.jh.movieticket.chat.domain;

import com.jh.movieticket.chat.dto.ChatRoomServiceDto;
import com.jh.movieticket.config.BaseTimeEntity;
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
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@SQLRestriction(value = "delete_date IS NULL")
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Member admin; // 관리자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 회원

    @Column(nullable = false)
    private int chatMemberCount; // 채팅방 멤버 수

    @Column(nullable = false)
    private long notReadMessage; // 안읽은 메시지 갯수

    @Column
    private LocalDateTime deleteDate; // 삭제날짜

    /**
     * 채잍메시지 저장 메소드
     *
     * @param chatMessage 저장할 채팅 메시지
     */
    public void addChatMessage(ChatMessage chatMessage) {

        chatMessageList = new ArrayList<>();
        chatMessageList.add(chatMessage);
    }

    /**
     * Entity -> ServiceDto
     *
     * @return ServiceDto
     */
    public ChatRoomServiceDto toServiceDto() {

        return ChatRoomServiceDto.builder()
            .id(id)
            .adminId(admin.getUserId())
            .memberId(member.getUserId())
            .chatMessageList(
                chatMessageList != null ? chatMessageList.stream().map(ChatMessage::toServiceDto)
                    .toList() : new ArrayList<>())
            .build();
    }
}
