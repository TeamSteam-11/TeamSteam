package com.ll.TeamSteam.domain.notification.service;

import com.ll.TeamSteam.domain.notification.entity.Notification;
import com.ll.TeamSteam.domain.notification.repository.NotificationRepository;
import com.ll.TeamSteam.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

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
}