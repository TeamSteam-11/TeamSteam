package com.ll.TeamSteam.domain.chatRoom.repository;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>{
}