package com.ll.TeamSteam.domain.userTag.genreTag;

import com.ll.TeamSteam.domain.userTag.UserTag;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class GenreTag extends BaseEntity {

    @ManyToOne
    private UserTag userTag;

}
