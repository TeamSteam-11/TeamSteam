package com.ll.TeamSteam.domain.user.entity;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.userTag.UserTag;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;
import java.util.ArrayList;


@Entity
@Getter
@SuperBuilder
@RequiredArgsConstructor
public class User extends BaseEntity {



    public User(String username, String steamId, String avatar) {
        this.username = username;
        this.steamId = steamId;
        this.avatar = avatar;
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

    @OneToOne(mappedBy = "user")
    private UserTag userTag;



    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
    public void setType(Gender type) {
        this.type = type;
    }
    public void setUserTag(UserTag userTag){ this.userTag = userTag; }


    public List<? extends GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();


        return grantedAuthorities;
    }
}
