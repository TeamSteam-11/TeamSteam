package com.ll.TeamSteam.domain.notification.repository;

import com.ll.TeamSteam.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ll.TeamSteam.domain.user.entity.User;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByInvitedUserOrderByIdDesc(User user);

}