package com.ll.TeamSteam.domain.notification.controller;

import com.ll.TeamSteam.domain.matchingPartner.service.MatchingPartnerService;
import com.ll.TeamSteam.domain.notification.entity.Notification;
import com.ll.TeamSteam.domain.notification.service.NotificationService;
import com.ll.TeamSteam.domain.user.controller.UserController;
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

import java.util.List;

@Controller
@RequestMapping("/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;
    private final MatchingPartnerService matchingPartnerService;


    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showList(Model model, @AuthenticationPrincipal SecurityUser user) {
        Long currentUserId = user.getId();
        User currentUser = userService.findByIdElseThrow(currentUserId);

        List<Notification> notifications = notificationService.findByInvitedUser(currentUser);

        notificationService.markAsRead(currentUser);

        model.addAttribute("notifications", notifications);

        return "notification/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{notificationId}/reject")
    public String rejectInvitation(@PathVariable Long notificationId,
                                   @AuthenticationPrincipal SecurityUser user) {
        notificationService.deleteNotification(user.getId(), notificationId);

        return "redirect:/notification/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{notificationId}/accept")
    public String acceptInvitation(@PathVariable Long notificationId,
                                   @AuthenticationPrincipal SecurityUser user) {

        Notification notification = notificationService.findById(notificationId);

        Long invitedUserId = user.getId();
        Long invitingUserId = notification.getInvitingUser().getId();
        log.info("InvitingUserId = {}", invitedUserId);
        log.info("InvitedUserId = {}", invitingUserId);
        Long roomId = notification.getRoomId();

        notificationService.deleteNotification(user.getId(), notificationId);

        if(roomId == null) {
            userService.addFriends(invitedUserId, invitingUserId);

            return "redirect:/notification/list";
        }

        //TODO:여기에 검증 => 초대 받았을 때, 매칭 파트너에 추가 + True로 변경

        // true 면 matching partner에 저장되어있고, false 면 없음
        boolean alreadyWithPartner = matchingPartnerService.isDuplicatedMatchingPartner(roomId, invitedUserId);

        // 이미 저장된 사람은 중복 저장되지 않도록 처리
        if (alreadyWithPartner) {
            throw new IllegalArgumentException("너 이미 매칭파트너에 참여중이야");
        }

        matchingPartnerService.addPartner(roomId, invitedUserId);

        matchingPartnerService.updateTrue(roomId, invitedUserId);

        return String.format("redirect:/chat/rooms/%d", roomId);
    }

}
