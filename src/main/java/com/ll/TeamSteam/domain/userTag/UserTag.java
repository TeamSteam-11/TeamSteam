package com.ll.TeamSteam.domain.userTag;

import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.userTag.gameTag.GameTag;
import com.ll.TeamSteam.domain.userTag.genreTag.GenreTag;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.util.ArrayList;
import java.util.List;

@Entity
public class UserTag extends BaseEntity {


    @OneToOne
    private User user;

    @OneToMany(mappedBy = "userTag")
    private List<GameTag> gameTag = new ArrayList<>();

    @OneToMany(mappedBy = "userTag")
    private List<GenreTag> genreTag = new ArrayList<>();

}
