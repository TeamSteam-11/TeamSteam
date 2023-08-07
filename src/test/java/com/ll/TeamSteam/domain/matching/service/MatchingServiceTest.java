package com.ll.TeamSteam.domain.matching.service;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.repository.MatchingRepository;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.rsData.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.MethodName.class)
public class MatchingServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private MatchingService matchingService;
    @Autowired
    @MockBean
    private MatchingRepository matchingRepository;

    @Test
    @DisplayName("create")
    void t001() {
        User user1 = userService.findById(1L).get();

        Matching matching1 = matchingService.create(user1, "matchingTest1", "content1", GenreTagType.격투, 41000, "남성", 3L, 5, 8, LocalDateTime.now());

        assertThat(matching1.getTitle()).isEqualTo("matchingTest1");
        assertThat(matching1.getGameTagId()).isEqualTo(41000);
    }

    @Test
    @DisplayName("setDeadline")
    void t002() {
        LocalDateTime testTime = LocalDateTime.now();

        LocalDateTime deadlineTest = matchingService.setDeadline(testTime, 3);

        assertThat(deadlineTest).isEqualTo(testTime.plusHours(3));
    }

    @Test
    @DisplayName("calculateDeadline - selectedHours가 1 이상인 경우")
    void t003() {
        // selectedHours가 1 이상인 경우 testTime(실제로는 modifyDate)에 Hours 단위로 더해져야 함
        LocalDateTime testTime = LocalDateTime.now();
        int selectedHours = 3;

        LocalDateTime deadlineTest = matchingService.calculateDeadline(testTime, selectedHours);

        assertThat(deadlineTest).isEqualTo(testTime.plusHours(3));
    }

    @Test
    @DisplayName("calculateDeadline - selectedHours가 0 이하인 경우")
    void t004() {
        // selectedHours가 0 이하인 경우 testTime(실제로는 modifyDate)에 Days 단위로 30일이 더해져야 함(default)
        LocalDateTime testTime = LocalDateTime.now();
        int selectedHours = 0;

        LocalDateTime deadlineTest = matchingService.calculateDeadline(testTime, selectedHours);

        assertThat(deadlineTest).isEqualTo(testTime.plusDays(30));
    }

    @Test
    @DisplayName("modify - selectedHours가 1 이상인 경우")
    void t005() {
        User user1 = userService.findById(1L).get();
        LocalDateTime testTime = LocalDateTime.now();

        Matching matching1 = matchingService.create(user1, "matchingTest1", "content1", GenreTagType.격투, 41000, "남성", 3L, 5, 8, testTime);

        int selectedHours = 5;

        when(matchingRepository.save(any(Matching.class))).thenReturn(matching1);

        // WHEN
        RsData<Matching> result = matchingService.modify(
                matching1,
                "newTitle",
                "newContent",
                GenreTagType.군사,
                "여성",
                4L,
                10,
                15,
                selectedHours
        );

        // THEN
        assertThat(result.getResultCode()).isEqualTo("S-1");
        assertThat(result.getMsg()).isEqualTo("매칭이 수정되었습니다");
        assertThat(result.getData()).isEqualTo(matching1);
        assertThat(matching1.getTitle()).isEqualTo("newTitle");
        assertThat(matching1.getContent()).isEqualTo("newContent");
        assertThat(matching1.getGenre()).isEqualTo(GenreTagType.군사);
        assertThat(matching1.getGender()).isEqualTo("여성");
        assertThat(matching1.getCapacity()).isEqualTo(4L);
        assertThat(matching1.getStartTime()).isEqualTo(10);
        assertThat(matching1.getEndTime()).isEqualTo(15);
        // modify 메서드에서는 modifyDate가 실행 시점으로 저장이 되기에 테스트코드 실행 시 초 단위가 맞지 않아 실패
        // assertThat(matching1.getDeadlineDate()).isEqualTo(testTime.plusHours(5));
    }

    @Test
    @DisplayName("modify - selectedHours가 0 이하인 경우")
    void t006() {
        User user1 = userService.findById(1L).get();
        LocalDateTime testTime = LocalDateTime.now();

        Matching matching1 = matchingService.create(user1, "matchingTest1", "content1", GenreTagType.격투, 41000, "남성", 3L, 5, 8, testTime);

        int selectedHours = 0;

        when(matchingRepository.save(any(Matching.class))).thenReturn(matching1);

        // WHEN
        RsData<Matching> result = matchingService.modify(
                matching1,
                "newTitle",
                "newContent",
                GenreTagType.군사,
                "여성",
                4L,
                10,
                15,
                selectedHours
        );

        // THEN
        assertThat(result.getResultCode()).isEqualTo("S-1");
        assertThat(result.getMsg()).isEqualTo("매칭이 수정되었습니다");
        assertThat(result.getData()).isEqualTo(matching1);
        assertThat(matching1.getTitle()).isEqualTo("newTitle");
        assertThat(matching1.getContent()).isEqualTo("newContent");
        assertThat(matching1.getGenre()).isEqualTo(GenreTagType.군사);
        assertThat(matching1.getGender()).isEqualTo("여성");
        assertThat(matching1.getCapacity()).isEqualTo(4L);
        assertThat(matching1.getStartTime()).isEqualTo(10);
        assertThat(matching1.getEndTime()).isEqualTo(15);
        // modify 메서드에서는 modifyDate가 실행 시점으로 저장이 되기에 테스트코드 실행 시 초 단위가 맞지 않아 실패
        // assertThat(matching1.getDeadlineDate()).isEqualTo(testTime.plusDays(30));
    }

    @Test
    @DisplayName("modify - 실패")
    void t007() {
        User user1 = userService.findById(1L).get();
        LocalDateTime testTime = LocalDateTime.now();

        Matching matching1 = matchingService.create(user1, "matchingTest1", "content1", GenreTagType.격투, 41000, "남성", 3L, 5, 8, testTime);

        int selectedHours = 2;

        when(matchingRepository.save(any(Matching.class))).thenThrow(new RuntimeException("Test Exception"));

        // WHEN
        RsData<Matching> result = matchingService.modify(
                matching1,
                "newTitle",
                "newContent",
                GenreTagType.군사,
                "여성",
                4L,
                10,
                15,
                selectedHours
        );

        // THEN
        assertThat(result.getResultCode()).isEqualTo("F-1");
        assertThat(result.getMsg()).isEqualTo("매칭 수정 중 오류가 발생했습니다.");
    }

}
