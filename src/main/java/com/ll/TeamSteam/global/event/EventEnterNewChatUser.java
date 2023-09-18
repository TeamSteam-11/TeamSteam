package com.ll.TeamSteam.global.event;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.user.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventEnterNewChatUser extends ApplicationEvent {

    private final User enterChatUser;
    private final User inChatUser;
    private final ChatRoom chatRoom;


    public EventEnterNewChatUser(Object source, ChatRoom chatRoom, User inChatUser, User enterChatUser) {
        super(source);
        this.enterChatUser = enterChatUser;
        this.inChatUser = inChatUser;
        this.chatRoom = chatRoom;
    }
}
