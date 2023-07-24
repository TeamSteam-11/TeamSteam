package com.ll.TeamSteam.domain.user.entity;

import com.ll.TeamSteam.domain.friend.entity.Friend;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser;
import com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary;
import com.ll.TeamSteam.domain.userTag.entity.UserTag;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;
import java.util.ArrayList;


@Entity
@Getter
@SuperBuilder
@RequiredArgsConstructor
@Table(name = "\"user\"")
public class User extends BaseEntity {



    public User(String username, String steamId, String avatar) {
        this.username = username;
        this.steamId = steamId;
        this.avatar = avatar;
        this.matchingPartners =new ArrayList<>();
        this.recentlyUsers = new ArrayList<>();
    }

    @Column(nullable = false)
    private String username;

    private String steamId;

    private int temperature;

    private String avatar;

    @Enumerated(EnumType.STRING)
    private Gender type;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Matching> matchingList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SteamGameLibrary> steamGameLibrary = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserTag userTag;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Friend> friendList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<MatchingPartner> matchingPartners;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RecentlyUser> recentlyUsers;



    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
    public void setType(Gender type) {
        this.type = type;
    }
    public void setUserTag(UserTag userTag){ this.userTag = userTag; }

    public void setAvatar(String avatar){ this.avatar = avatar;}

}
