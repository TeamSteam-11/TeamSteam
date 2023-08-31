package com.ll.TeamSteam.domain.notification.repository;

import com.ll.TeamSteam.domain.notification.entity.Notification;
import com.ll.TeamSteam.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByInvitedUserOrderByIdDesc(User user);

    int countByInvitedUserAndReadDateIsNull(User user);

    boolean existsByInvitingUserAndInvitedUserAndRoomId(User invitingUser, User invitedUser, Long roomId);
    Optional<Notification> findByInvitingUserAndInvitedUser(User invitingUser, User invitedUser);
}