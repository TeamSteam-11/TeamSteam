package com.ll.TeamSteam.domain.notification.eventListener;

import com.ll.TeamSteam.domain.notification.service.NotificationService;
import com.ll.TeamSteam.global.event.EventAfterCreateDm;
import com.ll.TeamSteam.global.event.EventAfterInvite;
import com.ll.TeamSteam.global.event.EventEnterNewChatUser;
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
    String image;

    @EventListener
    public void listen(EventAfterInvite event) {
        image = String.format("https://steamcdn-a.akamaihd.net/steam/apps/%d/header.jpg", event.getChatRoom().getMatching().getGameTagId());
        notificationService.makeLike(event.getInvitingUser(), event.getInvitedUser(), event.getChatRoom().getId(), event.getChatRoom().getName(), image ,false);
    }

    @EventListener
    public void listen(EventAfterCreateDm event){
        image = event.getDmSender().getAvatar();
        notificationService.makeDmCreateAlarm(event.getDm(), event.getDmSender(), event.getReceiver(), image);
    }

    @EventListener
    public void listen(EventEnterNewChatUser event) {
        image = String.format("https://steamcdn-a.akamaihd.net/steam/apps/%d/header.jpg", event.getChatRoom().getMatching().getGameTagId());
        notificationService.makeLike(event.getEnterChatUser(), event.getInChatUser(), event.getChatRoom().getId(), event.getChatRoom().getName(), image, true);
    }
}
