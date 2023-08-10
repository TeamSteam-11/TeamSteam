package com.ll.TeamSteam.domain.chatRoom.controller;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.service.ChatRoomService;
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

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class ChatRoomControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ChatRoomService chatRoomService;

    @Test
    @DisplayName("회원인 유저는 채팅방에 입장 가능")
    @WithMockUser("user1")
    void createChatRoom() throws Exception {

        ResultActions resultActions = mvc.perform(get("/chat/rooms/1"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ChatRoomController.class))
                .andExpect(handler().methodName("enterChatRoom"))
                .andExpect(status().isOk());

        ChatRoom chatRoom = chatRoomService.findByRoomId(1L);
        Long commonParticipantsCount = chatRoomService.getCommonParticipantsCount(chatRoom);

        assertThat(commonParticipantsCount).isEqualTo(1L);
    }

    @Test
    @DisplayName("회원가입하지 않으면 매칭에 들어갈 수 없고 redirect 됨.")
    void NoInChatRoom() throws Exception {
        ResultActions resultActions = mvc.perform(get("/chat/rooms/1"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ChatRoomController.class))
                .andExpect(handler().methodName("enterChatRoom"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("채팅방 방장은 채팅방을 삭제할 수 있다.")
    @WithMockUser("user1")
    void removeChatRoom() throws Exception {
        ResultActions resultActions = mvc.perform(post("/chat/rooms/1")
                        .with(csrf()))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ChatRoomController.class))
                .andExpect(handler().methodName("removeChatRoom"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("채팅방 유저는 채팅방을 나갈 수 있다.")
    @WithMockUser("user2")
    void exitChatRoom() throws Exception {
        ResultActions resultActions = mvc.perform(delete("/chat/rooms/1")
                        .with(csrf()))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ChatRoomController.class))
                .andExpect(handler().methodName("exitChatRoom"))
                .andExpect(status().isOk());
    }
}