package com.ll.TeamSteam.domain.dmMessage.controller;

import com.ll.TeamSteam.domain.dmMessage.dto.DmMessageDto;
import com.ll.TeamSteam.domain.dmMessage.dto.request.DmMessageRequest;
import com.ll.TeamSteam.domain.dmMessage.dto.response.DmResponse;
import com.ll.TeamSteam.domain.dmMessage.service.DmMessageService;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
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

import static com.ll.TeamSteam.domain.dmMessage.dto.response.DmSignalType.NEW_MESSAGE;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DmMessageController {

    private final DmMessageService dmMessageService;
    private final UserService userService;

    @MessageMapping("/dm/{dmId}/sendMessage") // app/dm/{dmId}/sendMessage
    @SendTo("/topic/dm/{dmId}") // 다시보내는 경로? enableSimpleBroker
    public DmResponse sendChatMessage(@DestinationVariable Long dmId, DmMessageRequest request,
                                      @AuthenticationPrincipal SecurityUser user)  {

        User sender = userService.findByIdElseThrow(user.getId());
        log.info("User user.getAvatar = {}", sender.getAvatar());

        dmMessageService.createAndSave(request.getContent(), sender, dmId);

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
            @PathVariable Long dmId, @AuthenticationPrincipal SecurityUser user, @RequestParam(defaultValue = "000000000000000000000000") String fromMessageId) {

        List<DmMessageDto> dmMessageDtos =
                dmMessageService.getByDmIdAndUserIdAndFromId(dmId, user.getId(), fromMessageId);

        return dmMessageDtos;
    }
}