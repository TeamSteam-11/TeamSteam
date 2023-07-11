package com.ll.TeamSteam.domain.chatRoom.service;

import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage;
import com.ll.TeamSteam.domain.chatRoom.dto.ChatRoomDto;
import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.repository.ChatRoomRepository;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUser;
import com.ll.TeamSteam.domain.chatUser.entity.ChatUserType;
import com.ll.TeamSteam.domain.chatUser.service.ChatUserService;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.matchingPartner.repository.MatchingPartnerRepository;
import com.ll.TeamSteam.domain.matchingPartner.service.MatchingPartnerService;
import com.ll.TeamSteam.domain.notification.service.NotificationService;
import com.ll.TeamSteam.domain.recentlyUser.service.RecentlyUserService;
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

import static com.ll.TeamSteam.domain.chatUser.entity.ChatUserType.EXIT;
import static com.ll.TeamSteam.domain.chatUser.entity.ChatUserType.KICKED;

@Service
//@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;
    private final ChatUserService chatUserService;
    private final RecentlyUserService recentlyUserService;
    private final SimpMessageSendingOperations template;
    private final ApplicationEventPublisher publisher;
    private final NotificationService notificationService;
    private final MatchingPartnerService matchingPartnerService;
    private final MatchingPartnerRepository matchingPartnerRepository;


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
        /**
         * 매칭 파트너가 아닌데 채팅방에 들어가려고 하면 IllegalArgumentException 던저주기
         */
        Matching matching = chatRoom.getMatching();

        matching.getMatchingPartners().stream()
                .filter(matchingPartner -> matchingPartner.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("너 매칭 파트너 아니야"));

        addChatRoomUser(chatRoom, user, userId);

        chatRoom.getChatUsers().stream()
                .filter(chatUser -> chatUser.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow();

        return ChatRoomDto.fromChatRoom(chatRoom, user);
    }

    private Optional<ChatUser> getChatUser(ChatRoom chatRoom, Long userId) {
        // 방에 해당 유저가 있으면 가져오기
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

    // 참여자 추가 가능한지 확인하는 메서드
    public RsData canAddChatRoomUser(ChatRoom chatRoom, Long userId, Matching matching) {

        // 발견한 건가?..발견한 것 같아!!!!
        if(!getChatUser(chatRoom, userId).isEmpty() && !matching.canAddParticipant()) {
            boolean whatIsTrueFalse = isExitUser(chatRoom, userId);
            log.info("whatIsTrueFalse = {}", whatIsTrueFalse);

            //방에 있는 사용자들 최근 매칭된 유저로 업데이트
            List<ChatUser> chatUserList = chatUserService.findByChatRoomId(chatRoom.getId());

            chatUserList.stream()
                .map(ChatUser::getUser)
                .map(User::getId)
                .forEach(recentlyUserService::updateRecentlyUser);

            if(whatIsTrueFalse) {
                return RsData.of("F-2", "모임 으으으으악 정원 초과!");
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
            return RsData.of("E-1", "강퇴당한 모임입니다!");
        }

        return RsData.of("S-1", "기존 모임 채팅방에 참여합니다.");
    }

    /**
     * 채팅방 삭제
     */
    @Transactional
    public void remove(Long roomId, Long OwnerId) {
        User owner = userService.findByIdElseThrow(OwnerId);
        log.info("roomId = {}", roomId);
        log.info("OwnerId = {}", owner.getId());

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
     */
    @Transactional
    public void exitChatRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
        log.info("userId = {} ", userId);

        // 해당 유저의 ChatUser를 제거합니다.
        ChatUser chatUser = findChatUserByUserId(chatRoom, userId);
        log.info("chatUser = {} ", chatUser);

        // 방장은 방에서 나갈 수 없도록 설정
        if(chatUser.getChatRoom().getOwner().getId() == userId){
            log.info("chatUser.getChatRoom().getOwner().getId() = {}", chatUser.getChatRoom().getOwner().getId());
            log.info("Owner userId = {}", userId);
            throw new IllegalArgumentException("방장은 방에서 나갈 수 없어");
        }

        if (chatUser != null) {
            chatUser.exitType();

            //퇴장 시 매칭파트너에서도 삭제
            MatchingPartner matchingPartner = matchingPartnerService.findByMatchingIdAndUserId(roomId, userId);
            // 수동으로 연관관계 끊어주기
//            chatRoom.getMatching().deleteMatchingPartner(matchingPartner);
            matchingPartnerRepository.delete(matchingPartner);

        }
    }

    public ChatUser findChatUserByUserId(ChatRoom chatRoom, Long userId) {
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
    public void kickChatUser(Long roomId, Long chatUserId, SecurityUser user) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        checkOwner(chatRoom, user.getId());

        ChatUser chatUser = chatUserService.findById(chatUserId);
        User kickUser = chatUser.getUser();

        // 강톼당할 UserId를 받아와야한다.
        Long originUserId = kickUser.getId();

        chatUser.changeType(); // 강퇴된 유저의 type 을 "KICKED"로 변경
        matchingPartnerService.updateFalse(roomId, originUserId);  // 강퇴된 유저의 inChatRoomTrueFalse 값을 false로 변경

        List<ChatMessage> chatMessages = chatRoom.getChatMessages();

        chatMessages.stream()
                .filter(chatMessage -> chatMessage.getSender().getId().equals(chatUserId))
                .forEach(chatMessage -> chatMessage.removeChatMessages("강퇴된 사용자의 메시지입니다."));

        // TODO: 유저가 강퇴 시 매칭파트너에서도 삭제
        MatchingPartner matchingPartner = matchingPartnerService.findByMatchingIdAndUserId(roomId, originUserId);
        log.info("kick matchingPartner = {}", matchingPartner);
        log.info("kick originUserId = {}", originUserId);
        matchingPartnerRepository.delete(matchingPartner);


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
        chatRoom.getMatching().updateParticipant(commonParticipantsCount);
    }

    @Transactional
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

        publisher.publishEvent(new EventAfterInvite(this, invitingUser, invitedUser, chatRoom));

        return RsData.of("S-1", "%s 님에게 초대를 보냈습니다".formatted(invitedUser.getUsername()));
    }

    public boolean isDuplicateInvite(Long roomId, Long invitingUserId, Long invitedUserId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        // 현재 로그인된 사용자가 방에 있는지 확인하는 로직
        User invitingUser = userService.findByIdElseThrow(invitingUserId);
        ChatUser chatUserByUserId = findChatUserByUserId(chatRoom, invitingUserId);

        if (chatUserByUserId == null){
            throw new IllegalArgumentException("현재 당신은 %s 방에 들어있지 않습니다.".formatted(chatRoom.getName()));
        }

        User invitedUser = userService.findByIdElseThrow(invitedUserId);

        if (invitingUser.getId() != null && invitingUser.getId().equals(invitedUser.getId())) {
            throw new IllegalArgumentException("본인을 초대할 수 없습니다.");
        }

        // 현재 DB에 정보가 있으면 더 이상 저장이 되지 않게
        return notificationService.checkDuplicateInvite(invitingUser, invitedUser, roomId);
    }

    // chatUser type이 Exit라면 true / 아니라면 false 그래서 방에 못들어감
    private boolean isExitUser(ChatRoom chatRoom, Long userId) {
        // 방에 해당 유저가 있는지 확인
        User currentUser = userService.findById(userId).orElseThrow();
        ChatUser exitUser = chatUserService.findByChatRoomAndUser(chatRoom, currentUser);

        // stream 버전
//        ChatUser existingUser = chatRoom.getChatUsers().stream()
//                .filter(chatUser -> chatUser.getUser().getId().equals(userId))
//                .findFirst()
//                .orElseThrow();

        log.info("exitUser.getType() = {}", exitUser.getType());

        if (exitUser.getType().equals(EXIT)) {
            return true;
        } else {
            return false;
        }
    }
}