package com.ll.TeamSteam.domain.recentlyUser.entity;


import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
public class RecentlyUser extends BaseEntity {

    Long partnerUserId;

    @ManyToOne(fetch = LAZY)
    private User user;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.REMOVE)
    private MatchingPartner matchingPartner;

    public void setUser (User user) {
        if (this.user != null) {
            this.user.getRecentlyUsers().remove(this);
        }
        this.user = user;
        user.getRecentlyUsers().add(this);
    }

    public void setMatchingPartner (MatchingPartner matchingPartner) {
        if (this.matchingPartner != null) {
            this.matchingPartner.getRecentlyUserList().remove(this);
        }
        this.matchingPartner = matchingPartner;
        matchingPartner.getRecentlyUserList().add(this);
    }

}
