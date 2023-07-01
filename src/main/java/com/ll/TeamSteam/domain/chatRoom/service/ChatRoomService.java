package com.ll.TeamSteam.domain.chatRoom.service;

import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage;
import com.ll.TeamSteam.domain.chatRoom.dto.ChatRoomDto;
import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.repository.ChatRoomRepository;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUser;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUserType;
import com.ll.TeamSteam.domain.chatUser.service.ChatUserService;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.notification.entity.Notification;
import com.ll.TeamSteam.domain.notification.repository.NotificationRepository;
import com.ll.TeamSteam.domain.notification.service.NotificationService;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.event.EventAfterInvite;
import com.ll.TeamSteam.global.rsData.RsData;
import com.ll.TeamSteam.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.ll.TeamSteam.domain.chatUser.entity.ChatUserType.KICKED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;
    private final ChatUserService chatUserService;
    private final SimpMessageSendingOperations template;
    private final ApplicationEventPublisher publisher;
    private final NotificationService notificationService;


    @Transactional
    public ChatRoom createAndConnect(String subject, Matching matching, Long ownerId) {
        User owner = userService.findByIdElseThrow(ownerId);
        ChatRoom chatRoom = ChatRoom.create(subject, matching, owner);

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        log.info("savedChatRoom = {} ", savedChatRoom);
        log.info("Owner = {} ", owner);
        log.info("matching = {}", matching);

        savedChatRoom.addChatUser(owner);

        return savedChatRoom;
    }

    public List<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }

    public ChatRoom findById(Long roomId) {
        return chatRoomRepository.findById(roomId).orElseThrow();
    }

    @Transactional
    public ChatRoomDto getByIdAndUserId(Long roomId, long userId) {
        User user = userService.findByIdElseThrow(userId);

        ChatRoom chatRoom = findById(roomId);

        addChatRoomUser(chatRoom, user, userId);

        chatRoom.getChatUsers().stream()
                .filter(chatUser -> chatUser.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow();

        return ChatRoomDto.fromChatRoom(chatRoom, user);
    }

    private Optional<ChatUser> getChatUser(ChatRoom chatRoom, User user, Long userId) {
        // 방에 해당 유저가 있으면 가져오기
        Optional<ChatUser> existingUser = chatRoom.getChatUsers().stream()
                .filter(chatUser -> chatUser.getUser().getId().equals(userId))
                .findFirst();

        log.info("userId = {}", userId);
        log.info("user.getId = {}", user.getId());

        return existingUser;
    }

    private void addChatRoomUser(ChatRoom chatRoom, User user, Long userId) {

        if (getChatUser(chatRoom, user, userId).isEmpty()) {
            chatRoom.addChatUser(user);
        }
    }

    // 참여자 추가 가능한지 확인하는 메서드
    public RsData canAddChatRoomUser(ChatRoom chatRoom, Long userId, Matching matching) {

        User user = userService.findByIdElseThrow(userId);

        // 이미 채팅방에 동일 유저가 존재하는 경우
        if (!getChatUser(chatRoom, user, userId).isEmpty()) {
            ChatUser chatUser = getChatUser(chatRoom, user, userId).get();
            return checkChatUserType(chatUser);
        }

//        if (!matching.canAddParticipant()) return RsData.of("F-2", "모임 정원 초과!");

        return RsData.of("S-1", "새로운 모임 채팅방에 참여합니다.");
    }

    public RsData checkChatUserType(ChatUser chatUser) {
        if (chatUser.getType().equals(KICKED)) return RsData.of("F-1", "강퇴당한 모임입니다!");

        return RsData.of("S-1", "기존 모임 채팅방에 참여합니다.");
    }

    /**
     * 채팅방 삭제
     */
    @Transactional
    // @CacheEvict(value = "chatroom", allEntries=true)
    public void remove(Long roomId, Long OwnerId) {
        User owner = userService.findByIdElseThrow(OwnerId);
        log.info("roomId = {}", roomId);
        log.info("OnwerId = {}", owner.getId());

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        if(!chatRoom.getOwner().equals(owner)) {
            throw new IllegalArgumentException("방 삭제 권한이 없습니다.");
        }

        removeChatRoom(chatRoom);
    }

    public void removeChatRoom(ChatRoom chatRoom) {
        chatRoom.getChatUsers().clear();
        chatRoomRepository.delete(chatRoom);
    }


    /**
     * 유저가 방 나가기
     * 현재는 사용안하고 있음!
     */
    @Transactional
    public void exitChatRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
        log.info("userId = {} ", userId);

        // 해당 유저의 ChatUser를 제거합니다.
        ChatUser chatUser = findChatUserByUserId(chatRoom, userId);
        log.info("chatUser = {} ", chatUser);

        if (chatUser != null) {
            chatUser.exitType();
        }
    }

    private ChatUser findChatUserByUserId(ChatRoom chatRoom, Long userId) {
        return chatRoom.getChatUsers().stream()
                .filter(chatUser -> chatUser.getUser().getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public void updateChatRoomName(ChatRoom chatRoom, String subject) {
        log.info("update subject = {}", subject);
        chatRoom.updateName(subject);
        chatRoomRepository.save(chatRoom);
    }

    // 유저 강퇴하기
    @Transactional
    public void kickChatUser(Long roomId, Long chatUserId, @AuthenticationPrincipal SecurityUser user) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        checkOwner(chatRoom, user.getId());

        ChatUser chatUser = chatUserService.findById(chatUserId);
        User kickUser = chatUser.getUser();

        // 강톼당할 UserId를 받아와야한다.
        Long originUserId = kickUser.getId();

        chatUser.changeType(); // 강퇴된 유저의 type 을 "KICKED"로 변경

        List<ChatMessage> chatMessages = chatRoom.getChatMessages();

        chatMessages.stream()
                .filter(chatMessage -> chatMessage.getSender().getId().equals(chatUserId))
                .forEach(chatMessage -> chatMessage.removeChatMessages("강퇴된 사용자의 메시지입니다."));

        template.convertAndSend("/topic/chats/" + roomId + "/kicked", originUserId);
    }

    public void checkOwner(ChatRoom chatRoom, Long ownerId) {
        User owner = userService.findByIdElseThrow(ownerId);

        log.info("roomId = {}", chatRoom.getId());
        log.info("OwnerId = {}", owner.getId());

        if (!chatRoom.getOwner().getId().equals(owner.getId())) {
            throw new IllegalArgumentException("강퇴 권한이 없습니다.");
        }
    }

    @Transactional
    public void updateChatUserType(Long roomId, Long userId) {
        ChatRoom chatRoom = chatUserService.findByRoomId(roomId);
        User user = userService.findByIdElseThrow(userId);

        UserTypeChange(chatRoom, user);
    }

    /**
     * COMMON으로 Type 수정
     */
    public void UserTypeChange(ChatRoom chatRoom, User user) {
        ChatUser chatUser = chatUserService.findByChatRoomAndUser(chatRoom, user);

        log.info("chatRoomId = {} ", chatRoom.getId());
        log.info("getUserId = {} ", user.getId());

        chatUser.changeUserCommonType();

    }

    public Long getCommonParticipantsCount(ChatRoom chatRoom) {
        return chatRoom.getChatUsers().stream()
                .filter(chatUser -> chatUser.getType().equals(ChatUserType.COMMON))
                .count();
    }

    @Transactional
    public void changeParticipant(ChatRoom chatRoom) {
        Long commonParticipantsCount = getCommonParticipantsCount(chatRoom);
        log.info("commonParticipantsCount = {} ", commonParticipantsCount);
//        chatRoom.getMatching().setParticipantsCount(commonParticipantsCount);
    }

    public RsData<User> inviteUser(Long roomId, SecurityUser user, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        log.info("chatRoom = {} ", chatRoom);

        // 현재 로그인된 사용자가 방에 있는지 확인하는 로직
        Long invitingUserId = user.getId();
        User invitingUser = userService.findByIdElseThrow(invitingUserId);
        ChatUser chatUserByUserId = findChatUserByUserId(chatRoom, invitingUserId);

        // return 값 바꿀 거면 수정하기
        if (chatUserByUserId == null){
            return RsData.of("F-1", "현재 당신은 %s 방에 들어있지 않습니다.".formatted(chatRoom.getName()));
        }

        User invitedUser = userService.findByIdElseThrow(userId);

        if (invitingUser.getId() == invitedUser.getId()) {
            return RsData.of("F-2", "본인을 초대할 수 없습니다");
        }

        Optional<Notification> optionalLastNotification = notificationService.inviteCoolTime1Minute(invitingUser, invitedUser, chatRoom.getId());

        log.info("optionalLastNotification = {} ", optionalLastNotification);

        if (optionalLastNotification.isPresent()) {
            LocalDateTime lastInvitationTime = optionalLastNotification.get().getCreateDate();
            log.info("lastInvitationTime = {} ", lastInvitationTime);
            Duration durationSinceLastInvitation = Duration.between(lastInvitationTime, LocalDateTime.now());

            if (durationSinceLastInvitation.toMinutes() < 1) {
                return RsData.of("F-3", "1분 내에는 다시 초대할 수 없습니다");
            }
        }

        publisher.publishEvent(new EventAfterInvite(this, invitingUser, invitedUser, chatRoom));

        return RsData.of("S-1", "%s 님에게 초대를 보냈습니다".formatted(invitedUser.getUsername()));
    }
}