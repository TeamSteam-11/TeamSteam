package com.ll.TeamSteam.domain.notification.entity;

import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Notification extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    private User invitedUser; // 초대를 받은 사람

    @ManyToOne(fetch = LAZY)
    private User invitingUser; // 초대를 보낸 사람

    private Long roomId;
    private String matchingName;
    private LocalDateTime readDate;
    private Long dmId;

    public boolean isRead() {
        return readDate != null;
    }

    public void markAsRead() {
        readDate = LocalDateTime.now();
    }

    public boolean isHot() {
        // 만들어진지 60분이 안되었다면 hot 으로 설정
        return getCreateDate().isAfter(LocalDateTime.now().minusMinutes(60));
    }
}
