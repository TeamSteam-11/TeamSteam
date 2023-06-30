package com.ll.TeamSteam.domain.userTag.genreTag;

import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.user.entity.Gender;
import com.ll.TeamSteam.domain.userTag.UserTag;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@RequiredArgsConstructor
@SuperBuilder
public class GenreTag extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private GenreTagType genre;
    @ManyToOne
    private UserTag userTag;

}
