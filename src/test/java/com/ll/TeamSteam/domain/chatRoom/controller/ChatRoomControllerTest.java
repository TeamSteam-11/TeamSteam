package com.ll.TeamSteam.domain.chatRoom.controller;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.service.ChatRoomService;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUser;
import com.ll.TeamSteam.domain.chatUser.service.ChatUserService;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.service.MatchingService;
import com.ll.TeamSteam.domain.matchingPartner.service.MatchingPartnerService;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.security.SecurityUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static com.ll.TeamSteam.domain.chatUser.entity.ChatUserType.EXIT;
import static com.ll.TeamSteam.domain.chatUser.entity.ChatUserType.KICKED;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

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
    @Autowired
    private UserService userService;
    @Autowired
    private MatchingPartnerService matchingPartnerService;
    @Autowired
    private MatchingService matchingService;
    @Autowired
    private ChatUserService chatUserService;

    @Test
    @DisplayName("매칭 방 생성 시 자동으로 채팅방 생성")
    void testCreateChatRoomWithAuthorization() throws Exception  {
        User currentUser = userService.findByIdElseThrow(1L);

        Matching matching = matchingService.create(currentUser, "제목", "내용", GenreTagType.valueOf("삼인칭슈팅"), 41000, "성별무관", 4L, 20, 21, LocalDateTime.now().plusHours(3));

        ChatRoom chatRoom = chatRoomService.createAndConnect("제목", matching, currentUser.getId());

        assertTrue(chatRoom != null);  // 채팅방이 null이 아닌지 확인
        assertTrue(chatRoom.getMatching() == matching);
    }

    @Test
    @DisplayName("권한이 없는 유저가 채팅방 삭제 시 에러 페이지로 넘어가기")
    void testRemoveChatRoomWithoutAuthorization() throws Exception {
        Long invalidRoomId = 1L;
        Long invalidOwnerId = 5L;

        assertThrows(IllegalArgumentException.class, () -> chatRoomService.remove(invalidRoomId, invalidOwnerId));
    }

    @Test
    @DisplayName("채팅방을 나갈 시 유저의 Type이 EXIT로 수정됨")
    void testExitChatRoomAndUserTypeChange() throws Exception {
        Long validRoomId = 2L;
        Long validUserId = 2L;

        User user = userService.findByIdElseThrow(validUserId);

        matchingPartnerService.addPartner(validRoomId, validUserId);
        matchingPartnerService.updateTrue(validRoomId, validUserId);

        ChatRoom chatRoom = chatRoomService.findById(validRoomId);

        chatRoomService.getByIdAndUserId(validRoomId, validUserId); // 채팅방에 참가하는 로직

        chatRoomService.exitChatRoom(validRoomId, validUserId);
        ChatUser chatUser = chatRoomService.findChatUserByUserId(chatRoom, validUserId);

        assertEquals(EXIT, chatUser.getType());
    }

    @Test
    @DisplayName("유저를 강퇴했을 때, 유저 타입이 KICKED로 바뀌는지 확인")
    void testKickChatUserAndCheckType() throws Exception {
        Long validRoomId = 1L;
        Long validUserId = 2L; // 강퇴당하는 유저
        SecurityUser user = new SecurityUser(1L, "user2", "8888888888"); // 방장

        // 게스트 매칭 파트너
        matchingPartnerService.addPartner(validRoomId, validUserId);
        matchingPartnerService.updateTrue(validRoomId, validUserId);
        matchingPartnerService.addPartner(validRoomId, user.getId());
        matchingPartnerService.updateTrue(validRoomId, user.getId());

        chatRoomService.getByIdAndUserId(validRoomId, validUserId);

        ChatRoom chatRoom = chatRoomService.findById(validRoomId);

        User kickedUser = userService.findById(validUserId).orElseThrow();

        ChatUser chatUserKicked = chatUserService.findByChatRoomAndUser(chatRoom, kickedUser);

        chatRoomService.kickChatUser(validRoomId, chatUserKicked.getId(), user);

        ChatUser chatUser = chatRoomService.findChatUserByUserId(chatRoom, validUserId);

        assertEquals(KICKED, chatUser.getType());
    }

    @Test
    @DisplayName("권한이 없는 사용자가 강퇴하였을 경우 Exception 처리")
    void testKickChatUserAndCheckTypeWithoutAuthorization() throws Exception {
        Long validRoomId = 1L;
        Long validUserId = 3L; // 강퇴당하는 유저
        SecurityUser user = new SecurityUser(2L, "user2", "8888888888"); // 방장

        // 게스트 매칭 파트너
        matchingPartnerService.addPartner(validRoomId, validUserId);
        matchingPartnerService.updateTrue(validRoomId, validUserId);
        matchingPartnerService.addPartner(validRoomId, user.getId());
        matchingPartnerService.updateTrue(validRoomId, user.getId());

        chatRoomService.getByIdAndUserId(validRoomId, validUserId);

        ChatRoom chatRoom = chatRoomService.findById(validRoomId);

        User kickedUser = userService.findById(validUserId).orElseThrow();

        ChatUser chatUserKicked = chatUserService.findByChatRoomAndUser(chatRoom, kickedUser);

        assertThrows(IllegalArgumentException.class, () -> chatRoomService.kickChatUser(validRoomId, chatUserKicked.getId(), user));
    }

    @Test
    @DisplayName("파트너로 등록되지 않은 유저가 채팅방에 입장하려고 하면 exception")
    void testNoPartnerIllegalException() throws Exception {
        Long validRoomId = 1L;
        Long validUserId = 2L;

        User currentUser = userService.findByIdElseThrow(validUserId);
        ChatRoom chatRoom = chatRoomService.findById(validRoomId);

        assertThrows(IllegalArgumentException.class, () -> chatRoomService.getByIdAndUserId(chatRoom.getId(), currentUser.getId()));
    }

    @Test
    @DisplayName("채팅방에 입장하지 않은 유저가 채팅방을 확인하려고 하면 예외처리 성공")
    void testNoChatRoomUserInviteAnotherUser() throws Exception {
        Long validRoomId = 1L;
        Long validUserId = 3L;
        SecurityUser user = new SecurityUser(4L, "user4", "7777777"); // 방장

        ChatRoom chatRoom = chatRoomService.findById(validRoomId);
        User currentUser = userService.findByIdElseThrow(validUserId);

        assertThrows(IllegalArgumentException.class, () -> chatRoomService.inviteUser(chatRoom.getId(), user, currentUser.getId()));
    }

}