package com.ll.TeamSteam.domain.matchingPartner.entity;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
public class MatchingPartner extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Matching matching;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private boolean inChatRoomTrueFalse;
}
