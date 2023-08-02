package com.ll.TeamSteam.domain.dmMessage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage;
import com.ll.TeamSteam.domain.dmMessage.entity.DmMessage;
import com.ll.TeamSteam.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DmMessageDto {

    @JsonProperty("message_id")
    private Long id;

    @JsonProperty("content")
    private String content;

    @JsonProperty("sender")
    private UserDto sender;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public static DmMessageDto fromDmMessage(DmMessage dmMessage) {

        UserDto userDto = UserDto.fromUser(dmMessage.getSender().getUser());

        DmMessageDto dmMessageDto = DmMessageDto.builder()
                .id(dmMessage.getId())
                .sender(userDto)
                .content(dmMessage.getContent())
                .createdAt(dmMessage.getCreateDate())
                .updatedAt(dmMessage.getModifyDate())
                .build();

        return dmMessageDto;
    }

    public static List<DmMessageDto> fromDmMessages(List<DmMessage> messages) {
        return messages.stream()
                .map(DmMessageDto::fromDmMessage)
                .toList();
    }
}
