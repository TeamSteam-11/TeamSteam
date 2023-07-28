package com.ll.TeamSteam.domain.genreTag.entity;

import static jakarta.persistence.FetchType.*;

import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.userTag.entity.UserTag;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@RequiredArgsConstructor
@SuperBuilder
public class GenreTag extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private GenreTagType genre;
    @ManyToOne(fetch = LAZY)
    private UserTag userTag;

}
