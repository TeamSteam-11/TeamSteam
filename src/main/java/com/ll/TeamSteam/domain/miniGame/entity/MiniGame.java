package com.ll.TeamSteam.domain.miniGame.entity;

import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
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
public class MiniGame extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    private User user;

    private Long score;

    public void updateScore(Long score) {
        this.score = score;
    }
}
