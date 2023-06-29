package com.ll.TeamSteam.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ll.TeamSteam.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {

    @JsonProperty("user_id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("created_at")
    private LocalDateTime createDate;

    @JsonProperty("updated_at")
    private LocalDateTime modifyDate;

    public static UserDto fromUser(User user) {
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .createDate(user.getCreateDate())
                .modifyDate(user.getModifyDate())
                .build();

        return userDto;
    }
}
