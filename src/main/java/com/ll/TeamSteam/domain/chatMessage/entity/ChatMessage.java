package com.ll.TeamSteam.domain.chatMessage.entity;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUser;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@SuperBuilder
public class ChatMessage extends BaseEntity {
    private String content; // 내용

    @ManyToOne(fetch = LAZY)
    private ChatUser sender; // 작성자

    @ManyToOne(fetch = LAZY)
    private ChatRoom chatRoom; // 해당 채팅방 룸

    @Enumerated(STRING)
    private ChatMessageType type;
}
