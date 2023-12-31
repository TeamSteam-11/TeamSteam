package com.ll.TeamSteam.domain.chatMessage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignalResponse {
    private SignalType type;
    private String message;
}