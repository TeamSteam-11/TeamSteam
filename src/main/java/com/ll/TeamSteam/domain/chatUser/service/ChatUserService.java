package com.ll.TeamSteam.domain.chatUser.service;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.exception.NotInChatRoomException;
import com.ll.TeamSteam.domain.chatRoom.repository.ChatRoomRepository;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUser;
import com.ll.TeamSteam.domain.chatUser.repository.ChatUserRepository;
import com.ll.TeamSteam.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatUserService {

    private final ChatUserRepository chatUserRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatUser findById(Long chatRoomUserId) {
        return chatUserRepository.findById(chatRoomUserId).orElseThrow();
    }

    public List<ChatUser> findByChatRoomIdAndChatUser(Long roomId, Long userId) {
        ChatRoom chatRoom = findByRoomId(roomId);
        ChatUser chatUser = findChatUserByUserId(chatRoom, userId);

        if (chatUser == null) {
            throw new NotInChatRoomException("해당 방에 참가하지 않았어!");
        }

        return chatUserRepository.findByChatRoomId(roomId);
    }

    public ChatUser findChatUserByUserId(ChatRoom chatRoom, Long userId) {
        return chatRoom.getChatUsers().stream()
                .filter(chatUser -> chatUser.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotInChatRoomException("해당 방에 참가하지 않았어!"));
    }

    public ChatRoom findByRoomId(Long roomId) {

        return chatRoomRepository.findById(roomId).orElseThrow();
    }

    public ChatUser findByChatRoomAndUser(ChatRoom chatRoom, User user) {
        return chatUserRepository.findByChatRoomAndUser(chatRoom, user);
    }
}
