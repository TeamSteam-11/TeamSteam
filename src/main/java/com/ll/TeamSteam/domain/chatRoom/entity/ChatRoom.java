package com.ll.TeamSteam.domain.chatRoom.entity;

import com.ll.TeamSteam.domain.chatUser.entity.ChatUser;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
@Slf4j
public class ChatRoom extends BaseEntity {


    private String name;

    @OneToOne(fetch = LAZY, cascade = PERSIST, orphanRemoval = true)
    private Matching matching;

    @ManyToOne(fetch = LAZY)
    private User owner;

    @OneToMany(mappedBy = "chatRoom", cascade = PERSIST, orphanRemoval = true)
    @Builder.Default
    private Set<ChatUser> chatUsers = new HashSet<>();

    public static ChatRoom create(String name, Matching matching, User owner) {

        Assert.notNull(name, "name는 널일 수 없습니다.");
        Assert.notNull(owner, "owner는 널일 수 없습니다.");

        ChatRoom chatRoom = ChatRoom.builder()
                .name(name)
                .matching(matching)
                .owner(owner)
                .build();

        return chatRoom;
    }

    public void addChatUser(User owner) {
        ChatUser chatUser = ChatUser.builder()
                .user(owner)
                .chatRoom(this)
                .build();

        chatUsers.add(chatUser);
    }

    public void updateName(String name){
        this.name = name;
    }

}
