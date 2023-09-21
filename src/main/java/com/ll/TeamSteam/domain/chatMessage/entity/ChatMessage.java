package com.ll.TeamSteam.domain.chatMessage.entity;

import com.ll.TeamSteam.domain.chatMessage.error.ChatMessageOverContent;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Document(collection = "chat")
@SuperBuilder
public class ChatMessage {

    private static final int MAX_LENGTH = 250;

    @Id
    private String id;
    @Size(max= 50)
    private String content; // 내용

    private Long senderId;
    private String senderUsername;
    private String senderAvatar;

    private Long chatRoomId;
    private Long chatId;

    private LocalDateTime createDate;

    @Enumerated(STRING)
    private ChatMessageType type;

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

    public void validateLength(String content) {
        if (content.length() > MAX_LENGTH) {
            throw new ChatMessageOverContent("메시지 content 초과했어!");
        }
    }
}

