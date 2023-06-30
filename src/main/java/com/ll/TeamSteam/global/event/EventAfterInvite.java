package com.ll.TeamSteam.global.event;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.user.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterInvite extends ApplicationEvent {
    private final User invitingUser;
    private final User invitedUser;
    private final ChatRoom chatRoom;

    public EventAfterInvite(Object source, User invitingUser, User invitedUser, ChatRoom chatRoom) {
        super(source);
        this.invitingUser = invitingUser;
        this.invitedUser = invitedUser;
        this.chatRoom = chatRoom;
    }
}