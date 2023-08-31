package com.ll.TeamSteam.domain.notification.eventListener;

import com.ll.TeamSteam.domain.notification.service.NotificationService;
import com.ll.TeamSteam.global.event.EventAfterFriendAdd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationEventListenerFriendAdd {

    private final NotificationService notificationService;

    @EventListener
    public void listen(EventAfterFriendAdd event) {

        notificationService.makeFriend(event.getInvitingUser(), event.getInvitedUser());
    }
}
