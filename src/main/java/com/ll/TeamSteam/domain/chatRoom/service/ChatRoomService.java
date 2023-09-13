package com.ll.TeamSteam.domain.chatRoom.service;

import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage;
import com.ll.TeamSteam.domain.chatMessage.repository.ChatMessageRepository;
import com.ll.TeamSteam.domain.chatRoom.dto.ChatRoomDto;
import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.exception.CanNotEnterException;
import com.ll.TeamSteam.domain.chatRoom.exception.KickedUserEnterException;
import com.ll.TeamSteam.domain.chatRoom.exception.NoChatRoomException;
import com.ll.TeamSteam.domain.chatRoom.exception.NotInChatRoomException;
import com.ll.TeamSteam.domain.chatRoom.repository.ChatRoomRepository;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUser;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUserType;
import com.ll.TeamSteam.domain.chatUser.service.ChatUserService;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.matchingPartner.repository.MatchingPartnerRepository;
import com.ll.TeamSteam.domain.matchingPartner.service.MatchingPartnerService;
import com.ll.TeamSteam.domain.notification.service.NotificationService;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.event.EventAfterInvite;
import com.ll.TeamSteam.global.event.EventEnterNewChatUser;
import com.ll.TeamSteam.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ll.TeamSteam.domain.chatUser.entity.ChatUserType.EXIT;
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
    private final MatchingPartnerService matchingPartnerService;
    private final MatchingPartnerRepository matchingPartnerRepository;
    private final ChatMessageRepository chatMessageRepository;


    @Transactional
    public ChatRoom createChatRoomAndConnectMatching(String subject, Matching matching, Long ownerId) {
        User owner = userService.findByIdElseThrow(ownerId);
        ChatRoom chatRoom = ChatRoom.create(subject, matching, owner);

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        savedChatRoom.addChatUser(owner);

        ChatUser chatUser = chatUserService.findChatUserByUserId(chatRoom, ownerId);
        chatUser.changeUserCommonType();

        return savedChatRoom;
    }

    @Transactional
    public ChatRoomDto validEnterChatRoom(Long roomId, long userId) {
        User user = userService.findByIdElseThrow(userId);

        ChatRoom chatRoom = findByRoomId(roomId);
        Matching matching = chatRoom.getMatching();

        matchingPartnerService.validNotMatchingPartner(matching, user);

        addChatRoomUser(chatRoom, user, userId);

        chatUserService.findChatUserByUserId(chatRoom, user.getId());

        List<ChatUser> chatUsers = chatRoom.getChatUsers().stream()
                .filter(chatUser -> !chatUser.getUser().getId().equals(userId))
                .toList();

        for (ChatUser chatUser : chatUsers){
            User inChatUser = chatUser.getUser();
            publisher.publishEvent(new EventEnterNewChatUser(this, chatRoom, inChatUser, user));
        }

        return ChatRoomDto.fromChatRoom(chatRoom, user);
    }

    private Optional<ChatUser> getChatUser(ChatRoom chatRoom, Long userId) {
        Optional<ChatUser> existingUser = chatRoom.getChatUsers().stream()
                .filter(chatUser -> chatUser.getUser().getId().equals(userId))
                .findFirst();

        return existingUser;
    }

    private void addChatRoomUser(ChatRoom chatRoom, User user, Long userId) {
        if (getChatUser(chatRoom, userId).isEmpty()) {
            chatRoom.addChatUser(user);
        }
    }

    public boolean canAddChatRoomUser(ChatRoom chatRoom, Long userId, Matching matching) {

        if(!getChatUser(chatRoom, userId).isEmpty() && !matching.canAddParticipant()) {
            boolean whatIsTrueFalse = isChatUserTypeExit(chatRoom, userId);

            if(whatIsTrueFalse) {
                throw new CanNotEnterException("매칭 전원 초과");
            }
        }

        if (!getChatUser(chatRoom, userId).isEmpty()) {
            ChatUser chatUser = getChatUser(chatRoom, userId).get();
            return checkChatUserType(chatUser);
        }

        if (!matching.canAddParticipant()) {
            throw new CanNotEnterException("매칭 전원 초과");
        }

        return true;
    }

    public boolean checkChatUserType(ChatUser chatUser) {
        if (chatUser.getType().equals(KICKED)) {
            throw new KickedUserEnterException("강퇴당한 모임입니다.");
        }

        return true;
    }

    @Transactional
    public void validRemoveChatRoom(Long roomId, Long OwnerId) {
        User owner = userService.findByIdElseThrow(OwnerId);

        ChatRoom chatRoom = findByRoomId(roomId);

        if(!chatRoom.getOwner().equals(owner)) {
            throw new IllegalArgumentException("방 삭제 권한이 없습니다.");
        }

        deleteChatRoom(chatRoom);
    }

    public void deleteChatRoom(ChatRoom chatRoom) {
        chatRoom.getChatUsers().clear();
        chatRoomRepository.delete(chatRoom);
    }

    @Transactional
    public void validExitChatRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = findByRoomId(roomId);

        ChatUser chatUser = chatUserService.findChatUserByUserId(chatRoom, userId);

        if(chatUser.getChatRoom().getOwner().getId() == userId){
            throw new IllegalArgumentException("방장은 방에서 나갈 수 없어");
        }

        if (chatUser != null) {
            chatUser.exitType();

            MatchingPartner matchingPartner = matchingPartnerService.findByMatchingIdAndUserId(roomId, userId);
            // 수동으로 연관관계 끊어주기
            // chatRoom.getMatching().deleteMatchingPartner(matchingPartner);
            matchingPartnerRepository.delete(matchingPartner);
        }
    }

    @Transactional
    public void updateChatRoomName(ChatRoom chatRoom, String subject) {
        chatRoom.updateName(subject);
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void kickChatUserAndChangeType(Long roomId, Long chatUserId, SecurityUser user) {
        ChatRoom chatRoom = findByRoomId(roomId);

        checkChatRoomOwner(chatRoom, user.getId());

        ChatUser chatUser = chatUserService.findById(chatUserId);
        User kickUser = chatUser.getUser();
        Long originUserId = kickUser.getId();

        chatUser.changeType();
        matchingPartnerService.updateFalse(roomId, originUserId);

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomId(roomId);

        chatMessages.stream()
                .filter(chatMessage -> chatMessage.getSenderId().equals(chatUserId))
                .forEach(chatMessage -> chatMessage.removeChatMessages("강퇴된 사용자의 메시지입니다."));

        MatchingPartner matchingPartner = matchingPartnerService.findByMatchingIdAndUserId(roomId, originUserId);
        matchingPartnerRepository.delete(matchingPartner);

        template.convertAndSend("/topic/chats/" + roomId + "/kicked", originUserId);
    }

    public void checkChatRoomOwner(ChatRoom chatRoom, Long ownerId) {
        User owner = userService.findByIdElseThrow(ownerId);

        if (!chatRoom.getOwner().getId().equals(owner.getId())) {
            throw new IllegalArgumentException("강퇴 권한이 없습니다.");
        }
    }

    @Transactional
    public void updateChatUserType(Long roomId, Long userId) {
        ChatRoom chatRoom = chatUserService.findByRoomId(roomId);
        User user = userService.findByIdElseThrow(userId);

        ChatUser chatUser = chatUserService.findByChatRoomAndUser(chatRoom, user);
        chatUser.changeUserCommonType();
    }

    public Long getCommonParticipantsCount(ChatRoom chatRoom) {
        return chatRoom.getChatUsers().stream()
                .filter(chatUser -> chatUser.getType().equals(ChatUserType.COMMON))
                .count();
    }

    @Transactional
    public void changeParticipantsCount(ChatRoom chatRoom) {
        Long commonParticipantsCount = getCommonParticipantsCount(chatRoom);
        chatRoom.getMatching().updateParticipant(commonParticipantsCount);
    }

    @Transactional
    public void validInviteChatRoom(Long roomId, SecurityUser user, Long invitedUserId) {
        ChatRoom chatRoom = findByRoomId(roomId);

        Long invitingUserId = user.getId();
        User invitingUser = userService.findByIdElseThrow(invitingUserId);
        ChatUser chatUserByUserId = chatUserService.findChatUserByUserId(chatRoom, invitingUserId);

        if (chatUserByUserId == null){
            throw new NotInChatRoomException("현재 당신은 %s 방에 들어있지 않습니다.".formatted(chatRoom.getName()));
        }

        User invitedUser = userService.findByIdElseThrow(invitedUserId);

        if (invitingUser.getId() == invitedUser.getId()) {
            throw new IllegalArgumentException("본인을 초대할 수 없습니다.");
        }

        publisher.publishEvent(new EventAfterInvite(this, invitingUser, invitedUser, chatRoom));
    }

    public boolean isDuplicatedInvitation(Long roomId, Long invitingUserId, Long invitedUserId) {
        ChatRoom chatRoom = findByRoomId(roomId);

        User invitingUser = userService.findByIdElseThrow(invitingUserId);
        ChatUser chatUserByUserId = chatUserService.findChatUserByUserId(chatRoom, invitingUserId);

        if (chatUserByUserId == null){
            throw new IllegalArgumentException("현재 당신은 %s 방에 들어있지 않습니다.".formatted(chatRoom.getName()));
        }

        User invitedUser = userService.findByIdElseThrow(invitedUserId);

        if (invitingUser.getId() != null && invitingUser.getId().equals(invitedUser.getId())) {
            throw new IllegalArgumentException("본인을 초대할 수 없습니다.");
        }

        return notificationService.checkDuplicateInvite(invitingUser, invitedUser, roomId);
    }

    private boolean isChatUserTypeExit(ChatRoom chatRoom, Long userId) {
        User currentUser = userService.findById(userId).orElseThrow();
        ChatUser exitUser = chatUserService.findByChatRoomAndUser(chatRoom, currentUser);

        // ChatUser existingUser = chatRoom.getChatUsers().stream()
        //                .filter(chatUser -> chatUser.getUser().getId().equals(userId))
        //                .findFirst()
        //                .orElseThrow();

        if (exitUser.getType().equals(EXIT)) {
            return true;
        } else {
            return false;
        }
    }

    public ChatRoom findByRoomId(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NoChatRoomException("방이 존재하지 않습니다."));
    }

    public Page<ChatRoom> findChatRoomByUserId(Long userId, Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate"); // 또는 다른 필드
        List<ChatUser> chatUsers = chatUserService.findByUserIdAndTypeIn(userId, ChatUserType.COMMON, sort);


        List<ChatRoom> chatRooms = chatUsers.stream()
                .map(ChatUser::getChatRoom)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), chatRooms.size());

        return new PageImpl<>(chatRooms.subList(start, end), pageable, chatRooms.size());
    }

    public List<ChatRoom> findChatRoomByUserId(Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate"); // 또는 다른 필드
        List<ChatUser> chatUsers = chatUserService.findByUserIdAndTypeIn(userId, ChatUserType.COMMON, sort);

        return chatUsers.stream()
                .map(ChatUser::getChatRoom)
                .collect(Collectors.toList());
    }
}