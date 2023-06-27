package com.ll.TeamSteam.domain.user.entity;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;
import static jakarta.persistence.EnumType.STRING;
import java.util.ArrayList;


@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Enumerated(EnumType.STRING)
    private Gender type;

    private String avatar;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Matching> matchingList = new ArrayList<>();



    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
    public void setType(Gender type) {
        this.type = type;
    }
}
