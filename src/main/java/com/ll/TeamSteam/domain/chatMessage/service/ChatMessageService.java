package com.ll.TeamSteam.domain.chatMessage.service;

import com.ll.TeamSteam.domain.chatMessage.dto.ChatMessageDto;
import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage;
import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessageType;
import com.ll.TeamSteam.domain.chatMessage.repository.ChatMessageRepository;
import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.service.ChatRoomService;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = "cacheManager")
@Slf4j
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

    public ChatMessage createAndSave(String content, Long senderId, Long chatRoomId, ChatMessageType type) {

        ChatRoom chatRoom = chatRoomService.findByRoomId(chatRoomId);

        ChatUser sender = chatRoom.getChatUsers().stream()
                .filter(chatUser -> chatUser.getUser().getId().equals(senderId))
                .findFirst()
                .orElseThrow();

        ChatMessage chatMessage = ChatMessage.create(content, sender, type, chatRoom);

        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessageDto> getByChatRoomIdAndUserIdAndFromId(Long roomId, Long userId, Long fromId) {

        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);

        chatRoom.getChatUsers().stream()
                .filter(chatUser -> chatUser.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow();

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomId(roomId);

        List<ChatMessage> list = chatMessages.stream()
                .filter(chatMessage -> chatMessage.getId() > fromId)
                .sorted(Comparator.comparing(ChatMessage::getId))
                .toList();

        return ChatMessageDto.fromChatMessages(list);
    }
}
