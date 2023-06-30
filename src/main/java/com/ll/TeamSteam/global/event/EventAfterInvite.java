package com.ll.TeamSteam.global.event;

import com.ll.TeamSteam.domain.user.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterInvite extends ApplicationEvent {
    private final User user;

    public EventAfterInvite(Object source, User user) {
        super(source);
        this.user = user;

    }
}