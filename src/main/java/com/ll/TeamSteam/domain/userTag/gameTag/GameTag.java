package com.ll.TeamSteam.domain.userTag.gameTag;

import com.ll.TeamSteam.domain.userTag.UserTag;
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

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    private UserTag userTag;

    public void setAppid(Integer appid) {
        this.appid = appid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUserTag(UserTag userTag) {
        this.userTag = userTag;
    }
}
