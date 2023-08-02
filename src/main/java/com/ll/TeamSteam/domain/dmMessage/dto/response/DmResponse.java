package com.ll.TeamSteam.domain.dmMessage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DmResponse {
    private DmSignalType type;
    private String message;
}