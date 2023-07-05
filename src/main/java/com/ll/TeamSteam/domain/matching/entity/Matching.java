package com.ll.TeamSteam.domain.matching.entity;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.matchingTag.entity.MatchingTag;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
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

    @Enumerated(EnumType.STRING)
    private GenreTagType genre;

    private Integer gameTagId;

    private Long capacity; // 모집 인원

    private Long participant; // 참가자 수

    private int startTime; // 원하는 시간대 시작

    private int endTime; // 원하는 시간대 끝

    private LocalDateTime deadlineDate; // 마감 시간

    @OneToOne(mappedBy = "matching", fetch = LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void update(String title, String content, Long capacity, int startTime, int endTime) {
        this.title = title;
        this.content = content;
        this.capacity = capacity;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}