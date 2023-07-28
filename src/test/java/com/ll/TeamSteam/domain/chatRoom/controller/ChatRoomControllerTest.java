package com.ll.TeamSteam.domain.chatRoom.controller;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.exception.CanNotEnterException;
import com.ll.TeamSteam.domain.chatRoom.exception.NoChatRoomException;
import com.ll.TeamSteam.domain.chatRoom.exception.NotInChatRoomException;
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
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.ll.TeamSteam.domain.chatUser.entity.ChatUserType.*;
import static net.bytebuddy.matcher.ElementMatchers.is;
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
    @DisplayName("매칭 방 생성 시 자동으로 채팅방 생성 성공")
    void testCreateChatRoomWithAuthorization() throws Exception  {
        User currentUser = userService.findByIdElseThrow(1L);

        Matching matching = matchingService.create(currentUser, "제목", "내용", GenreTagType.valueOf("삼인칭슈팅"), 41000, "성별무관", 4L, 20, 21, LocalDateTime.now().plusHours(3));

        ChatRoom chatRoom = chatRoomService.createChatRoomAndConnectMatching("제목", matching, currentUser.getId());

        assertTrue(chatRoom != null);  // 채팅방이 null이 아닌지 확인
        assertTrue(chatRoom.getMatching() == matching);
    }

    @Test
    @DisplayName("권한이 없는 유저가 채팅방 삭제 시 에러 페이지로 넘어가기")
    void testRemoveChatRoomWithoutAuthorization() throws Exception {
        ChatRoom chatRoom = chatRoomService.findByRoomId(1L);
        User user = userService.findByIdElseThrow(5L);

        assertThrows(IllegalArgumentException.class, () -> chatRoomService.validRemoveChatRoom(chatRoom.getId(), user.getId()));
    }

    @Test
    @DisplayName("권한이 있는 유저가 채팅방 삭제 성공")
    void testRemoveChatRoomWithAuthorization() throws Exception {
        ChatRoom chatRoom = chatRoomService.findByRoomId(1L);
        User ownerId = userService.findByIdElseThrow(1L);

        chatRoomService.validRemoveChatRoom(chatRoom.getId(), ownerId.getId());

        // 삭제 후 채팅방 조회하였을 경우 NoChatRoomException이 나와야 한다.
        assertThrows(NoChatRoomException.class, () -> chatRoomService.findByRoomId(chatRoom.getId()));
    }

    @Test
    @DisplayName("채팅방을 나갈 시 유저의 Type이 EXIT로 수정됨")
    void testExitChatRoomAndUserTypeChange() throws Exception {
        ChatRoom chatRoom = chatRoomService.findByRoomId(2L);
        User user = userService.findByIdElseThrow(2L);

        matchingPartnerService.addPartner(chatRoom.getId(), user.getId());
        matchingPartnerService.updateTrue(chatRoom.getId(), user.getId());

        chatRoomService.validEnterChatRoom(chatRoom.getId(), user.getId()); // 채팅방에 참가하는 로직

        chatRoomService.validExitChatRoom(chatRoom.getId(), user.getId());
        ChatUser chatUser = chatUserService.findChatUserByUserId(chatRoom, user.getId());

        assertEquals(EXIT, chatUser.getType());
    }

    @Test
    @DisplayName("방장이 채팅방을 나가려고 하면 예외처리")
    void testExitOwnerChatRoom() throws Exception {
        ChatRoom chatRoom = chatRoomService.findByRoomId(1L);
        User user = userService.findByIdElseThrow(1L);

        matchingPartnerService.addPartner(chatRoom.getId(), user.getId());
        matchingPartnerService.updateTrue(chatRoom.getId(), user.getId());

        chatRoomService.validEnterChatRoom(chatRoom.getId(), user.getId()); // 채팅방에 참가하는 로직

        assertThrows(IllegalArgumentException.class, () -> chatRoomService.validExitChatRoom(chatRoom.getId(), user.getId()));
    }


    @Test
    @DisplayName("유저를 강퇴했을 때, 유저 타입이 KICKED로 바뀌는지 확인")
    void testKickChatUserAndCheckType() throws Exception {
        ChatRoom chatRoom = chatRoomService.findByRoomId(1L);
        User KickedUser = userService.findByIdElseThrow(2L);
        SecurityUser user = new SecurityUser(1L, "user2", "8888888888"); // 방장

        // 게스트 매칭 파트너
        matchingPartnerService.addPartner(chatRoom.getId(), KickedUser.getId());
        matchingPartnerService.updateTrue(chatRoom.getId(), KickedUser.getId());
        matchingPartnerService.addPartner(chatRoom.getId(), user.getId());
        matchingPartnerService.updateTrue(chatRoom.getId(), user.getId());

        chatRoomService.validEnterChatRoom(chatRoom.getId(), KickedUser.getId());

        User kickedUser = userService.findById(KickedUser.getId()).orElseThrow();

        ChatUser chatUserKicked = chatUserService.findByChatRoomAndUser(chatRoom, kickedUser);

        chatRoomService.kickChatUserAndChangeType(chatRoom.getId(), chatUserKicked.getId(), user);

        ChatUser chatUser = chatUserService.findChatUserByUserId(chatRoom, KickedUser.getId());

        assertEquals(KICKED, chatUser.getType());
    }

    @Test
    @DisplayName("권한이 없는 사용자가 강퇴하였을 경우 Exception 처리")
    void testKickChatUserAndCheckTypeWithoutAuthorization() throws Exception {
        ChatRoom chatRoom = chatRoomService.findByRoomId(1L);
        User KickedUser = userService.findByIdElseThrow(3L);
        SecurityUser user = new SecurityUser(2L, "user2", "8888888888"); // 방장

        // 게스트 매칭 파트너
        matchingPartnerService.addPartner(chatRoom.getId(), user.getId());
        matchingPartnerService.updateTrue(chatRoom.getId(), user.getId());
        matchingPartnerService.addPartner(chatRoom.getId(), KickedUser.getId());
        matchingPartnerService.updateTrue(chatRoom.getId(), KickedUser.getId());

        chatRoomService.validEnterChatRoom(chatRoom.getId(), KickedUser.getId());

        User kickedUser = userService.findById(KickedUser.getId()).orElseThrow();

        ChatUser chatUserKicked = chatUserService.findByChatRoomAndUser(chatRoom, kickedUser);

        assertThrows(IllegalArgumentException.class, () -> chatRoomService.kickChatUserAndChangeType(chatRoom.getId(), chatUserKicked.getId(), user));
    }

    @Test
    @DisplayName("파트너로 등록되지 않은 유저가 채팅방에 입장하려고 하면 exception")
    void testNoPartnerIllegalException() throws Exception {
        User currentUser = userService.findByIdElseThrow(2L);
        ChatRoom chatRoom = chatRoomService.findByRoomId(1L);

        assertThrows(IllegalArgumentException.class, () -> chatRoomService.validEnterChatRoom(chatRoom.getId(), currentUser.getId()));
    }

    @Test
    @DisplayName("채팅방에 입장하지 않은 유저가 채팅방을 확인하려고 하면 예외처리 성공")
    void testNoChatRoomUserInviteAnotherUser() throws Exception {
        SecurityUser user = new SecurityUser(4L, "user4", "7777777"); // 방장

        ChatRoom chatRoom = chatRoomService.findByRoomId(1L);
        User currentUser = userService.findByIdElseThrow(3L);

        assertThrows(NotInChatRoomException.class, () -> chatRoomService.validInviteChatRoom(chatRoom.getId(), user, currentUser.getId()));
    }

    @Test
    @DisplayName("인원이 가득찬 방에 들어가려고 했을 경우 CanNotEnterException")
    void testEnterFullChatRoom() throws  Exception{
        ChatRoom chatRoom = chatRoomService.findByRoomId(1L);
        Matching matching = matchingService.findById(1L).orElse(null);
        User user = userService.findByIdElseThrow(1L);
        User user2 = userService.findByIdElseThrow(2L);
        User user3 = userService.findByIdElseThrow(3L);

        // 방장과 user2가 매칭 파트너 신청
        matchingPartnerService.addPartner(chatRoom.getId(), user.getId());
        matchingPartnerService.addPartner(chatRoom.getId(), user2.getId());
        matchingPartnerService.addPartner(chatRoom.getId(), user3.getId());
        matchingPartnerService.updateTrue(chatRoom.getId(), user.getId());
        matchingPartnerService.updateTrue(chatRoom.getId(), user2.getId());

        // 방장과 user2가 이미 방에 들어가잇는 시점
        chatRoomService.validEnterChatRoom(chatRoom.getId(), user2.getId());

        chatRoomService.updateChatUserType(chatRoom.getId(), user.getId());
        chatRoomService.updateChatUserType(chatRoom.getId(), user2.getId());

        // user2는 방에 들어갔다가 나간 상태 (EXIT 상태)
        chatRoomService.validExitChatRoom(chatRoom.getId(), user2.getId());

        // user3가 방에 입장
        matchingPartnerService.updateTrue(chatRoom.getId(), user3.getId());
        chatRoomService.validEnterChatRoom(chatRoom.getId(), user3.getId());
        chatRoomService.updateChatUserType(chatRoom.getId(), user3.getId());

        chatRoomService.changeParticipantsCount(chatRoom);

        assertThrows(CanNotEnterException.class, () -> chatRoomService.canAddChatRoomUser(chatRoom, user2.getId(), matching));
    }
}