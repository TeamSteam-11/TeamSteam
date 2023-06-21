package com.ll.TeamSteam.domain.chatRoom.entity;

import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUser;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class ChatRoom extends BaseEntity {

    private String name;

    @OneToOne(fetch = LAZY)
    private Matching matching;

    @ManyToOne(fetch = LAZY)
    private User owner;

    @OneToMany(mappedBy = "chatRoom",  cascade = PERSIST)
    @Builder.Default
    private Set<ChatUser> chatUsers = new HashSet<>();

    @OneToMany(mappedBy = "chatRoom",  cascade = PERSIST)
    private List<ChatMessage> chatMessages = new ArrayList<>();
}
