package com.ll.TeamSteam.domain.dm.controller;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.exception.NotInChatRoomException;
import com.ll.TeamSteam.domain.chatRoom.service.ChatRoomService;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUser;
import com.ll.TeamSteam.domain.chatUser.service.ChatUserService;
import com.ll.TeamSteam.domain.dm.entity.Dm;
import com.ll.TeamSteam.domain.dm.service.DmService;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/dm")
public class DmController {

    private final DmService dmService;
    private final UserService userService;
    private final ChatRoomService chatRoomService;

    /**
     * 방 생성 및 입장
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/room/receiver/{receiverId}")
    public String createAndEnterDm(@PathVariable Long receiverId, @AuthenticationPrincipal SecurityUser user) {

        User sender = userService.findByIdElseThrow(user.getId());
        User receiver = userService.findByIdElseThrow(receiverId);

        if(user.getId() == receiverId) {
            throw new IllegalArgumentException("본인한테는 안 돼");
        }

        Optional<Dm> dm = dmService.findByDmSenderAndDmReceiver(sender, receiver);
        Optional<Dm> dmCrossCheck = dmService.findByDmSenderAndDmReceiver(receiver, sender);

        if(dm.isPresent()) {
            return String.format("redirect:/dm/room/%d", dm.get().getId());
        }

        if (dmCrossCheck.isPresent()) {
            return String.format("redirect:/dm/room/%d", dmCrossCheck.get().getId());
        }

        Dm newDm = dmService.createDmAndEnterDm(receiverId, user.getId());

        return String.format("redirect:/dm/room/%d", newDm.getId());
    }

    /**
     * 방 입장
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/room/{dmId}")
    public String enterDm(@PathVariable Long dmId, Model model, @AuthenticationPrincipal SecurityUser user) {

        Dm dm = dmService.findByDmId(dmId);

        if(!dm.getDmSender().getId().equals(user.getId()) && !dm.getDmReceiver().getId().equals(user.getId())) {
            throw new IllegalArgumentException("돌아가라");
        }

        dmService.validEnterDm(dm.getId(), user.getId());

        model.addAttribute("dm", dm);
        model.addAttribute("user", user);

        return "dm/dmRoom";
    }

    /**
    * 참여 중인 매칭 목록
    */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/chatlist")
    public String chatList(Model model, @AuthenticationPrincipal SecurityUser user) {

        // TODO : 상의하기(알림 채팅 목록처럼 로그인해야 아이콘 뜨게 하기 & @PreAuthorize("isAuthenticated()")에 걸리면 현재 Security 로그인 창이 뜨는데 해당 부분 수정하는 게 어떤지)
        // TODO : 로직 정리
        // TODO : 원래는 테이블이 비어있으면 테이블 헤드 안 뜨고 '참여 중인 채팅이 없습니다' 뜨게 하고 싶었는데 생각되로 잘 안 돼서 테이블 바디에 넣음 -> 의견 나눠보기

        // userId로 chatUser를 받아와서 chatRoom 받아오기 (chatUserType이 COMMON인 방만 받아오기)
        List<ChatRoom> myChatRoomList = chatRoomService.findChatRoomByUserId(user.getId());

        // userId로 dmUser를 받아와서 Dm 받아오기
        List<Dm> myDmList = dmService.findByDmSenderIdOrDmReceiverId(user.getId(), user.getId());

        model.addAttribute("myChatRoomList", myChatRoomList);
        model.addAttribute("myDmList", myDmList);
        model.addAttribute("user", user);

        return "dm/chatList";
    }
}
