package com.ll.TeamSteam.domain.matching.service;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.repository.MatchingRepository;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
    private MatchingRepository matchingRepository;

    @Test
    @DisplayName("create")
    void t001() throws Exception {
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

}
