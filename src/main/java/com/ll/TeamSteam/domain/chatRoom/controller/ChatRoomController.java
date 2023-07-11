package com.ll.TeamSteam.domain.chatRoom.controller;

import com.ll.TeamSteam.domain.chatMessage.dto.response.SignalResponse;
import com.ll.TeamSteam.domain.chatMessage.service.ChatMessageService;
import com.ll.TeamSteam.domain.chatRoom.dto.ChatRoomDto;
import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.service.ChatRoomService;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUser;
import com.ll.TeamSteam.domain.chatUser.service.ChatUserService;
import com.ll.TeamSteam.domain.friend.entity.Friend;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matchingPartner.service.MatchingPartnerService;
import com.ll.TeamSteam.domain.recentlyUser.service.RecentlyUserService;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.rq.Rq;
import com.ll.TeamSteam.global.rsData.RsData;
import com.ll.TeamSteam.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.ll.TeamSteam.domain.chatMessage.dto.response.SignalType.NEW_MESSAGE;
import static com.ll.TeamSteam.domain.chatMessage.entity.ChatMessageType.ENTER;
import static com.ll.TeamSteam.domain.chatMessage.entity.ChatMessageType.LEAVE;
import static com.ll.TeamSteam.domain.chatUser.entity.ChatUserType.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

    private final Rq rq;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final SimpMessageSendingOperations template;
    private final ChatUserService chatUserService;
    private final UserService userService;

    private final RecentlyUserService recentlyUserService;

    /**
     * 방 조회
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/rooms")
    public String showRooms(Model model) {

        List<ChatRoom> chatRooms = chatRoomService.findAll();

        model.addAttribute("chatRooms", chatRooms);
        return "chat/rooms";
    }

    /**
     * 방 입장
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/rooms/{roomId}")
    public String showRoom(@PathVariable Long roomId, Model model, @AuthenticationPrincipal SecurityUser user) {

        log.info("showRoom SecurityUser = {}", user);
        log.info("user.getName = {}", user.getUsername());
        log.info("user.getId = {}", user.getId());

        ChatRoom chatRoom = chatRoomService.findById(roomId);
        Matching matching = chatRoom.getMatching();

        RsData rsData = chatRoomService.canAddChatRoomUser(chatRoom, user.getId(), matching);

        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }

        ChatRoomDto chatRoomDto = chatRoomService.getByIdAndUserId(roomId, user.getId());

        if (chatRoomDto.getType().equals(ROOMIN) || chatRoomDto.getType().equals(EXIT)){
            // 사용자가 방에 입장할 때 메시지 생성
            String enterMessage = " < " + user.getUsername() + "님이 입장하셨습니다. >";
            chatMessageService.createAndSave(enterMessage, user.getId(), roomId, ENTER);

            // 실시간으로 입장 메시지 전송
            SignalResponse signalResponse = SignalResponse.builder()
                    .type(NEW_MESSAGE)
                    .message(enterMessage)  // 입장 메시지 설정
                    .build();

            template.convertAndSend("/topic/chats/" + chatRoom.getId(), signalResponse);
        }

        chatRoomService.updateChatUserType(roomId, user.getId());
        chatRoomService.changeParticipant(chatRoom);

        model.addAttribute("chatRoom", chatRoomDto);
        model.addAttribute("user", user);

        return "chat/room";
    }

    /**
     * 채팅방 삭제(Owner만 가능)
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/rooms/{roomId}")
    public String removeRoom(@PathVariable Long roomId, @AuthenticationPrincipal SecurityUser user) {
        chatRoomService.remove(roomId, user.getId());
        return "redirect:/match/list";
    }

    /**
     * User가 채팅방 나가기
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/rooms/{roomId}")
    public String exitChatRoom(@PathVariable Long roomId, @AuthenticationPrincipal SecurityUser user){

        ChatRoom chatRoom = chatRoomService.findById(roomId);

        ChatRoomDto chatRoomDto = chatRoomService.getByIdAndUserId(roomId, user.getId());

        if (chatRoomDto.getType().equals(COMMON)){
            // 사용자가 방에서 퇴장할 때 메시지 생성
            String exitMessage = " < " + user.getUsername() + "님이 퇴장하셨습니다. >";
            chatMessageService.createAndSave(exitMessage, user.getId(), roomId, LEAVE);

            // 실시간으로 퇴장 메시지 전송
            SignalResponse signalResponseLeave = SignalResponse.builder()
                    .type(NEW_MESSAGE)
                    .message(exitMessage)  // 퇴장 메시지 설정
                    .build();

            template.convertAndSend("/topic/chats/" + chatRoom.getId(), signalResponseLeave);
        }

        chatRoomService.exitChatRoom(roomId, user.getId());
        chatRoomService.changeParticipant(chatRoom);

        return "redirect:/match/list";
    }

    // 방장이 유저 강퇴시키기
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{roomId}/kick/{chatUserId}")
    public String kickChatUser(@PathVariable Long roomId, @PathVariable Long chatUserId,
                                 @AuthenticationPrincipal SecurityUser user){
        ChatRoom chatRoom = chatRoomService.findById(roomId);
        chatRoomService.kickChatUser(roomId, chatUserId, user);

        return ("redirect:/chat/%d/userList").formatted(chatRoom.getId());
    }

    // 유저 정보 가져오기
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{roomId}/userList")
    public String userList(Model model, @PathVariable Long roomId,
                             @AuthenticationPrincipal SecurityUser user) {
        List<ChatUser> chatUserList = chatUserService.findByChatRoomIdAndChatUser(roomId, user.getId());
        ChatRoom chatRoom = chatRoomService.findById(roomId);

        if (chatUserList == null) {
            return rq.historyBack("해당 방에 참가하지 않았습니다.");
        }

        User currentUser = userService.findById(user.getId()).orElseThrow(null);

        model.addAttribute("chatUserList", chatUserList);
        model.addAttribute("chatRoom", chatRoom);
        model.addAttribute("COMMON", COMMON);
        model.addAttribute("currentUser", currentUser);
        return "chat/userList";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{roomId}/inviteList")
    public String inviteList(Model model, @PathVariable Long roomId, @AuthenticationPrincipal SecurityUser user) {
        List<User> userList = userService.findAll();

        User currentUser = userService.findByIdElseThrow(user.getId());
        List<Friend> friendList = currentUser.getFriendList();
        ChatRoom chatRoom = chatRoomService.findById(roomId);

        log.info("userList = {}", userList);
        model.addAttribute("chatRoom", chatRoom);
        model.addAttribute("user", currentUser);
        model.addAttribute("friendList", friendList);

        return "chat/inviteList";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{roomId}/inviteUser/{userId}")
    public ResponseEntity<Boolean> inviteUser(@PathVariable Long roomId, @AuthenticationPrincipal SecurityUser user,
                                              @PathVariable Long userId, Model model){
        // DB에서 이미 정보가 있는지 없는지 조회
        ChatRoom chatRoom = chatRoomService.findById(roomId);
        boolean alreadyInvited = chatRoomService.isDuplicateInvite(chatRoom.getId(), user.getId(), userId);
        log.info("alreadyInvited = {} ", alreadyInvited);

        model.addAttribute("alreadyInvited", alreadyInvited);

        if (alreadyInvited){
            return ResponseEntity.ok(alreadyInvited);
        }

        // DB 저장
        chatRoomService.inviteUser(roomId, user, userId);

        return ResponseEntity.ok(alreadyInvited);
    }
}
