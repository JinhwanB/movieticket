package com.jh.movieticket.chat.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.movieticket.auth.SecurityConfiguration;
import com.jh.movieticket.auth.TokenProvider;
import com.jh.movieticket.chat.dto.ChatMessageServiceDto;
import com.jh.movieticket.chat.service.ChatMessageService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(value = {ChatMessageController.class, SecurityConfiguration.class})
class ChatMessageControllerTest {

    @Autowired
    WebApplicationContext context;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ChatMessageService chatMessageService;

    @MockBean
    TokenProvider tokenProvider;

    MockMvc mockMvc;
    List<ChatMessageServiceDto> chatMessageServiceDtoList;

    @BeforeEach
    void before() {

        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        ChatMessageServiceDto chatMessageServiceDto = ChatMessageServiceDto.builder()
            .message("hello")
            .senderId("test")
            .notReadCount(0)
            .build();

        chatMessageServiceDtoList = List.of(chatMessageServiceDto);
    }

    @Test
    @DisplayName("채팅방의 전체 채팅 메시지 리스트 조회 컨트롤러")
    void chatMessageVerifyAllController() throws Exception {

        when(chatMessageService.chatMessageVerifyAll(any())).thenReturn(chatMessageServiceDtoList);

        mockMvc.perform(get("/chatmessages/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("채팅방의 전체 채팅 메시지 리스트 조회 컨트롤러 실패 - api 경로 틀림")
    void chatMessageVerifyAllControllerFail1() throws Exception {

        mockMvc.perform(get("/chatmessage/1"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("채팅방의 전체 채팅 메시지 리스트 조회 컨트롤러 실패 - pathVariable 입력 x")
    void chatMessageVerifyAllControllerFail2() throws Exception {

        mockMvc.perform(get("/chatmessages/ "))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("채팅방의 전체 채팅 메시지 리스트 조회 컨트롤러 실패 - 유효성 검사 실패")
    void chatMessageVerifyAllControllerFail3() throws Exception {

        mockMvc.perform(get("/chatmessages/0"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400));
    }
}