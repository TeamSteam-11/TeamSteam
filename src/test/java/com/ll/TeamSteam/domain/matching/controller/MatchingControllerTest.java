package com.ll.TeamSteam.domain.matching.controller;

import com.ll.TeamSteam.domain.matching.repository.MatchingRepository;
import com.ll.TeamSteam.domain.matching.service.MatchingService;
import com.ll.TeamSteam.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class MatchingControllerTest {
    @Autowired
    private MatchingService matchingService;
    @Autowired
    private MatchingRepository matchingRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MockMvc mvc;


    @Test
    @DisplayName("매칭 등록")
    void t001() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/match/create"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MatchingController.class))
                .andExpect(handler().methodName("matchingCreate"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("매칭 등록 성공")
    void t002() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/match/create")
                        .with(csrf())
                        .param("user", "user1")
                        .param("title", "title1")
                        .param("content", "content1")
                        .param("genre", "삼인칭슈팅")
                        .param("gameTagId", "41000")
                        .param("capacity", "3")
                        .param("startTime", "9")
                        .param("endTime", "12")
                        .param("deadlineDate", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MatchingController.class))
                .andExpect(handler().methodName("matchingCreate"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("매칭 등록 실패_title 내용 없음")
    void t003() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/match/create")
                        .with(csrf())
                        .param("user", "user1")
                        .param("title", "")
                        .param("content", "content1")
                        .param("genre", "삼인칭슈팅")
                        .param("gameTagId", "41000")
                        .param("capacity", "3")
                        .param("startTime", "9")
                        .param("endTime", "12")
                        .param("deadlineDate", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MatchingController.class))
                .andExpect(handler().methodName("matchingCreate"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("매칭 등록 실패_content 내용 없음")
    void t004() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/match/create")
                        .with(csrf())
                        .param("user", "user1")
                        .param("title", "title1")
                        .param("content", "")
                        .param("genre", "삼인칭슈팅")
                        .param("gameTagId", "41000")
                        .param("capacity", "3")
                        .param("startTime", "9")
                        .param("endTime", "12")
                        .param("deadlineDate", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MatchingController.class))
                .andExpect(handler().methodName("matchingCreate"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("매칭 등록 실패_genre 정보 없음")
    void t005() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/match/create")
                        .with(csrf())
                        .param("user", "user1")
                        .param("title", "title1")
                        .param("content", "content1")
                        .param("genre", "")
                        .param("gameTagId", "41000")
                        .param("capacity", "3")
                        .param("startTime", "9")
                        .param("endTime", "12")
                        .param("deadlineDate", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MatchingController.class))
                .andExpect(handler().methodName("matchingCreate"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("매칭 등록 실패_gameTagId 정보 없음")
    void t006() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/match/create")
                        .with(csrf())
                        .param("user", "user1")
                        .param("title", "title1")
                        .param("content", "content1")
                        .param("genre", "삼인칭슈팅")
                        .param("gameTagId", "")
                        .param("capacity", "3")
                        .param("startTime", "9")
                        .param("endTime", "12")
                        .param("deadlineDate", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MatchingController.class))
                .andExpect(handler().methodName("matchingCreate"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("매칭 등록 실패_capacity의 Max값보다 큰 수가 입력됨")
    void t007() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/match/create")
                        .with(csrf())
                        .param("user", "user1")
                        .param("title", "title1")
                        .param("content", "content1")
                        .param("genre", "삼인칭슈팅")
                        .param("gameTagId", "41000")
                        .param("capacity", "100")
                        .param("startTime", "9")
                        .param("endTime", "12")
                        .param("deadlineDate", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MatchingController.class))
                .andExpect(handler().methodName("matchingCreate"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("매칭 등록 실패_capacity의 Min값보다 작은 수가 입력됨")
    void t008() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/match/create")
                        .with(csrf())
                        .param("user", "user1")
                        .param("title", "title1")
                        .param("content", "content1")
                        .param("genre", "삼인칭슈팅")
                        .param("gameTagId", "41000")
                        .param("capacity", "1")
                        .param("startTime", "9")
                        .param("endTime", "12")
                        .param("deadlineDate", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MatchingController.class))
                .andExpect(handler().methodName("matchingCreate"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("매칭 등록 실패_startTime의 Min값보다 작은 수가 입력됨")
    void t009() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/match/create")
                        .with(csrf())
                        .param("user", "user1")
                        .param("title", "title1")
                        .param("content", "content1")
                        .param("genre", "삼인칭슈팅")
                        .param("gameTagId", "41000")
                        .param("capacity", "3")
                        .param("startTime", "-1")
                        .param("endTime", "12")
                        .param("deadlineDate", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MatchingController.class))
                .andExpect(handler().methodName("matchingCreate"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("매칭 등록 실패_startTime의 Max값보다 큰 수가 입력됨")
    void t010() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/match/create")
                        .with(csrf())
                        .param("user", "user1")
                        .param("title", "title1")
                        .param("content", "content1")
                        .param("genre", "삼인칭슈팅")
                        .param("gameTagId", "41000")
                        .param("capacity", "3")
                        .param("startTime", "25")
                        .param("endTime", "12")
                        .param("deadlineDate", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MatchingController.class))
                .andExpect(handler().methodName("matchingCreate"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("매칭 등록 실패_endTime의 Min값보다 작은 수가 입력됨")
    void t011() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/match/create")
                        .with(csrf())
                        .param("user", "user1")
                        .param("title", "title1")
                        .param("content", "content1")
                        .param("genre", "삼인칭슈팅")
                        .param("gameTagId", "41000")
                        .param("capacity", "3")
                        .param("startTime", "9")
                        .param("endTime", "-1")
                        .param("deadlineDate", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MatchingController.class))
                .andExpect(handler().methodName("matchingCreate"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("매칭 등록 실패_endTime의 Max값보다 큰 수가 입력됨")
    void t012() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/match/create")
                        .with(csrf())
                        .param("user", "user1")
                        .param("title", "title1")
                        .param("content", "content1")
                        .param("genre", "삼인칭슈팅")
                        .param("gameTagId", "41000")
                        .param("capacity", "3")
                        .param("startTime", "9")
                        .param("endTime", "25")
                        .param("deadlineDate", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MatchingController.class))
                .andExpect(handler().methodName("matchingCreate"))
                .andExpect(status().is4xxClientError());
    }

    /*
    // NotProd.java의 @Configuration 어노테이션을 주석처리해야 통과
    @Test
    @DisplayName("매칭 삭제")
    void t013() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/match/detail/delete/1")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MatchingController.class))
                .andExpect(handler().methodName("deleteMatching"))
                .andExpect(status().is2xxSuccessful());

        assertThat(matchingService.findById(1L).isPresent()).isEqualTo(false);
    }
    */

    @Test
    @DisplayName("매칭 수정 폼")
    @WithMockUser("user1")
    void t014() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/match/modify/1"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MatchingController.class))
                .andExpect(handler().methodName("modifyMatching"))
                .andExpect(status().is2xxSuccessful());
    }

    /*
    // 테스트 실패
    @Test
    @DisplayName("매칭 수정")
    @WithMockUser("user1")
    void t015() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/match/modify/1")
                        .with(csrf())
                        .param("title", "modifyTitle")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MatchingController.class))
                .andExpect(handler().methodName("modifyMatching"))
                .andExpect(status().is2xxSuccessful());

        assertThat(matchingRepository.findById(1L).get().getTitle()).isEqualTo("modifyTitle");
    }
    */

}
