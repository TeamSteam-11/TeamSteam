package com.ll.TeamSteam.global.init;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.service.ChatRoomService;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.service.MatchingService;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.domain.userTag.UserTag;
import com.ll.TeamSteam.domain.userTag.UserTagRepository;
import com.ll.TeamSteam.domain.userTag.gameTag.GameTag;
import com.ll.TeamSteam.domain.userTag.gameTag.GameTagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Profile({"local", "test"})
@Slf4j
public class NotProd {
    @Bean
    CommandLineRunner initData(
            ChatRoomService chatRoomService,
            UserRepository userRepository,
            MatchingService matchingService,
            UserTagRepository userTagRepository,
            GameTagRepository gameTagRepository

    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {

                User user1 = User.builder()
                        .id(1L)
                        .username("user1")
                        .steamId("12412412414")
                        .temperature(36)
                        .avatar("")
                        .build();

                userRepository.save(user1);

                User user2 = User.builder()
                        .id(2L)
                        .username("user2")
                        .steamId("8888888888")
                        .temperature(36)
                        .build();

                userRepository.save(user2);

                log.info("user1 = {}", user1);


                UserTag userTag =UserTag.builder()
                        .user(user1)
                        .build();


                userTagRepository.save(userTag);

                GameTag gameTag = GameTag.builder()
                        .appid(41000)
                        .name("시리우스샘")
                        .build();

                gameTag.setUserTag(userTag);

                gameTagRepository.save(gameTag);

                for (int i = 0; i <= 100; i++){

                    String title = "Matching " + i;
                    String content = "으악1";
                    GenreTagType genre = GenreTagType.valueOf("삼인칭슈팅");
                    int gameTagId = 41000;
                    long duration = 4L;
                    int startTime = 20;
                    int endTime = 269770;

                    Matching matching = matchingService.create(user2, title, content, genre, gameTagId, duration, startTime, endTime, null);

                    ChatRoom chatRoom = chatRoomService.createAndConnect(matching.getTitle(), matching, user2.getId());
                }
            }
        };
    }
}