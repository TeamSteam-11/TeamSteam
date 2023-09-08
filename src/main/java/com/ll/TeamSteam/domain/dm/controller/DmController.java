package com.ll.TeamSteam.domain.dm.controller;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.service.ChatRoomService;
import com.ll.TeamSteam.domain.dm.entity.Dm;
import com.ll.TeamSteam.domain.dm.service.DmService;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        dmService.notSelfDm(user.getId(), receiver.getId());

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
        dmService.notEnterThirdPerson(dm, user);

//        if(!dm.getDmSender().getId().equals(user.getId()) && !dm.getDmReceiver().getId().equals(user.getId())) {
//            throw new IllegalArgumentException("돌아가라");
//        }

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
    public String chatList(Model model, @AuthenticationPrincipal SecurityUser user,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "8") int size,
                           @RequestParam(defaultValue = "createDate") String sortCode,
                           @RequestParam(defaultValue = "DESC") String direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortCode));

        List<ChatRoom> myChatRoomList = chatRoomService.findChatRoomByUserId(user.getId());

        List<Dm> myDmList = dmService.findByDmSenderIdOrDmReceiverId(user.getId(), user.getId());

        model.addAttribute("myChatRoomList", myChatRoomList);
        model.addAttribute("myDmList", myDmList);
        model.addAttribute("user", user);

        return "dm/chatList";
    }
}
