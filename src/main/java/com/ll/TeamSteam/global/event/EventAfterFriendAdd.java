package com.ll.TeamSteam.global.event;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.user.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterFriendAdd extends ApplicationEvent {
    private final User invitingUser;
    private final User invitedUser;

    public EventAfterFriendAdd(Object source, User invitingUser, User invitedUser) {
        super(source);
        this.invitingUser = invitingUser;
        this.invitedUser = invitedUser;
    }
}