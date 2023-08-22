package com.ll.TeamSteam.domain.chatMessage.entity;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUser;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Document(collection = "chat")
@SuperBuilder
public class ChatMessage {

    @Id
    private String id;
    private String content; // 내용

    private Long senderId;
    private String senderUsername;
    private String senderAvatar;

    private Long chatRoomId;
    private Long chatId;

    private LocalDateTime createDate;

    @Enumerated(STRING)
    private ChatMessageType type;

//    @Builder
//    public ChatMessage(String content, Long senderId, String senderUsername, String senderAvatar, Long chatRoomId, LocalDateTime createDate) {
//
//        Assert.notNull(content, "content는 널일 수 없습니다.");
//        Assert.notNull(senderId, "senderId는 널일 수 없습니다.");
//        Assert.notNull(chatRoomId, "chatRoomId는 널일 수 없습니다.");
//
//        this.content = content;
//        this.senderId = senderId;
//        this.senderUsername = senderUsername;
//        this.senderAvatar = senderAvatar;
//        this.chatRoomId = chatRoomId;
//        this.createDate = createDate;
//    }

    public static ChatMessage create(String content, Long senderId, String senderUsername, String senderAvatar, ChatMessageType chatMessageType, Long chatRoomId) {

        ChatMessage chatMessage = ChatMessage.builder()
                .content(content)
                .senderId(senderId)
                .senderUsername(senderUsername)
                .senderAvatar(senderAvatar)
                .type(chatMessageType)
                .chatRoomId(chatRoomId)
                .createDate(LocalDateTime.now())
                .build();

        return chatMessage;
    }

    public static ChatMessage create(String content, Long chatRoomId) {

        ChatMessage chatMessage = ChatMessage.builder()
                .content(content)
                .chatRoomId(chatRoomId)
                .build();

        return chatMessage;
    }

    public void removeChatMessages(String content){
        this.content = content;
    }
}

