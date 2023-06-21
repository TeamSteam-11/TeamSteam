package com.ll.TeamSteam.domain.userTag.entity;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
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
public class userTag extends BaseEntity {
    private GenreTagType type;

    private String gameTag;

    @ManyToOne(fetch = LAZY)
    private User user;
}
