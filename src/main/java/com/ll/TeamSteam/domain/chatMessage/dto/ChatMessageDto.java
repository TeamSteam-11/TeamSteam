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
    private String id;

    @JsonProperty("content")
    private String content;

    @JsonProperty("senderId")
    private Long senderId;
    @JsonProperty("senderUsername")
    private String senderUsername;
    @JsonProperty("senderAvatar")
    private String senderAvatar;

    @JsonProperty("type")
    private ChatMessageType type;

    @JsonProperty("createDate")
    private LocalDateTime createDate;

    public static ChatMessageDto fromChatMessage(ChatMessage chatMessage) {

        Long senderId = chatMessage.getSenderId();
        String senderUsername = chatMessage.getSenderUsername();
        String senderAvatar = chatMessage.getSenderAvatar();

        // UserDto userDto = UserDto.fromUser(chatMessage.getSender().getUser());

        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .id(chatMessage.getId())
                .type(chatMessage.getType())
                .senderId(senderId)
                .senderUsername(senderUsername)
                .senderAvatar(senderAvatar)
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .createDate(chatMessage.getCreateDate())
                .build();

        return chatMessageDto;
    }

    public static List<ChatMessageDto> fromChatMessages(List<ChatMessage> messages) {
        return messages.stream()
                .map(ChatMessageDto::fromChatMessage)
                .toList();
    }

}
