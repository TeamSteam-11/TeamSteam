package com.ll.TeamSteam.domain.chatMessage.repository;

import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomId(Long roomId);

}
