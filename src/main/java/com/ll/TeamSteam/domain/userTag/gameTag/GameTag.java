package com.ll.TeamSteam.domain.userTag.gameTag;

import com.ll.TeamSteam.domain.userTag.UserTag;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@RequiredArgsConstructor
@SuperBuilder
public class GameTag extends BaseEntity {

    private String gameName;

    private TagOn tagOn; // On Off로 조절
    @ManyToOne
    private UserTag userTag;

}
