package com.jh.movieticket.chat.service;

import com.jh.movieticket.chat.domain.ChatRoom;
import com.jh.movieticket.chat.dto.ChatRoomServiceDto;
import com.jh.movieticket.chat.dto.ChatRoomVerifyDto;
import com.jh.movieticket.chat.exception.ChatRoomErrorCode;
import com.jh.movieticket.chat.exception.ChatRoomException;
import com.jh.movieticket.chat.repository.ChatRoomRepository;
import com.jh.movieticket.config.CacheName;
import com.jh.movieticket.member.domain.Member;
import com.jh.movieticket.member.exception.MemberErrorCode;
import com.jh.movieticket.member.exception.MemberException;
import com.jh.movieticket.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final CacheManager redisCacheManager;

    /**
     * 채팅방 삭제 서비스
     *
     * @param id 삭제할 채팅방 pk
     */
    public void deleteChatRoom(Long id) {

        ChatRoom chatRoom = chatRoomRepository.findById(id)
            .orElseThrow(() -> new ChatRoomException(ChatRoomErrorCode.NOT_FOUND_CHAT_ROOM));

        Cache cache = redisCacheManager.getCache(CacheName.CHAT_ROOM_CACHE_NAME);
        if (cache != null) {
            cache.evict(chatRoom.getMember().getUserId()); // 캐시 삭제
        }

        chatRoom = chatRoom.toBuilder()
            .chatMessageList(new ArrayList<>())
            .deleteDate(LocalDateTime.now())
            .build();
        chatRoomRepository.save(chatRoom);
    }

    /**
     * 채팅방 조회 서비스
     *
     * @param verifyRequest 조회 정보 dto
     * @return 채팅방 dto
     */
    @Cacheable(key = "#verifyRequest.userId", value = CacheName.CHAT_ROOM_CACHE_NAME)
    public ChatRoomServiceDto verifyChatRoom(ChatRoomVerifyDto.Request verifyRequest) {

        Member member = memberRepository.findByUserIdAndDeleteDate(verifyRequest.getUserId(), null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        ChatRoom chatRoom = chatRoomRepository.findByMember(member)
            .orElse(null);

        if (chatRoom == null) { // 조회한 채팅방이 없는 경우 새로 생성 후 반환
            return createChatRoom(verifyRequest.getUserId(),
                verifyRequest.getAdminId()).toServiceDto();
        }

        return chatRoom.toServiceDto();
    }

    /**
     * 페이징 처리된 전체 채팅방 리스트 조회 서비스
     *
     * @param pageable 페이징 정보
     * @return 페이징 처리된 전체 채팅방 dto 리스트
     */
    public Page<ChatRoomServiceDto> verifyAllChatRoom(Pageable pageable) {

        Page<ChatRoom> allOfChatRoom = chatRoomRepository.findAll(pageable);
        List<ChatRoomServiceDto> chatRoomServiceDtoList = allOfChatRoom.getContent().stream()
            .map(ChatRoom::toServiceDto)
            .toList();

        return new PageImpl<>(chatRoomServiceDtoList, pageable, chatRoomServiceDtoList.size());
    }

    /**
     * 채팅방 생성 메소드
     *
     * @param userId  회원 아이디
     * @param adminId 관리자 아이디
     * @return 생성된 채팅방
     */
    private ChatRoom createChatRoom(String userId, String adminId) {

        Member member = memberRepository.findByUserIdAndDeleteDate(userId, null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        Member admin = memberRepository.findByUserIdAndDeleteDate(adminId, null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        ChatRoom chatRoom = ChatRoom.builder()
            .admin(admin)
            .member(member)
            .build();
        return chatRoomRepository.save(chatRoom);
    }
}
