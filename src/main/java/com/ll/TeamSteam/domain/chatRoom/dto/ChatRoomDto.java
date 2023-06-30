package com.ll.TeamSteam.domain.chatRoom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUser;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUserType;
import com.ll.TeamSteam.domain.user.dto.UserDto;
import com.ll.TeamSteam.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ChatRoomDto implements Serializable {

    @JsonProperty("chat_room_id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("owner")
    private UserDto owner;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // 추가: 사용자 상태를 나타내는 필드
    @JsonProperty("type")
    private ChatUserType type;

    public static ChatRoomDto fromChatRoom(ChatRoom chatRoom, User user) {
        UserDto userDto = UserDto.fromUser(chatRoom.getOwner());

        log.info("user = {} ", user);

        ChatUser chatUser = chatRoom.getChatUsers().stream()
                .filter(cm -> cm.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);

        log.info("chatUserId = {}", chatUser.getId());
        log.info("userId = {}", user.getId());

        ChatUserType nowType = chatUser.getType();

        log.info("nowType = {}", nowType);

        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .owner(userDto)
                .createdAt(chatRoom.getCreateDate())
                .updatedAt(chatRoom.getModifyDate())
                .type(nowType)
                .build();

        return chatRoomDto;
    }
}
