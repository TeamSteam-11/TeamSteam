package com.ll.TeamSteam.domain.matchingPartner.entity;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
public class MatchingPartner extends BaseEntity {

    @Setter
    @ManyToOne(fetch = LAZY)
    private Matching matching;

    @ManyToOne(fetch = LAZY)
    private User user;

    private boolean inChatRoomTrueFalse;

    @OneToMany(mappedBy = "matchingPartner", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecentlyUser> recentlyUserList = new ArrayList<>();

    public void updateInChatRoomTrueFalse(boolean inChatRoomTrueFalse){
        this.inChatRoomTrueFalse = inChatRoomTrueFalse;
    }


}
