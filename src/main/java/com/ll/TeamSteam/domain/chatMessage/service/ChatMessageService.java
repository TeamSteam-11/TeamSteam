package com.ll.TeamSteam.domain.chatMessage.service;

import com.ll.TeamSteam.domain.chatMessage.dto.ChatMessageDto;
import com.ll.TeamSteam.domain.chatMessage.dto.response.SignalResponse;
import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage;
import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessageType;
import com.ll.TeamSteam.domain.chatMessage.repository.ChatMessageRepository;
import com.ll.TeamSteam.domain.chatRoom.dto.ChatRoomDto;
import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.service.ChatRoomService;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.ll.TeamSteam.domain.chatMessage.dto.response.SignalType.NEW_MESSAGE;
import static com.ll.TeamSteam.domain.chatMessage.entity.ChatMessageType.ENTER;
import static com.ll.TeamSteam.domain.chatMessage.entity.ChatMessageType.LEAVE;
import static com.ll.TeamSteam.domain.chatUser.entity.ChatUserType.*;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = "cacheManager")
@Slf4j
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final SimpMessageSendingOperations template;
    private final UserService userService;

    public ChatMessage createAndSave(String content, Long userId, Long chatRoomId, ChatMessageType type) {

        ChatRoom chatRoom = chatRoomService.findByRoomId(chatRoomId);

        User sender = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저아님"));

        ChatMessage chatMessage = ChatMessage.create(content, sender.getId(), sender.getUsername(), sender.getAvatar(), type, chatRoom.getId());
        // 글자수 제한
        chatMessage.validateLength(chatMessage.getContent());

        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessageDto> getByChatRoomIdAndUserIdAndFromId(Long roomId, Long userId, String fromId) {

        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);

        chatRoom.getChatUsers().stream()
                .filter(chatUser -> chatUser.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow();

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomId(roomId);

        List<ChatMessage> list = chatMessages.stream()
                .filter(chatMessage -> new ObjectId(chatMessage.getId()).compareTo(new ObjectId(fromId)) > 0)
                .sorted(Comparator.comparing(ChatMessage::getId))
                .collect(Collectors.toList());

        return ChatMessageDto.fromChatMessages(list);
    }


    public void enterMessage(ChatRoomDto chatRoomDto, SecurityUser user, Long roomId) {

        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);

        if (chatRoomDto.getType().equals(ROOMIN) || chatRoomDto.getType().equals(EXIT)){
            String enterMessage = " < " + user.getUsername() + "님이 입장하셨습니다. >";
            createAndSave(enterMessage, user.getId(), roomId, ENTER);

            SignalResponse signalResponse = SignalResponse.builder()
                    .type(NEW_MESSAGE)
                    .message(enterMessage)
                    .build();

            template.convertAndSend("/topic/chats/" + chatRoom.getId(), signalResponse);
        }
    }

    public void leaveMessage(ChatRoomDto chatRoomDto, SecurityUser user, Long roomId) {

        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);

        if (chatRoomDto.getType().equals(COMMON)){
            String exitMessage = " < " + user.getUsername() + "님이 퇴장하셨습니다. >";
            createAndSave(exitMessage, user.getId(), roomId, LEAVE);

            SignalResponse signalResponseLeave = SignalResponse.builder()
                    .type(NEW_MESSAGE)
                    .message(exitMessage)
                    .build();

            template.convertAndSend("/topic/chats/" + chatRoom.getId(), signalResponseLeave);
        }
    }
}
