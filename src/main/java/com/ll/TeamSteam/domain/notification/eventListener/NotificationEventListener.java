package com.ll.TeamSteam.domain.notification.eventListener;

import com.ll.TeamSteam.domain.notification.service.NotificationService;
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



    }
}
