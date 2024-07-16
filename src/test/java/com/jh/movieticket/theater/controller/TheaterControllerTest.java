package com.jh.movieticket.theater.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.movieticket.auth.SecurityConfiguration;
import com.jh.movieticket.auth.TokenProvider;
import com.jh.movieticket.theater.dto.TheaterCreateDto;
import com.jh.movieticket.theater.dto.TheaterCreateDto.Request;
import com.jh.movieticket.theater.dto.TheaterModifyDto;
import com.jh.movieticket.theater.dto.TheaterServiceDto;
import com.jh.movieticket.theater.service.TheaterService;
import java.util.ArrayList;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(value = {TheaterController.class, SecurityConfiguration.class})
class TheaterControllerTest {

    MockMvc mockMvc;
    TheaterServiceDto theaterServiceDto;
    TheaterCreateDto.Request createRequest;
    TheaterModifyDto.Request modifyRequest;
    Pageable pageable = PageRequest.of(0, 10, Direction.ASC, "name");
    Page<TheaterServiceDto> pageableList;

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

        modifyRequest = TheaterModifyDto.Request.builder()
            .originName("1관")
            .changedName("2관")
            .build();

        List<TheaterServiceDto> list = new ArrayList<>();
        TheaterServiceDto secondServiceDto = theaterServiceDto.toBuilder()
            .name("2관")
            .seatCnt(20)
            .build();
        list.add(theaterServiceDto);
        list.add(secondServiceDto);
        pageableList = new PageImpl<>(list, pageable, list.size());
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

    @Test
    @DisplayName("상영관 수정 컨트롤러")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void theaterModifyController() throws Exception {

        when(theaterService.updateTheater(any())).thenReturn(theaterServiceDto);

        mockMvc.perform(put("/theaters/theater")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 수정 컨트롤러 실패 - url 경로 다름")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void theaterModifyControllerFail1() throws Exception {

        when(theaterService.updateTheater(any())).thenReturn(theaterServiceDto);

        mockMvc.perform(put("/theaters/theaterer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyRequest)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 수정 컨트롤러 실패 - 로그인 x")
    void theaterModifyControllerFail2() throws Exception {

        when(theaterService.updateTheater(any())).thenReturn(theaterServiceDto);

        mockMvc.perform(put("/theaters/theater")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 수정 컨트롤러 실패 - 권한 없음")
    @WithMockUser(username = "admin", roles = "USER")
    void theaterModifyControllerFail3() throws Exception {

        when(theaterService.updateTheater(any())).thenReturn(theaterServiceDto);

        mockMvc.perform(put("/theaters/theater")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyRequest)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 수정 컨트롤러 실패 - originName 미입력")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void theaterModifyControllerFail4() throws Exception {

        TheaterModifyDto.Request badRequest = modifyRequest.toBuilder()
            .originName("")
            .build();

        when(theaterService.updateTheater(any())).thenReturn(theaterServiceDto);

        mockMvc.perform(put("/theaters/theater")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 수정 컨트롤러 실패 - originName 형식 틀림")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void theaterModifyControllerFail5() throws Exception {

        TheaterModifyDto.Request badRequest = modifyRequest.toBuilder()
            .originName("111관")
            .build();

        when(theaterService.updateTheater(any())).thenReturn(theaterServiceDto);

        mockMvc.perform(put("/theaters/theater")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 수정 컨트롤러 실패 - changeName 미입력")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void theaterModifyControllerFail6() throws Exception {

        TheaterModifyDto.Request badRequest = modifyRequest.toBuilder()
            .changedName("")
            .build();

        when(theaterService.updateTheater(any())).thenReturn(theaterServiceDto);

        mockMvc.perform(put("/theaters/theater")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 수정 컨트롤러 실패 - changeName 형식 틀림")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void theaterModifyControllerFail7() throws Exception {

        TheaterModifyDto.Request badRequest = modifyRequest.toBuilder()
            .changedName("111관")
            .build();

        when(theaterService.updateTheater(any())).thenReturn(theaterServiceDto);

        mockMvc.perform(put("/theaters/theater")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 삭제 컨트롤러")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void theaterDeleteController() throws Exception {

        mockMvc.perform(delete("/theaters/theater/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 삭제 컨트롤러 실패 - url 경로 다름")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void theaterDeleteControllerFail1() throws Exception {

        mockMvc.perform(delete("/theaters/theate"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 삭제 컨트롤러 실패 - 로그인 x")
    void theaterDeleteControllerFail2() throws Exception {

        mockMvc.perform(delete("/theaters/theater/1"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 삭제 컨트롤러 실패 - 권한 없음")
    @WithMockUser(username = "admin", roles = "USER")
    void theaterDeleteControllerFail3() throws Exception {

        mockMvc.perform(delete("/theaters/theater/1"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 삭제 컨트롤러 실패 - 삭제할 상영관 입력 x")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void theaterDeleteControllerFail4() throws Exception {

        mockMvc.perform(delete("/theaters/theater/ "))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("상영관 삭제 컨트롤러 실패 - 삭제할 상영관 이름 형식 틀림")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void theaterDeleteControllerFail5() throws Exception {

        mockMvc.perform(delete("/theaters/theater/0"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 조회 컨트롤러")
    void theaterVerifyController() throws Exception {

        when(theaterService.verify(anyString())).thenReturn(theaterServiceDto);

        mockMvc.perform(get("/theaters/theater/1관/detail"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 조회 컨트롤러 실패 - url 경로 틀림")
    void theaterVerifyControllerFail1() throws Exception {

        when(theaterService.verify(anyString())).thenReturn(theaterServiceDto);

        mockMvc.perform(get("/theaters/theater/1관/de"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 조회 컨트롤러 실패 - 조회할 상영관 입력 x")
    void theaterVerifyControllerFail2() throws Exception {

        when(theaterService.verify(anyString())).thenReturn(theaterServiceDto);

        mockMvc.perform(get("/theaters/theater/ /detail"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 조회 컨트롤러 실패 - 조회할 상영관 이름 형식 틀림")
    void theaterVerifyControllerFail3() throws Exception {

        when(theaterService.verify(anyString())).thenReturn(theaterServiceDto);

        mockMvc.perform(get("/theaters/theater/111관/detail"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 전체 리스트 조회 컨트롤러")
    void theaterVerifyAllController() throws Exception {

        when(theaterService.verifyAll(any())).thenReturn(pageableList);

        mockMvc.perform(get("/theaters"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("상영관 전체 리스트 조회 컨트롤러 실패 - url 경로 틀림")
    void theaterVerifyAllControllerFail() throws Exception {

        when(theaterService.verifyAll(any())).thenReturn(pageableList);

        mockMvc.perform(get("/theater"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andDo(print());
    }
}