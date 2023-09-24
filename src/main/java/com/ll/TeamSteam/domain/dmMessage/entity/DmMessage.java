package com.ll.TeamSteam.domain.dmMessage.entity;

import com.ll.TeamSteam.domain.chatMessage.error.ChatMessageOverContent;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Document(collection = "dm")
@SuperBuilder
@Slf4j
public class DmMessage {

    private static final int MAX_LENGTH = 250;

    @Id
    private String id;
    private String content; // 내용

    // senderUsername, senderAvatar, senderId
    private Long senderId;
    private String senderUsername;
    private String senderAvatar;

    // dmId
    private Long dmId;

    private LocalDateTime createdDate;

    public static DmMessage create(String content, Long senderId, String senderUsername, String senderAvatar, Long dmId) {

        DmMessage dmMessage = DmMessage.builder()
                .content(content)
                .senderId(senderId)
                .senderUsername(senderUsername)
                .senderAvatar(senderAvatar)
                .dmId(dmId)
                .createdDate(LocalDateTime.now())
                .build();

        return dmMessage;
    }

    public void validateLength(String content) {
        if (content.length() > MAX_LENGTH) {
            throw new ChatMessageOverContent("메시지 content 초과했어!");
        }
    }
}
