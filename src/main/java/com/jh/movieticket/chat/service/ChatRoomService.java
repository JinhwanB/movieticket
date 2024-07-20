package com.jh.movieticket.chat.service;

import com.jh.movieticket.chat.domain.ChatMessage;
import com.jh.movieticket.chat.domain.ChatRoom;
import com.jh.movieticket.chat.dto.ChatMessageServiceDto;
import com.jh.movieticket.chat.dto.ChatRoomCreateDto;
import com.jh.movieticket.chat.dto.ChatRoomJoinDto;
import com.jh.movieticket.chat.dto.ChatRoomOutDto;
import com.jh.movieticket.chat.dto.ChatRoomServiceDto;
import com.jh.movieticket.chat.dto.ChatRoomVerifyDto;
import com.jh.movieticket.chat.exception.ChatRoomErrorCode;
import com.jh.movieticket.chat.exception.ChatRoomException;
import com.jh.movieticket.chat.repository.ChatMessageRepository;
import com.jh.movieticket.chat.repository.ChatRoomRepository;
import com.jh.movieticket.config.CacheName;
import com.jh.movieticket.member.domain.Member;
import com.jh.movieticket.member.exception.MemberErrorCode;
import com.jh.movieticket.member.exception.MemberException;
import com.jh.movieticket.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, ChatMessageServiceDto> chatMessageRedisTemplate;

    // 채팅방에 입장하고 있는 사람이 몇 명인지 확인이 가능해야 한다.
    // 입장 시 채팅 메시지들을 불러와 입장한 회원과 비교하여 해당 회원이 작성한 메시지가 아닌  메시지의 count - 1을 진행(읽음 처리)
    // 조회시에는 메시지들을 불러와 조회한 사람이 아닌 사람이 보낸 메시지가 1이 있는 경우를 카운팅하여 새로운 메시지 갯수를 보여준다.(최대 100개)

    /**
     * 채팅방 생성 메소드
     *
     * @param createRequest 채팅방 생성 정보 dto
     * @return 생성된 채팅방 dto
     */
    public ChatRoomServiceDto createChatRoom(ChatRoomCreateDto.Request createRequest) {

        Member member = memberRepository.findByUserIdAndDeleteDate(createRequest.getUserId(), null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        Member admin = memberRepository.findByUserIdAndDeleteDate(createRequest.getAdminId(), null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        if (chatRoomRepository.existsByMember(member)) { // 이미 채팅방 존재하는 경우
            throw new ChatRoomException(ChatRoomErrorCode.EXIST_CHAT_ROOM);
        }

        ChatRoom chatRoom = ChatRoom.builder()
            .admin(admin)
            .member(member)
            .chatMemberCount(0)
            .notReadMessage(0)
            .build();

        return chatRoomRepository.save(chatRoom).toServiceDto();
    }

    /**
     * 채팅방 입장
     *
     * @param joinRequest 채팅방 입장 정보 dto
     */
    public void enterChatRoom(ChatRoomJoinDto.Request joinRequest) {

        ChatRoom chatRoom = chatRoomRepository.findById(joinRequest.getChatRoomId())
            .orElseThrow(() -> new ChatRoomException(ChatRoomErrorCode.NOT_FOUND_CHAT_ROOM));

        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoom(chatRoom);
        if (chatMessageList != null && !chatMessageList.isEmpty()) {
            chatMessageList.stream()
                .filter(cm -> !cm.getSender().getUserId().equals(joinRequest.getEnterMemberId())
                    && cm.getNotReadCount() != 0) // 상대방이 보낸 메시지 중 안 읽은 수가 0이 아닌 경우만 필터링
                .forEach(cm -> {
                    cm = cm.toBuilder()
                        .notReadCount(cm.getNotReadCount() - 1) // 안 읽은 수를 -1 처리함으로써 읽음 처리
                        .build();
                    chatMessageRepository.save(cm);
                });

            String chatMessageKey =
                CacheName.CHAT_MESSAGE_CACHE_NAME + "::" + joinRequest.getChatRoomId();
            List<ChatMessageServiceDto> chatMessageServiceDtoList = chatMessageList.stream()
                .map(ChatMessage::toServiceDto)
                .toList();
            chatMessageRedisTemplate.opsForList()
                .rightPushAll(chatMessageKey, chatMessageServiceDtoList); // 채팅 메시지 redis 저장
        }

        chatRoom = chatRoom.toBuilder()
            .chatMemberCount(chatRoom.getChatMemberCount() + 1) // 채팅방 인원 수 증가
            .notReadMessage(0) // 안읽은 메시지 수 초기화
            .build();
        chatRoomRepository.save(chatRoom);
    }

    /**
     * 채팅방 퇴장
     *
     * @param outRequest 퇴장할 채팅방 정보 dto
     */
    public void outChatRoom(ChatRoomOutDto.Request outRequest) {

        ChatRoom chatRoom = chatRoomRepository.findById(outRequest.getChatRoomId())
            .orElseThrow(() -> new ChatRoomException(ChatRoomErrorCode.NOT_FOUND_CHAT_ROOM));

        chatRoom = chatRoom.toBuilder()
            .chatMemberCount(chatRoom.getChatMemberCount() - 1) // 채팅방 인원 수 감소
            .build();
        chatRoomRepository.save(chatRoom);
    }

    /**
     * 채팅방 삭제 서비스
     *
     * @param id 삭제할 채팅방 pk
     */
    public void deleteChatRoom(Long id) {

        ChatRoom chatRoom = chatRoomRepository.findById(id)
            .orElseThrow(() -> new ChatRoomException(ChatRoomErrorCode.NOT_FOUND_CHAT_ROOM));

        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoom(chatRoom);
        chatMessageRepository.deleteAll(chatMessageList); // 채팅 메시지 삭제

        String chatMessageKey = CacheName.CHAT_MESSAGE_CACHE_NAME + "::" + chatRoom.getId();
        if (Boolean.TRUE.equals(chatMessageRedisTemplate.hasKey(chatMessageKey))) {
            chatMessageRedisTemplate.delete(chatMessageKey); // 채팅 메시지 캐시 삭제
        }

        chatRoom = chatRoom.toBuilder()
            .deleteDate(LocalDateTime.now())
            .build();
        chatRoomRepository.save(chatRoom);
    }

    /**
     * 채팅방 조회
     *
     * @param verifyRequest 조회할 채팅방 정보 dto
     * @return 조회된 채팅방 dto
     */
    public ChatRoomServiceDto verifyChatRoom(ChatRoomVerifyDto.Request verifyRequest) {

        Member member = memberRepository.findByUserIdAndDeleteDate(
                verifyRequest.getChatRoomMemberId(), null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        ChatRoom chatRoom = chatRoomRepository.findByMember(member)
            .orElseThrow(() -> new ChatRoomException(ChatRoomErrorCode.NOT_FOUND_CHAT_ROOM));

        return getChatRoomWithNotReadCount(verifyRequest.getVerifyMemberId(), chatRoom);
    }

    /**
     * 페이징 처리된 전체 채팅방 리스트 조회 서비스
     *
     * @param pageable 페이징 정보
     * @return 페이징 처리된 전체 채팅방 dto 리스트
     */
    public Page<ChatRoomServiceDto> verifyAllChatRoom(String verifyMemberId, Pageable pageable) {

        Page<ChatRoom> allOfChatRoom = chatRoomRepository.findAll(pageable);
        List<ChatRoomServiceDto> chatRoomServiceDtoList = allOfChatRoom.getContent().stream()
            .map(cr -> getChatRoomWithNotReadCount(verifyMemberId, cr)) // 안읽은 메시지 업데이트
            .toList();

        return new PageImpl<>(chatRoomServiceDtoList, pageable, chatRoomServiceDtoList.size());
    }

    /**
     * 채팅방의 채팅 메시지 중 안읽은 메시지 갯수 카운팅 메소드
     *
     * @param verifyMemberId 채팅방 조회하는 회원 아이디
     * @param chatRoom       조회할 채팅방
     * @return 카운팅한 안읽은 메시지 갯수
     */
    private ChatRoomServiceDto getChatRoomWithNotReadCount(String verifyMemberId,
        ChatRoom chatRoom) {

        long count;
        String chatMessageKey = CacheName.CHAT_MESSAGE_CACHE_NAME + "::" + chatRoom.getId();
        if (Boolean.TRUE.equals(
            chatMessageRedisTemplate.hasKey(chatMessageKey))) { // redis에 채팅 메시지가 존재하는 경우
            List<ChatMessageServiceDto> messageServiceDtoList = chatMessageRedisTemplate.opsForList()
                .range(chatMessageKey, 0, -1);
            count = messageServiceDtoList.stream()
                .filter(cmd -> !cmd.getSenderId().equals(verifyMemberId)
                    && cmd.getNotReadCount() != 0) // 읽지 않은 메시지 최대 100개까지 카운팅
                .limit(100)
                .count();
        } else { // redis에 채팅 메시지가 존재하지 않는 경우
            List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoom(chatRoom);
            count = chatMessageList.stream()
                .filter(cm -> !cm.getSender().getUserId().equals(verifyMemberId)
                    && cm.getNotReadCount() != 0) // 읽지 않은 메시지 최대 100개까지 카운팅
                .limit(100)
                .count();
        }

        chatRoom = chatRoom.toBuilder()
            .notReadMessage(count)
            .build();

        return chatRoomRepository.save(chatRoom).toServiceDto();
    }
}
