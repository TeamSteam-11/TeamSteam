package com.ll.TeamSteam.domain.chatMessage.controller;

import com.ll.TeamSteam.domain.chatMessage.dto.ChatMessageDto;
import com.ll.TeamSteam.domain.chatMessage.dto.request.ChatMessageRequest;
import com.ll.TeamSteam.domain.chatMessage.dto.response.SignalResponse;
import com.ll.TeamSteam.domain.chatMessage.service.ChatMessageService;
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

import static com.ll.TeamSteam.domain.chatMessage.dto.response.SignalType.NEW_MESSAGE;
import static com.ll.TeamSteam.domain.chatMessage.entity.ChatMessageType.MESSAGE;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chats/{roomId}/sendMessage") // app/chats/{roomId}/sendMessage
    @SendTo("/topic/chats/{roomId}") // 다시보내는 경로? enableSimpleBroker
    public SignalResponse sendChatMessage(@DestinationVariable Long roomId, ChatMessageRequest request,
                                          @AuthenticationPrincipal SecurityUser user)  {

        chatMessageService.createAndSave(request.getContent(), user.getId(), roomId, MESSAGE);

        return SignalResponse.builder()
                .type(NEW_MESSAGE)
                .build();
    }

    @MessageExceptionHandler
    public void handleException(Exception ex) {
        log.error("예외 발생!!!", ex);
    }

    @GetMapping("/chat/rooms/{roomId}/messages")
    @ResponseBody
    public List<ChatMessageDto> findAll(
            @PathVariable Long roomId, @AuthenticationPrincipal SecurityUser user,
            @RequestParam(defaultValue = "000000000000000000000000") String fromId) {

        List<ChatMessageDto> chatMessageDtos =
                chatMessageService.getByChatRoomIdAndUserIdAndFromId(roomId, user.getId(), fromId);

        return chatMessageDtos;
    }
}