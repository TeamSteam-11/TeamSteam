package com.ll.TeamSteam.domain.dm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ll.TeamSteam.domain.dm.entity.Dm;
import com.ll.TeamSteam.domain.dmUser.entity.DmUser;
import com.ll.TeamSteam.domain.user.dto.UserDto;
import com.ll.TeamSteam.domain.user.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class DmDto {

    @JsonProperty("chat_room_id")
    private Long id;

    @JsonProperty("owner")
    private UserDto owner;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public static DmDto fromDmDto(Dm dm, User user) {
        UserDto userDto = UserDto.fromUser(dm.getDmSender());

        DmUser dmUser = dm.getDmUsers().stream()
                .filter(du -> du.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);

        DmDto dmDto = DmDto.builder()
                .id(dm.getId())
                .owner(userDto)
                .createdAt(dm.getCreateDate())
                .updatedAt(dm.getModifyDate())
                .build();

        return dmDto;
    }
}
