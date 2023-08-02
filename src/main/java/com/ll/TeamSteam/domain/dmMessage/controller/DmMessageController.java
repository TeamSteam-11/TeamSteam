package com.ll.TeamSteam.domain.dmMessage.controller;

import com.ll.TeamSteam.domain.dmMessage.dto.DmMessageDto;
import com.ll.TeamSteam.domain.dmMessage.dto.request.DmMessageRequest;
import com.ll.TeamSteam.domain.dmMessage.dto.response.DmResponse;
import com.ll.TeamSteam.domain.dmMessage.service.DmMessageService;
import com.ll.TeamSteam.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static com.ll.TeamSteam.domain.dmMessage.dto.response.DmSignalType.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DmMessageController {

    private final DmMessageService dmMessageService;

    @MessageMapping("/dm/{dmId}/sendMessage") // app/dm/{dmId}/sendMessage
    @SendTo("/topic/dm/{dmId}") // 다시보내는 경로? enableSimpleBroker
    public DmResponse sendChatMessage(@DestinationVariable Long dmId, DmMessageRequest request,
                                      @AuthenticationPrincipal SecurityUser user)  {

        dmMessageService.createAndSave(request.getContent(), user.getId(), dmId);

        return DmResponse.builder()
                .type(NEW_MESSAGE)
                .build();
    }

    @MessageExceptionHandler
    public void handleException(Exception ex) {
        log.error("예외 발생!!!", ex);
    }

    @GetMapping("/dm/rooms/{dmId}/messages")
    @ResponseBody
    public List<DmMessageDto> findAll(
            @PathVariable Long dmId, @AuthenticationPrincipal SecurityUser user,
            @RequestParam(defaultValue = "0") Long fromMessageId) {

        List<DmMessageDto> dmMessageDtos =
                dmMessageService.getByDmIdAndUserIdAndFromId(dmId, user.getId(), fromMessageId);

        return dmMessageDtos;
    }
}
