package com.ll.TeamSteam.domain.chatRoom.service;

import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage;
import com.ll.TeamSteam.domain.chatRoom.dto.ChatRoomDto;
import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.exception.KickedUserEnterException;
import com.ll.TeamSteam.domain.chatRoom.exception.NoChatRoomException;
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
import com.ll.TeamSteam.global.rsData.RsData;
import com.ll.TeamSteam.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ll.TeamSteam.domain.chatUser.entity.ChatUserType.*;

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


    @Transactional
    public ChatRoom createChatRoomAndConnectMatching(String subject, Matching matching, Long ownerId) {
        User owner = userService.findByIdElseThrow(ownerId);
        ChatRoom chatRoom = ChatRoom.create(subject, matching, owner);

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        savedChatRoom.addChatUser(owner);

        ChatUser chatUser = findChatUserByUserId(chatRoom, ownerId);
        chatUser.changeUserCommonType();

        return savedChatRoom;
    }

    public ChatRoom findById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NoChatRoomException("방이 존재하지 않습니다."));
    }

    @Transactional
    public ChatRoomDto validEnterChatRoom(Long roomId, long userId) {
        User user = userService.findByIdElseThrow(userId);

        ChatRoom chatRoom = findById(roomId);
        Matching matching = chatRoom.getMatching();

        matchingPartnerService.validNotMatchingPartner(matching, user);

        addChatRoomUser(chatRoom, user, userId);

        findChatUserByUserId(chatRoom, user.getId());

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

    public RsData canAddChatRoomUser(ChatRoom chatRoom, Long userId, Matching matching) {

        if(!getChatUser(chatRoom, userId).isEmpty() && !matching.canAddParticipant()) {
            boolean whatIsTrueFalse = isChatUserTypeExit(chatRoom, userId);

            if(whatIsTrueFalse) {
                return RsData.of("F-2", "모임 정원 초과!");
            }
        }

        // 이미 채팅방에 동일 유저가 존재하는 경우
        if (!getChatUser(chatRoom, userId).isEmpty()) {
            ChatUser chatUser = getChatUser(chatRoom, userId).get();
            return checkChatUserType(chatUser);
        }

        if (!matching.canAddParticipant()) {
            return RsData.of("F-2", "모임 정원 초과!"); // 참여자 수가 capacity 보다 많으면 참가 하지 못하도록
        }

        return RsData.of("S-1", "새로운 모임 채팅방에 참여합니다.");
    }

    public RsData checkChatUserType(ChatUser chatUser) {
        if (chatUser.getType().equals(KICKED)) {
            throw new KickedUserEnterException("강퇴당한 모임입니다.");
        }

        return RsData.of("S-1", "기존 모임 채팅방에 참여합니다.");
    }

    @Transactional
    public void validRemoveChatRoom(Long roomId, Long OwnerId) {
        User owner = userService.findByIdElseThrow(OwnerId);

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NoChatRoomException("존재하지 않는 방입니다."));

        if(!chatRoom.getOwner().equals(owner)) {
            throw new IllegalArgumentException("방 삭제 권한이 없습니다.");
        }

        deleteChatRoom(chatRoom);
    }

    public void deleteChatRoom(ChatRoom chatRoom) {
        chatRoom.getChatUsers().clear();
        chatRoomRepository.delete(chatRoom);
    }


    /**
     * 유저가 방 나가기
     */
    @Transactional
    public void validExitChatRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NoChatRoomException("존재하지 않는 방입니다."));

        ChatUser chatUser = findChatUserByUserId(chatRoom, userId);

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

    public ChatUser findChatUserByUserId(ChatRoom chatRoom, Long userId) {
        return chatRoom.getChatUsers().stream()
                .filter(chatUser -> chatUser.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("너 ChatUser 아니야!"));
    }

    @Transactional
    public void updateChatRoomName(ChatRoom chatRoom, String subject) {
        chatRoom.updateName(subject);
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void kickChatUserAndChangeType(Long roomId, Long chatUserId, SecurityUser user) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NoChatRoomException("존재하지 않는 방입니다."));

        checkChatRoomOwner(chatRoom, user.getId());

        ChatUser chatUser = chatUserService.findById(chatUserId);
        User kickUser = chatUser.getUser();

        Long originUserId = kickUser.getId();

        chatUser.changeType();
        matchingPartnerService.updateFalse(roomId, originUserId);

        List<ChatMessage> chatMessages = chatRoom.getChatMessages();

        chatMessages.stream()
                .filter(chatMessage -> chatMessage.getSender().getId().equals(chatUserId))
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

    // 버그 없는지 확인 : 메서드 두 개 통합
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
    public RsData<User> validInviteChatRoom(Long roomId, SecurityUser user, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NoChatRoomException("존재하지 않는 방입니다."));

        Long invitingUserId = user.getId();
        User invitingUser = userService.findByIdElseThrow(invitingUserId);
        ChatUser chatUserByUserId = findChatUserByUserId(chatRoom, invitingUserId);

        if (chatUserByUserId == null){
            throw new IllegalArgumentException("현재 당신은 %s 방에 들어있지 않습니다.".formatted(chatRoom.getName()));
        }

        User invitedUser = userService.findByIdElseThrow(userId);

        if (invitingUser.getId() == invitedUser.getId()) {
            return RsData.of("F-2", "본인을 초대할 수 없습니다");
        }

        publisher.publishEvent(new EventAfterInvite(this, invitingUser, invitedUser, chatRoom));

        return RsData.of("S-1", "%s 님에게 초대를 보냈습니다".formatted(invitedUser.getUsername()));
    }

    public boolean isDuplicatedInvitation(Long roomId, Long invitingUserId, Long invitedUserId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NoChatRoomException("존재하지 않는 방입니다."));

        User invitingUser = userService.findByIdElseThrow(invitingUserId);
        ChatUser chatUserByUserId = findChatUserByUserId(chatRoom, invitingUserId);

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
}