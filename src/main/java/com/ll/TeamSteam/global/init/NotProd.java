package com.ll.TeamSteam.global.init;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.service.ChatRoomService;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.service.MatchingService;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import com.ll.TeamSteam.domain.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile({"local"})
@Slf4j
public class NotProd {
    @Bean
    CommandLineRunner initData(
            UserService userService,
            ChatRoomService chatRoomService,
            UserRepository userRepository,
            MatchingService matchingService

    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {
                User user1 = User.builder()
                        .id(1L)
                        .username("user1")
                        .steamId("7777777777")
                        .build();

                userRepository.save(user1);

                User user2 = User.builder()
                        .id(2L)
                        .username("user2")
                        .steamId("8888888888")
                        .build();

                userRepository.save(user2);

                log.info("user1 = {}", user1);

                Matching matching1 = matchingService.create("오늘 한강에서 러닝하실 분 구합니다!!",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", 8L).getData();

                Matching matching2 = matchingService.create("오늘 한강에서 러닝하실 분 구합니다!!",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", 4L).getData();

                log.info("matching1 = {}", matching1);
                log.info("matching2 = {}", matching2);


                ChatRoom chatRoom1 = chatRoomService.createAndConnect(matching1.getTitle(), matching1, user1.getId());
                ChatRoom chatRoom2 = chatRoomService.createAndConnect(matching2.getTitle(), matching2, user2.getId());
            }
        };
    }
}