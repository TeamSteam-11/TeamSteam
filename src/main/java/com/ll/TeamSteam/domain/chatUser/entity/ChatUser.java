package com.ll.TeamSteam.domain.chatUser.entity;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static com.ll.TeamSteam.domain.chatUser.entity.ChatUserType.*;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@SuperBuilder
public class ChatUser extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    private User user;

    @ManyToOne(fetch = LAZY)
    private ChatRoom chatRoom;

    @Builder.Default
    @Enumerated(STRING)
    private ChatUserType type = ROOMIN;


    @Builder
    public ChatUser(User user, ChatRoom chatRoom, ChatUserType type) {
        this.user = user;
        this.chatRoom = chatRoom;
        this.type = type;
    }

    public void changeType() {
        this.type = KICKED;
    }

    public void exitType() {
        this.type = EXIT;
    }

    public void changeUserCommonType() {
        this.type = COMMON;
    }
}
