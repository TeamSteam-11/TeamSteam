package com.ll.TeamSteam.domain.dm.entity;

import com.ll.TeamSteam.domain.dmMessage.entity.DmMessage;
import com.ll.TeamSteam.domain.dmUser.entity.DmUser;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
@Slf4j
public class Dm extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    private User dmSender;

    @ManyToOne(fetch = LAZY)
    private User dmReceiver;

    @OneToMany(mappedBy = "dm", cascade = PERSIST, orphanRemoval = true)
    @Builder.Default
    private Set<DmUser> dmUsers = new HashSet<>();

    public static Dm create(User dmSender, User dmReceiver) {

        Dm dm = Dm.builder()
                .dmSender(dmSender)
                .dmReceiver(dmReceiver)
                .build();

        return dm;
    }

    public void addDmUser(User dmSender) {
        DmUser dmUser = DmUser.builder()
                .user(dmSender)
                .dm(this)
                .build();

        dmUsers.add(dmUser);
    }
}
