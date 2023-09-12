package com.ll.TeamSteam.domain.notification.eventListener;

import com.ll.TeamSteam.domain.notification.service.NotificationService;
import com.ll.TeamSteam.global.event.EventAfterCreateDm;
import com.ll.TeamSteam.global.event.EventAfterInvite;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void listen(EventAfterInvite event) {

        notificationService.makeLike(event.getInvitingUser(), event.getInvitedUser(), event.getChatRoom().getId(), event.getChatRoom().getName());
    }

    @EventListener
    public void listen(EventAfterCreateDm event){
        notificationService.makeDmCreateAlarm(event.getDm(), event.getDmSender(), event.getReceiver());
    }
}
