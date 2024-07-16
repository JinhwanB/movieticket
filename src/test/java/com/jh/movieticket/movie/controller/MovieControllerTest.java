package com.jh.movieticket.movie.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.movieticket.auth.SecurityConfiguration;
import com.jh.movieticket.auth.TokenProvider;
import com.jh.movieticket.movie.domain.ScreenType;
import com.jh.movieticket.movie.dto.MovieCreateDto;
import com.jh.movieticket.movie.dto.MovieCreateDto.Request;
import com.jh.movieticket.movie.dto.MovieModifyDto;
import com.jh.movieticket.movie.dto.MovieServiceDto;
import com.jh.movieticket.movie.service.MovieService;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(value = {MovieController.class, SecurityConfiguration.class})
class MovieControllerTest {

    MockMvc mockMvc;
    MovieCreateDto.Request createRequest;
    MovieModifyDto.Request modifyRequest;
    MovieServiceDto movieServiceDto;
    MockMultipartFile mockMultipartFile;
    MockMultipartFile badMockMultipartFile;

    @Autowired
    WebApplicationContext context;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MovieService movieService;

    @MockBean
    TokenProvider tokenProvider;

    @BeforeEach
    void before() throws IOException {

        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        List<String> genreList = List.of("genre1", "genre2");
        List<String> actorList = List.of("actor1", "actor2");

        movieServiceDto = MovieServiceDto.builder()
            .title("title")
            .description("description")
            .director("director")
            .screenType("현재 상영작")
            .totalShowTime("100분")
            .releaseDate(LocalDate.now())
            .genreList(genreList)
            .actorList(actorList)
            .posterName("posterName")
            .totalAudienceCnt(0)
            .reservationRate(0)
            .gradeAvg(0)
            .posterUrl("posterUrl")
            .build();

        createRequest = MovieCreateDto.Request.builder()
            .title("title")
            .description("description")
            .director("director")
            .screenType(ScreenType.NOW)
            .totalShowTime("100분")
            .releaseDay(15)
            .releaseMonth(1)
            .releaseYear(2023)
            .genreList(genreList)
            .actorList(actorList)
            .build();

        modifyRequest = MovieModifyDto.Request.builder()
            .title("title2")
            .description("description")
            .director("director")
            .screenType(ScreenType.NOW)
            .totalShowTime("100분")
            .releaseDay(15)
            .releaseMonth(1)
            .releaseYear(2023)
            .genreList(genreList)
            .actorList(actorList)
            .originMovieTitle("title")
            .build();

        String fileName = "스크린샷3.png";
        String contentType = "image/png";
        String filePath = "src/main/resources/" + fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        mockMultipartFile = new MockMultipartFile("multipartFile", fileName, contentType, fileInputStream);

        fileName = "test1.txt";
        contentType = "text/plain";
        filePath = "src/main/resources/" + fileName;
        fileInputStream = new FileInputStream(filePath);
        badMockMultipartFile = new MockMultipartFile("multipartFile", fileName, contentType, fileInputStream);
    }

    @Test
    @DisplayName("영화 생성 컨트롤러")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void movieCreateController() throws Exception {

        when(movieService.createMovie(any(), any())).thenReturn(movieServiceDto);

        mockMvc.perform(multipart("/movies/movie")
            .file(mockMultipartFile)
            .file(new MockMultipartFile("createRequest", "", "application/json", objectMapper.writeValueAsString(createRequest).getBytes(
                StandardCharsets.UTF_8)))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value(201))
            .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("영화 생성 컨트롤러 실패 - 로그인 x")
    void movieCreateControllerFail1() throws Exception {

        mockMvc.perform(multipart("/movies/movie")
                .file(mockMultipartFile)
                .file(new MockMultipartFile("createRequest", "", "application/json", objectMapper.writeValueAsString(createRequest).getBytes(
                    StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("영화 생성 컨트롤러 실패 - 권한 없음")
    @WithMockUser(username = "user", roles = "USER")
    void movieCreateControllerFail2() throws Exception {

        mockMvc.perform(multipart("/movies/movie")
                .file(mockMultipartFile)
                .file(new MockMultipartFile("createRequest", "", "application/json", objectMapper.writeValueAsString(createRequest).getBytes(
                    StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("영화 생성 컨트롤러 실패 - api 경로 틀림")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void movieCreateControllerFail3() throws Exception {

        mockMvc.perform(multipart("/movies/movi")
                .file(mockMultipartFile)
                .file(new MockMultipartFile("createRequest", "", "application/json", objectMapper.writeValueAsString(createRequest).getBytes(
                    StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("영화 생성 컨트롤러 실패 - 허용하지 않는 파일 확장자")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void movieCreateControllerFail4() throws Exception {

        mockMvc.perform(multipart("/movies/movie")
                .file(badMockMultipartFile)
                .file(new MockMultipartFile("createRequest", "", "application/json", objectMapper.writeValueAsString(createRequest).getBytes(
                    StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400));
    }

    @Test
    @DisplayName("영화 생성 컨트롤러 실패 - dto 유효성 검사 실패")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void movieCreateControllerFail5() throws Exception {

        Request badRequest = createRequest.toBuilder()
            .title("")
            .build();

        mockMvc.perform(multipart("/movies/movie")
                .file(badMockMultipartFile)
                .file(new MockMultipartFile("createRequest", "", "application/json", objectMapper.writeValueAsString(badRequest).getBytes(
                    StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400));
    }

    @Test
    @DisplayName("영화 수정 컨트롤러")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void movieModifyController() throws Exception {

        when(movieService.updateMovie(any(), any())).thenReturn(movieServiceDto);

        mockMvc.perform(multipart(HttpMethod.PUT,  "/movies/movie")
            .file(mockMultipartFile)
            .file(new MockMultipartFile("modifyRequest", "", "application/json", objectMapper.writeValueAsString(modifyRequest).getBytes(StandardCharsets.UTF_8)))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("영화 수정 컨트롤러 실패 - 로그인 x")
    void movieModifyControllerFail1() throws Exception {

        mockMvc.perform(multipart(HttpMethod.PUT,  "/movies/movie")
                .file(mockMultipartFile)
                .file(new MockMultipartFile("modifyRequest", "", "application/json", objectMapper.writeValueAsString(modifyRequest).getBytes(StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("영화 수정 컨트롤러 실패 - 권한 없음")
    @WithMockUser(username = "user", roles = "USER")
    void movieModifyControllerFail2() throws Exception {

        mockMvc.perform(multipart(HttpMethod.PUT,  "/movies/movie")
                .file(mockMultipartFile)
                .file(new MockMultipartFile("modifyRequest", "", "application/json", objectMapper.writeValueAsString(modifyRequest).getBytes(StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("영화 수정 컨트롤러 실패 - api 경로 틀림")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void movieModifyControllerFail3() throws Exception {

        mockMvc.perform(multipart(HttpMethod.PUT,  "/movies/movi")
                .file(mockMultipartFile)
                .file(new MockMultipartFile("modifyRequest", "", "application/json", objectMapper.writeValueAsString(modifyRequest).getBytes(StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("영화 수정 컨트롤러 실패 - 이미지가 아닌 다른 파일")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void movieModifyControllerFail4() throws Exception {

        mockMvc.perform(multipart(HttpMethod.PUT,  "/movies/movie")
                .file(badMockMultipartFile)
                .file(new MockMultipartFile("modifyRequest", "", "application/json", objectMapper.writeValueAsString(modifyRequest).getBytes(StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400));
    }

    @Test
    @DisplayName("영화 수정 컨트롤러 실패 - dto 유효성 검사 실패")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void movieModifyControllerFail5() throws Exception {

        MovieModifyDto.Request badRequest = modifyRequest.toBuilder()
            .title("")
            .build();

        mockMvc.perform(multipart(HttpMethod.PUT,  "/movies/movie")
                .file(badMockMultipartFile)
                .file(new MockMultipartFile("modifyRequest", "", "application/json", objectMapper.writeValueAsString(badRequest).getBytes(StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400));
    }
}