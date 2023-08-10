package com.ll.TeamSteam.domain.dm.service;

import com.ll.TeamSteam.domain.dm.entity.Dm;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.security.SecurityUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class DmServiceTest {

    @Autowired
    private DmService dmService;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("채팅하기 누를 경우 DM이 생성된다.")
    void createDm() throws Exception  {
        User senderId = userService.findByIdElseThrow(1L);
        User receiverId = userService.findByIdElseThrow(2L);

        Dm dm = dmService.createDmAndEnterDm(receiverId.getId(), senderId.getId());

        assertTrue(dm != null);
        assertThat(dm.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("자신에게는 채팅을 할 수 없다.")
    void NoSelfDm() throws Exception {
        User senderId = userService.findByIdElseThrow(1L);
        User receiverId = userService.findByIdElseThrow(1L);

        assertThrows(IllegalArgumentException.class, () -> dmService.notSelfDm(senderId.getId(), receiverId.getId()));
    }

    @Test
    @DisplayName("Dm은 sender와 receiver가 아니면 들어갈 수 없다.")
    void noEnterThirdPerson() throws Exception {
        User senderId = userService.findByIdElseThrow(1L);
        User receiverId = userService.findByIdElseThrow(2L);
        SecurityUser thirdPerson = new SecurityUser(3L, "user3", "8888888888");

        Dm dm = dmService.createDmAndEnterDm(receiverId.getId(), senderId.getId());

        assertThrows(IllegalArgumentException.class, () -> dmService.notEnterThirdPerson(dm, thirdPerson));
    }

}