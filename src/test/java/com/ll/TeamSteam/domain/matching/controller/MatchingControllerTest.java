package com.ll.TeamSteam.domain.matching.controller;

import com.ll.TeamSteam.domain.chatRoom.service.ChatRoomService;
import com.ll.TeamSteam.domain.matching.repository.MatchingRepository;
import com.ll.TeamSteam.domain.matching.service.MatchingService;
import com.ll.TeamSteam.domain.matchingPartner.service.MatchingPartnerService;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MatchingControllerTest {
    @Autowired
    private MatchingRepository matchingRepository;
    @Autowired
    private MatchingController matchingController;
    @Autowired
    private MatchingService matchingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MatchingPartnerService matchingPartnerService;
    @Autowired
    private ChatRoomService chatRoomService;
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
    @DisplayName("매칭 등록 실패_startTime 값이 범위를 벗어남")
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

}
