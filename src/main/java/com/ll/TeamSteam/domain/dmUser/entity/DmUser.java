package com.ll.TeamSteam.domain.dmUser.entity;

import com.ll.TeamSteam.domain.dm.entity.Dm;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
@Slf4j
public class DmUser extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    private User user;

    @ManyToOne(fetch = LAZY)
    private Dm dm;

    @Builder
    public DmUser(User user, Dm dm) {
        this.user = user;
        this.dm = dm;
    }
}
