package com.ll.TeamSteam.domain.chatUser.service;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
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

    public List<ChatUser> findByChatRoomId(Long chatRoomId) {
        return chatUserRepository.findByChatRoomId(chatRoomId);
    }

    public List<ChatUser> findByChatRoomIdAndChatUser(Long roomId, Long userId) {
        ChatRoom chatRoom = findByRoomId(roomId);
        ChatUser chatUser = findChatUserByUserId(chatRoom, userId);

        if (chatUser == null) {
            return null;
        }

        return chatUserRepository.findByChatRoomId(roomId);
    }

    private ChatUser findChatUserByUserId(ChatRoom chatRoom, Long userId) {
        return chatRoom.getChatUsers().stream()
                .filter(chatUser -> chatUser.getUser().getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public ChatRoom findByRoomId(Long roomId) {

        return chatRoomRepository.findById(roomId).orElseThrow();
    }

    // OwnerID 비교
    public List<ChatUser> findByChatRoomOwnerIdAndChatUser(Long roomId, Long userId) {
        ChatRoom chatRoom = findByRoomId(roomId);
        Long ownerId = chatRoom.getOwner().getId();
        log.info("ownerId = {} ", ownerId);


        if (ownerId != userId){
            return null;
        }

        return chatUserRepository.findByChatRoomId(roomId);
    }

    public ChatUser findByChatRoomAndUser(ChatRoom chatRoom, User user) {
        return chatUserRepository.findByChatRoomAndUser(chatRoom, user);
    }
}
