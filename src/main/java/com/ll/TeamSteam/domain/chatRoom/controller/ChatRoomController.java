package com.ll.TeamSteam.domain.chatRoom.controller;

import com.ll.TeamSteam.domain.chatMessage.service.ChatMessageService;
import com.ll.TeamSteam.domain.chatRoom.dto.ChatRoomDto;
import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.service.ChatRoomService;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUser;
import com.ll.TeamSteam.domain.chatUser.service.ChatUserService;
import com.ll.TeamSteam.domain.dm.entity.Dm;
import com.ll.TeamSteam.domain.friend.entity.Friend;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.recentlyUser.service.RecentlyUserService;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ll.TeamSteam.domain.chatUser.entity.ChatUserType.COMMON;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final ChatUserService chatUserService;
    private final UserService userService;
    private final RecentlyUserService recentlyUserService;

    /**
     * 방 입장
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/rooms/{roomId}")
    public String enterChatRoom(@PathVariable Long roomId, Model model, @AuthenticationPrincipal SecurityUser user) {
        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);
        Matching matching = chatRoom.getMatching();

        chatRoomService.canAddChatRoomUser(chatRoom, user.getId(), matching);

        ChatRoomDto chatRoomDto = chatRoomService.validEnterChatRoom(roomId, user.getId());

        chatMessageService.enterMessage(chatRoomDto, user, roomId);

        chatRoomService.updateChatUserType(roomId, user.getId());
        chatRoomService.changeParticipantsCount(chatRoom);

        recentlyUserService.updateRecentlyUser(user.getId());

        model.addAttribute("chatRoom", chatRoomDto);
        model.addAttribute("user", user);

        return "chat/room";
    }

    /**
     * 채팅방 삭제(Owner만 가능)
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/rooms/{roomId}")
    public String removeChatRoom(@PathVariable Long roomId, @AuthenticationPrincipal SecurityUser user) {
        chatRoomService.validRemoveChatRoom(roomId, user.getId());
        return "redirect:/match/list";
    }

    /**
     * User가 채팅방 나가기
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/rooms/{roomId}")
    public String exitChatRoom(@PathVariable Long roomId, @AuthenticationPrincipal SecurityUser user){

        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);

        ChatRoomDto chatRoomDto = chatRoomService.validEnterChatRoom(roomId, user.getId());

        chatMessageService.leaveMessage(chatRoomDto, user, roomId);

        chatRoomService.validExitChatRoom(roomId, user.getId());
        chatRoomService.changeParticipantsCount(chatRoom);

        return "redirect:/match/list";
    }

    /**
     * 유저 강퇴시키기
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{roomId}/kick/{chatUserId}")
    public String kickChatUser(@PathVariable Long roomId, @PathVariable Long chatUserId,
                                 @AuthenticationPrincipal SecurityUser user){
        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);
        chatRoomService.kickChatUserAndChangeType(roomId, chatUserId, user);

        chatRoomService.changeParticipantsCount(chatRoom);

        return ("redirect:/chat/%d/userList").formatted(chatRoom.getId());
    }

    /**
     * 유저 정보 가져오기
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{roomId}/userList")
    public String chatUserList(Model model, @PathVariable Long roomId,
                             @AuthenticationPrincipal SecurityUser user) {
        List<ChatUser> chatUserList = chatUserService.findByChatRoomIdAndChatUser(roomId, user.getId());
        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);

        User currentUser = userService.findById(user.getId()).orElseThrow(null);

        model.addAttribute("chatUserList", chatUserList);
        model.addAttribute("chatRoom", chatRoom);
        model.addAttribute("COMMON", COMMON);
        model.addAttribute("currentUser", currentUser);
        return "chat/userList";
    }

    /**
     * 채팅방 유저 초대 목록
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{roomId}/inviteList")
    public String friendListForInvitingChatRoom(Model model, @PathVariable Long roomId, @AuthenticationPrincipal SecurityUser user) {
        chatUserService.findByChatRoomIdAndChatUser(roomId, user.getId());

        User currentUser = userService.findByIdElseThrow(user.getId());
        List<Friend> friendList = currentUser.getFriendList();
        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);

        model.addAttribute("chatRoom", chatRoom);
        model.addAttribute("user", currentUser);
        model.addAttribute("friendList", friendList);

        return "chat/inviteList";
    }

    /**
     * 채팅방 유저 초대하기
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{roomId}/inviteUser/{userId}")
    public ResponseEntity<Boolean> inviteChatRoom(@PathVariable Long roomId, @AuthenticationPrincipal SecurityUser user,
                                                  @PathVariable Long userId, Model model){
        // DB에서 이미 정보가 있는지 없는지 조회
        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);
        boolean alreadyInvited = chatRoomService.isDuplicatedInvitation(chatRoom.getId(), user.getId(), userId);

        model.addAttribute("alreadyInvited", alreadyInvited);

        if (alreadyInvited){
            return ResponseEntity.ok(alreadyInvited);
        }

        // DB 저장
        chatRoomService.validInviteChatRoom(roomId, user, userId);

        return ResponseEntity.ok(alreadyInvited);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/chatList")
    public String chatList(Model model, @AuthenticationPrincipal SecurityUser user,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "3") int size) {

        int totalItems = chatRoomService.findChatRoomByUserId(user.getId()).size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        if (page > totalPages - 1) {
            page = totalPages - 1;
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatRoom> myChatRoomList = chatRoomService.findChatRoomByUserId(user.getId(), pageable);

        model.addAttribute("myChatRoomList", myChatRoomList);
        model.addAttribute("currentPage", page);
        model.addAttribute("user", user);

        return "chat/chatList";
    }
}
