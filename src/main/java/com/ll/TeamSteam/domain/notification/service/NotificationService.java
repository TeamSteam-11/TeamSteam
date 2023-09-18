package com.ll.TeamSteam.domain.notification.service;

import com.ll.TeamSteam.domain.dm.entity.Dm;
import com.ll.TeamSteam.domain.notification.entity.Notification;
import com.ll.TeamSteam.domain.notification.repository.NotificationRepository;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Page<Notification> findByInvitedUser(User user, Pageable pageable) {
        return notificationRepository.findByInvitedUserOrderByIdDesc(user, pageable);
    }

    @Transactional
    public void markAsRead(User invitedUser, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByInvitedUserOrderByIdDesc(invitedUser, pageable);
        notifications.stream()
                .filter(notification -> !notification.isRead())
                .forEach(Notification::markAsRead);
    }

    public void makeLike(User invitingUser, User invitedUser, Long roomId, String roomName, boolean enterAlarm) {
        createAndSaveNotification(invitingUser, invitedUser, roomId, roomName, enterAlarm);
    }

    public RsData<Notification> createAndSaveNotification(User invitingUser, User invitedUser, Long roomId, String roomName, boolean enterAlarm) {

        Notification notification = Notification.builder()
                .invitingUser(invitingUser)
                .invitedUser(invitedUser)
                .roomId(roomId)
                .matchingName(roomName)
                .enterAlarm(enterAlarm)
                .build();

        notificationRepository.save(notification);

        return RsData.of("S-1", "알림 메세지가 생성되었습니다.", notification);
    }

    @Transactional
    public Notification deleteNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        notificationRepository.deleteById(notificationId);

        return notification;
    }

    public Notification findById (Long notificationId) {
        return notificationRepository.findById(notificationId).orElseThrow();
    }

    public boolean countUnreadNotificationsByInvitedUser(User user) {
        return notificationRepository.countByInvitedUserAndReadDateIsNull(user) > 0;
    }

    public boolean checkDuplicateInvite(User invitingUser, User invitedUser, Long roomId) {
        return notificationRepository.existsByInvitingUserAndInvitedUserAndRoomId(invitingUser, invitedUser, roomId);
    }

    public void makeFriend(User invitingUser, User invitedUser) {
        createAndSaveNotification(invitingUser, invitedUser);
    }

    public boolean isDupNotification(User invitingUser, User invitedUser){
        if(notificationRepository.findByInvitingUserAndInvitedUser(invitingUser,invitedUser).isPresent()){
            return true;
        }
        return false;
    }

    public RsData<Notification> createAndSaveNotification(User invitingUser, User invitedUser) {

        Notification notification = Notification.builder()
                .invitingUser(invitingUser)
                .invitedUser(invitedUser)
                .build();

        notificationRepository.save(notification);

        return RsData.of("S-1", "알림 메세지가 생성되었습니다.", notification);
    }

    public void makeDmCreateAlarm(Dm dm, User dmSender, User receiver) {
        createDmAlarm(dm, dmSender, receiver);
    }

    private void createDmAlarm(Dm dm, User dmSender, User receiver) {
        Notification notification = Notification.builder()
                .dmId(dm.getId())
                .invitingUser(dmSender)
                .invitedUser(receiver)
                .build();

        notificationRepository.save(notification);
    }
}