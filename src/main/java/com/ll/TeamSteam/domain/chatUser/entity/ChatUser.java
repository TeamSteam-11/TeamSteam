package com.ll.TeamSteam.domain.chatUser.entity;

import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage;
import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.ll.TeamSteam.domain.chatUser.entity.ChatUserType.COMMON;
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
    private ChatUserType type = COMMON;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<ChatMessage> chatMessages = new ArrayList<>();
}
