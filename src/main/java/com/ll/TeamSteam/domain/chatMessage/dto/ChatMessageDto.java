package com.ll.TeamSteam.domain.chatMessage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage;
import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessageType;
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
public class ChatMessageDto implements Serializable {

    @JsonProperty("message_id")
    private Long id;

    @JsonProperty("content")
    private String content;

    @JsonProperty("sender")
    private UserDto sender;

    @JsonProperty("type")
    private ChatMessageType type;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public static ChatMessageDto fromChatMessage(ChatMessage chatMessage) {

        UserDto userDto = UserDto.fromUser(chatMessage.getSender().getUser());

        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .id(chatMessage.getId())
                .type(chatMessage.getType())
                .sender(userDto)
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .createdAt(chatMessage.getCreateDate())
                .updatedAt(chatMessage.getModifyDate())
                .build();

        return chatMessageDto;
    }

    public static List<ChatMessageDto> fromChatMessages(List<ChatMessage> messages) {
        return messages.stream()
                .map(ChatMessageDto::fromChatMessage)
                .toList();
    }

}
