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

    @ManyToOne
    private UserTag userTag;

}
