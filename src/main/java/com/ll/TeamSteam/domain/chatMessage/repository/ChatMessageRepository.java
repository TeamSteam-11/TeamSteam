package com.ll.TeamSteam.domain.chatMessage.repository;

import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {


    List<ChatMessage> findByChatRoomId(Long roomId);
}
