package com.jh.movieticket.theater.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.movieticket.auth.SecurityConfiguration;
import com.jh.movieticket.auth.TokenProvider;
import com.jh.movieticket.theater.dto.TheaterCreateDto;
import com.jh.movieticket.theater.dto.TheaterCreateDto.Request;
import com.jh.movieticket.theater.dto.TheaterServiceDto;
import com.jh.movieticket.theater.service.TheaterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(value = {TheaterController.class, SecurityConfiguration.class})
class TheaterControllerTest {

    MockMvc mockMvc;
    TheaterServiceDto theaterServiceDto;
    TheaterCreateDto.Request createRequest;

    @MockBean
    TheaterService theaterService;

    @MockBean
    TokenProvider tokenProvider;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext context;

    @BeforeEach
    void before() {

        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        theaterServiceDto = TheaterServiceDto.builder()
            .name("1관")
            .seatCnt(30)
            .build();

        createRequest = TheaterCreateDto.Request.builder()
            .name("1관")
            .seatCnt(30)
            .build();
    }

    @Test
    @DisplayName("상영관 생성 컨트롤러")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void theaterCreateController() throws Exception {

        when(theaterService.createTheater(any())).thenReturn(theaterServiceDto);

        mockMvc.perform(post("/theaters/theater")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value(201))
            .andExpect(jsonPath("$.data").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 생성 컨트롤러 실패 - 로그인 x")
    void theaterCreateControllerFail1() throws Exception {

        mockMvc.perform(post("/theaters/theater")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 생성 컨트롤러 실패 - 권한 없음")
    @WithMockUser(username = "admin", roles = "USER")
    void theaterCreateControllerFail2() throws Exception {

        mockMvc.perform(post("/theaters/theater")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 생성 컨트롤러 실패 - url 경로 다름")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void theaterCreateControllerFail3() throws Exception {

        when(theaterService.createTheater(any())).thenReturn(theaterServiceDto);

        mockMvc.perform(post("/theaters/theaterer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 생성 컨트롤러 실패 - 상영관 이름 미입력")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void theaterCreateControllerFail4() throws Exception {

        Request badRequest = createRequest.toBuilder()
            .name("")
            .build();

        when(theaterService.createTheater(any())).thenReturn(theaterServiceDto);

        mockMvc.perform(post("/theaters/theater")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 생성 컨트롤러 실패 - 상영관 이름 형식 틀림")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void theaterCreateControllerFail5() throws Exception {

        Request badRequest = createRequest.toBuilder()
            .name("111관")
            .build();

        when(theaterService.createTheater(any())).thenReturn(theaterServiceDto);

        mockMvc.perform(post("/theaters/theater")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andDo(print());
    }
}