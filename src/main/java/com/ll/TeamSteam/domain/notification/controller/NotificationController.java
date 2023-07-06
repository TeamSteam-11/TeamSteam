package com.ll.TeamSteam.domain.notification.controller;

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

    private final UserController userController;

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

        Long InvitedUserId = user.getId();
        Long InvitingUserId = notification.getInvitingUser().getId();
        log.info("InvitingUserId = {}", InvitingUserId);
        log.info("InvitedUserId = {}", InvitedUserId);
        Long roomId = notification.getRoomId();

        notificationService.deleteNotification(user.getId(), notificationId);

        if(roomId == null) {
            userService.addFriends(InvitedUserId, InvitingUserId);

            return "redirect:/notification/list";
        }

        return String.format("redirect:/chat/rooms/%d", roomId);
    }

    public void friendRequest(User targetUser, User loginedUser){
        notificationService.makeFriend(targetUser, loginedUser);
    }

}
