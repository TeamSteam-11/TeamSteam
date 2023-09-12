package com.ll.TeamSteam.domain.chatUser.repository;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUser;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUserType;
import com.ll.TeamSteam.domain.user.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
    List<ChatUser> findByChatRoomId(Long chatRoomId);

    ChatUser findByChatRoomAndUser(ChatRoom chatRoom, User user);

    List<ChatUser> findByUserIdAndType(Long userId, ChatUserType type, Sort sort);
}
