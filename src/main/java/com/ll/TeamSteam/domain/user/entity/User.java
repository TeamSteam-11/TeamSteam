package com.ll.TeamSteam.domain.user.entity;

import com.ll.TeamSteam.domain.friend.entity.Friend;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser;
import com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary;
import com.ll.TeamSteam.domain.userTag.entity.UserTag;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@RequiredArgsConstructor
@Table(name = "\"user\"", uniqueConstraints = @UniqueConstraint(name = "steamId", columnNames = {"steamId"}))
public class User extends BaseEntity{



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
    private List<Friend> friendList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<MatchingPartner> matchingPartners = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RecentlyUser> recentlyUsers = new ArrayList<>();



    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
    public void setType(Gender type) {
        this.type = type;
    }
    public void setUserTag(UserTag userTag){ this.userTag = userTag; }

    public void setAvatar(String avatar){ this.avatar = avatar;}

}
