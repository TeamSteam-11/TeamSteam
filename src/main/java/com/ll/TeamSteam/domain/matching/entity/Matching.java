package com.ll.TeamSteam.domain.matching.entity;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
public class Matching extends BaseEntity {

    @NotBlank
    private String title;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    private GenreTagType genre;

    @NotNull
    private Integer gameTagId;
    private String gameTagName;

    @Min(value = 2)
    @Max(value = 10)
    private Long capacity; // 모집 인원

    private Long participant; // 참가자 수

    private Integer startTime; // 원하는 시간대 시작

    private Integer endTime; // 원하는 시간대 끝

    private LocalDateTime deadlineDate; // 마감 시간

    private String gender; // 성별

    private Boolean isMic; // 마이크

    @OneToOne(mappedBy = "matching", fetch = LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    private ChatRoom chatRoom;
    @OneToMany(mappedBy = "matching", cascade = CascadeType.REMOVE)
    private List<MatchingPartner> matchingPartners = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void update(String title, String content, GenreTagType genre, String gender, Long capacity, Integer startTime, Integer endTime, Boolean isMic) {
        this.title = title;
        this.content = content;
        this.genre = genre;
        this.gender = gender;
        this.capacity = capacity;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isMic = isMic;
    }

    public void updateParticipant(Long participant) {
        this.participant = participant;
    }

    public boolean canAddParticipant() {
        return this.participant < this.capacity;
    }

    // 마감 시간과 현재 시간 간 차이를 나타내는 메서드
    public String getDeadlineDuration() {
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(currentTime, getDeadlineDate());

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        if (hours <= 0 && minutes <= 0 && seconds <= 0) {
            return null;
        }

        String result = String.format("%d시간 %d분 %d초", hours, minutes, seconds);
        return result;
    }

    public Long getRemainingCapacity() {
        return capacity - participant;
    }

    // 현재는 사용 x
    public void deleteMatchingPartner(MatchingPartner matchingPartner){
        this.getMatchingPartners().remove(matchingPartner);
        matchingPartner.setMatching(null);
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

}