package com.ll.TeamSteam.domain.matching.entity;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.matchingTag.entity.MatchingTag;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
public class Matching extends BaseEntity {

    private String title;

    private String content;

    private Long capacity; // 모집 인원

    private Long participant; // 참가자 수

    private int startTime; // 원하는 시간대 시작

    private int endTime; // 원하는 시간대 끝

    @OneToOne(mappedBy = "matching")
    private ChatRoom chatRoom;

    @OneToMany(mappedBy = "matching", cascade = CascadeType.ALL)
    private List<MatchingTag> matchingTagList = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}