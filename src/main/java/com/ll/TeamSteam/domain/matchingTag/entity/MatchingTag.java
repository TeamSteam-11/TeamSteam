package com.ll.TeamSteam.domain.matchingTag.entity;

import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
public class MatchingTag extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private GenreTagType genre;
    private String gameTag;

}
