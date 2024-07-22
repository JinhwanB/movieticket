package com.jh.movieticket.chat.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.movieticket.auth.SecurityConfiguration;
import com.jh.movieticket.auth.TokenProvider;
import com.jh.movieticket.chat.dto.ChatRoomCreateDto;
import com.jh.movieticket.chat.dto.ChatRoomServiceDto;
import com.jh.movieticket.chat.dto.ChatRoomVerifyDto;
import com.jh.movieticket.chat.service.ChatRoomService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest({ChatRoomController.class, SecurityConfiguration.class})
class ChatRoomControllerTest {

    @Autowired
    WebApplicationContext context;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ChatRoomService chatRoomService;

    @MockBean
    TokenProvider tokenProvider;

    MockMvc mockMvc;
    ChatRoomCreateDto.Request createRequest;
    ChatRoomServiceDto chatRoomServiceDto;
    ChatRoomVerifyDto.Request verifyRequest;
    Pageable pageable;
    Page<ChatRoomServiceDto> chatRoomServiceDtoPageList;

    @BeforeEach
    void before() {

        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        createRequest = ChatRoomCreateDto.Request.builder()
            .userId("test")
            .adminId("admin")
            .build();

        chatRoomServiceDto = ChatRoomServiceDto.builder()
            .adminId("admin")
            .chatMemberCount(0)
            .memberId("test")
            .notReadMessage(0)
            .build();

        verifyRequest = ChatRoomVerifyDto.Request.builder()
            .chatRoomMemberId("test")
            .verifyMemberId("test")
            .build();

        pageable = PageRequest.of(0, 10, Direction.ASC, "id");

        List<ChatRoomServiceDto> chatRoomServiceDtoList = List.of(chatRoomServiceDto);

        chatRoomServiceDtoPageList = new PageImpl<>(chatRoomServiceDtoList, pageable,
            chatRoomServiceDtoList.size());
    }

    @Test
    @DisplayName("채팅방 생성 컨트롤러")
    void chatRoomCreateController() throws Exception {

        when(chatRoomService.createChatRoom(any())).thenReturn(chatRoomServiceDto);

        mockMvc.perform(post("/chatrooms/chatroom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(createRequest)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value(201))
            .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("채팅방 생성 컨트롤러 실패 - api 경로 틀림")
    void chatRoomCreateControllerFail1() throws Exception {

        when(chatRoomService.createChatRoom(any())).thenReturn(chatRoomServiceDto);

        mockMvc.perform(post("/chatrooms/chatroom/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(createRequest)))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("채팅방 생성 컨트롤러 실패 - 유효성 검사 실패")
    void chatRoomCreateControllerFail2() throws Exception {

        createRequest = createRequest.toBuilder()
            .userId("")
            .build();

        mockMvc.perform(post("/chatrooms/chatroom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(createRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400));
    }

    @Test
    @DisplayName("채팅방 삭제 컨트롤러")
    void chatRoomDeleteController() throws Exception {

        mockMvc.perform(delete("/chatrooms/chatroom/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("채팅방 삭제 컨트롤러 실패 - api 경로 틀림")
    void chatRoomDeleteControllerFail1() throws Exception {

        mockMvc.perform(delete("/chatrooms/chatroo/1"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("채팅방 삭제 컨트롤러 실패 - pathVariable 입력 x")
    void chatRoomDeleteControllerFail2() throws Exception {

        mockMvc.perform(delete("/chatrooms/chatroom/ "))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("채팅방 삭제 컨트롤러 실패 - pk값 0 또는 음수 입력")
    void chatRoomDeleteControllerFail3() throws Exception {

        mockMvc.perform(delete("/chatrooms/chatroom/0"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400));
    }

    @Test
    @DisplayName("채팅방 조회 컨트롤러")
    void chatRoomVerifyController() throws Exception {

        when(chatRoomService.verifyChatRoom(any())).thenReturn(chatRoomServiceDto);

        mockMvc.perform(get("/chatrooms/chatroom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(verifyRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("채팅방 조회 컨트롤러 실패 - api 경로 틀림")
    void chatRoomVerifyControllerFail1() throws Exception {

        mockMvc.perform(get("/chatrooms/chatroom/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(verifyRequest)))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("채팅방 조회 컨트롤러 실패 - 유효성 검사 실패")
    void chatRoomVerifyControllerFail12() throws Exception {

        verifyRequest = verifyRequest.toBuilder()
            .chatRoomMemberId("")
            .build();

        mockMvc.perform(get("/chatrooms/chatroom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(verifyRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400));
    }

    @Test
    @DisplayName("채팅방 전체 리스트 조회 컨트롤러")
    void chatRoomVerifyAllController() throws Exception {

        when(chatRoomService.verifyAllChatRoom(any(), any())).thenReturn(
            chatRoomServiceDtoPageList);

        mockMvc.perform(get("/chatrooms/test"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("채팅방 전체 리스트 조회 컨트롤러 실패 - api 경로 틀림")
    void chatRoomVerifyAllControllerFail1() throws Exception {

        mockMvc.perform(get("/chatroom/test"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("채팅방 전체 리스트 조회 컨트롤러 실패 - pathVariable 입력 x")
    void chatRoomVerifyAllControllerFail2() throws Exception {

        mockMvc.perform(get("/chatrooms/ "))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400));
    }
}