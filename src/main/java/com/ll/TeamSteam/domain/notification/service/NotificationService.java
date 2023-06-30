package com.ll.TeamSteam.domain.notification.service;

import com.ll.TeamSteam.domain.notification.entity.Notification;
import com.ll.TeamSteam.domain.notification.repository.NotificationRepository;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final UserService userService;
    private final NotificationRepository notificationRepository;

    public List<Notification> findByInvitedUser(User user) {
        return notificationRepository.findByInvitedUserOrderByIdDesc(user);
    }

    @Transactional
    public void markAsRead(User invitedUser) {
        List<Notification> notifications = notificationRepository.findByInvitedUserOrderByIdDesc(invitedUser);
        notifications.stream()
                .filter(notification -> !notification.isRead())
                .forEach(Notification::markAsRead);
    }

    public void makeLike(User invitingUser, User invitedUser, Long roomId, String roomName) {
        createAndSaveNotification(invitingUser, invitedUser, roomId, roomName);
    }

    public RsData<Notification> createAndSaveNotification(User invitingUser, User invitedUser, Long roomId, String roomName) {

        Notification notification = Notification.builder()
                .invitingUser(invitingUser)
                .invitedUser(invitedUser)
                .roomId(roomId)
                .matchingName(roomName)
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
}