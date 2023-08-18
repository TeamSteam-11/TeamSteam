package com.ll.TeamSteam.domain.dmMessage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ll.TeamSteam.domain.dmMessage.entity.DmMessage;
import com.ll.TeamSteam.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DmMessageDto {

    @JsonProperty("message_id")
    private String id;

    @JsonProperty("content")
    private String content;

    @JsonProperty("senderId")
    private Long senderId;
    @JsonProperty("senderUsername")
    private String senderUsername;
    @JsonProperty("senderAvatar")
    private String senderAvatar;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public static DmMessageDto fromDmMessage(DmMessage dmMessage) {

        Long senderId = dmMessage.getSenderId();
        String senderUsername = dmMessage.getSenderUsername();
        String senderAvatar = dmMessage.getSenderAvatar();


//        UserDto userDto = UserDto.fromUser(dmMessage.getSender().getUser());

        DmMessageDto dmMessageDto = DmMessageDto.builder()
                .id(dmMessage.getId())
                .senderId(senderId)
                .senderUsername(senderUsername)
                .senderAvatar(senderAvatar)
                .content(dmMessage.getContent())
                .createdAt(dmMessage.getCreatedDate())
                .build();

        return dmMessageDto;
    }

    public static List<DmMessageDto> fromDmMessages(List<DmMessage> messages) {
        return messages.stream()
                .map(DmMessageDto::fromDmMessage)
                .toList();
    }
}
