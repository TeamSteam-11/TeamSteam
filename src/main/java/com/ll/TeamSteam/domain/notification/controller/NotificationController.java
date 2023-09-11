package com.ll.TeamSteam.domain.notification.controller;

import com.ll.TeamSteam.domain.matchingPartner.service.MatchingPartnerService;
import com.ll.TeamSteam.domain.notification.entity.Notification;
import com.ll.TeamSteam.domain.notification.service.NotificationService;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    public String showList(Model model, @AuthenticationPrincipal SecurityUser user,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "createDate") String sortCode,
                           @RequestParam(defaultValue = "DESC") String direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortCode));

        Long currentUserId = user.getId();
        User currentUser = userService.findByIdElseThrow(currentUserId);

        Page<Notification> notifications = notificationService.findByInvitedUser(currentUser, pageable);

        if(page != 0 && page > notifications.getTotalPages() - 1) {
            page = notifications.getTotalPages() - 1;
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortCode));
            notifications = notificationService.findByInvitedUser(currentUser, pageable);
        }

        notificationService.markAsRead(currentUser, pageable);

        model.addAttribute("notifications", notifications);
        model.addAttribute("currentPage", page);

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
        Long roomId = notification.getRoomId();

        notificationService.deleteNotification(user.getId(), notificationId);

        if(roomId == null) {
            userService.addFriends(invitedUserId, invitingUserId);

            return "redirect:/notification/list";
        }

        boolean alreadyWithPartner = matchingPartnerService.isDuplicatedMatchingPartner(roomId, invitedUserId);

        if (alreadyWithPartner) {
            throw new IllegalArgumentException("너 이미 매칭파트너에 참여중이야");
        }

        matchingPartnerService.addPartner(roomId, invitedUserId);

        matchingPartnerService.updateTrue(roomId, invitedUserId);

        return String.format("redirect:/chat/rooms/%d", roomId);
    }
}
