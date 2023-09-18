package com.ll.TeamSteam.global.event;

import com.ll.TeamSteam.domain.dm.entity.Dm;
import com.ll.TeamSteam.domain.user.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterCreateDm extends ApplicationEvent {
    private final Dm dm;
    private final User dmSender;
    private final User receiver;

    public EventAfterCreateDm(Object source, Dm dm, User dmSender, User receiver) {
        super(source);
        this.dm = dm;
        this.dmSender = dmSender;
        this.receiver = receiver;
    }
}
