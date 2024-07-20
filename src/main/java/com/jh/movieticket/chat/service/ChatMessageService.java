package com.jh.movieticket.chat.service;

import com.jh.movieticket.chat.domain.ChatMessage;
import com.jh.movieticket.chat.domain.ChatRoom;
import com.jh.movieticket.chat.dto.ChatMessageSendDto;
import com.jh.movieticket.chat.dto.ChatMessageServiceDto;
import com.jh.movieticket.chat.exception.ChatRoomErrorCode;
import com.jh.movieticket.chat.exception.ChatRoomException;
import com.jh.movieticket.chat.repository.ChatMessageRepository;
import com.jh.movieticket.chat.repository.ChatRoomRepository;
import com.jh.movieticket.config.CacheName;
import com.jh.movieticket.member.domain.Member;
import com.jh.movieticket.member.exception.MemberErrorCode;
import com.jh.movieticket.member.exception.MemberException;
import com.jh.movieticket.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, ChatMessageServiceDto> ChatMessageRedisTemplate;

    // 채팅방에 입장하고 있는 사람이 몇 명인지 확인이 가능해야 한다.
    // 메시지를 보낼 때 두 사람 모두 입장시 0, 한 사람만 입장한 경우는 1로 해서 보낸다.

    /**
     * 채팅 메시지 저장
     *
     * @param message 저장할 메시지 정보 dto
     */
    public void chatMessageSave(ChatMessageSendDto message) {

        ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomId())
            .orElseThrow(() -> new ChatRoomException(ChatRoomErrorCode.NOT_FOUND_CHAT_ROOM));

        Member sender = memberRepository.findByUserIdAndDeleteDate(message.getSender(), null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        int chatMemberCount = chatRoom.getChatMemberCount();

        ChatMessage chatMessage = ChatMessage.builder()
            .message(message.getMessage())
            .chatRoom(chatRoom)
            .sender(sender)
            .notReadCount(chatMemberCount == 2 ? 0 : 1)
            .build();
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        ChatMessageRedisTemplate.opsForList()
            .rightPush(String.valueOf(chatRoom.getId()),
                savedChatMessage.toServiceDto()); // redis 저장
    }

    /**
     * 채팅 메시지 내역 조회
     *
     * @param chatRoomId 채팅 내용 조회할 채팅방 pk
     * @return 채팅방의 채팅 내용 dto 리스트
     */
    public List<ChatMessageServiceDto> chatMessageVerifyAll(Long chatRoomId) {

        String chatMessageKey = CacheName.CHAT_MESSAGE_CACHE_NAME + "::" + chatRoomId;
        List<ChatMessageServiceDto> messageServiceDtoList = ChatMessageRedisTemplate.opsForList()
            .range(chatMessageKey, 0, -1);
        if (messageServiceDtoList != null) { // redis에 존재하는 경우
            return messageServiceDtoList;
        }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new ChatRoomException(ChatRoomErrorCode.NOT_FOUND_CHAT_ROOM));

        List<ChatMessageServiceDto> chatMessageServiceDtoList = chatMessageRepository.findAllByChatRoom(
                chatRoom)
            .stream()
            .map(ChatMessage::toServiceDto)
            .toList();

        ChatMessageRedisTemplate.opsForList()
            .rightPushAll(chatMessageKey, chatMessageServiceDtoList); // redis에 저장

        return chatMessageServiceDtoList;
    }
}
