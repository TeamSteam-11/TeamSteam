package com.ll.TeamSteam.domain.user.entity;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

import java.util.ArrayList;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(nullable = false)
    private String username;

    private String serialNumber;

    private int temperature;

    private Gender type;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Matching> matchingList = new ArrayList<>();
}
