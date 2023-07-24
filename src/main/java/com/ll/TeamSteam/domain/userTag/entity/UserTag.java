package com.ll.TeamSteam.domain.userTag.entity;

import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.gameTag.entity.GameTag;
import com.ll.TeamSteam.domain.genreTag.entity.GenreTag;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@RequiredArgsConstructor
@SuperBuilder
@Getter
public class UserTag extends BaseEntity {


    @OneToOne
    private User user;

    public UserTag(User user) {
        this.user = user;
    }

    @OneToMany(mappedBy = "userTag")
    private List<GameTag> gameTag = new ArrayList<>();

    @OneToMany(mappedBy = "userTag")
    private List<GenreTag> genreTag = new ArrayList<>();




}
