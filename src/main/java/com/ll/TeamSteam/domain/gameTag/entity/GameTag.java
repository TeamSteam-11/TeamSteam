package com.ll.TeamSteam.domain.gameTag.entity;

import com.ll.TeamSteam.domain.userTag.entity.UserTag;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@RequiredArgsConstructor
@SuperBuilder
public class GameTag extends BaseEntity {

    @Column(name = "appid")
    private Integer appid;

    @Column(name = "name")
    private String name;

    @ManyToOne
    private UserTag userTag;

    public void setAppid(Integer appid) {
        this.appid = appid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserTag(UserTag userTag) {
        this.userTag = userTag;
    }

    public GameTag(Integer appid, UserTag userTag, String name) {
        this.appid = appid;
        this.userTag = userTag;
        this.name = name;
    }
}
