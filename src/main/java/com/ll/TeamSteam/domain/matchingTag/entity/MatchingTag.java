package com.ll.TeamSteam.domain.matchingTag.entity;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
public class MatchingTag extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private GenreTagType genre;
    private String gameTag;

}
