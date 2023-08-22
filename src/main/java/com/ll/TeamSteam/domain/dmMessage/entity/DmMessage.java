package com.ll.TeamSteam.domain.dmMessage.entity;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@RequiredArgsConstructor
@Document(collection = "dm")
@SuperBuilder
@Slf4j
public class DmMessage {

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
}
