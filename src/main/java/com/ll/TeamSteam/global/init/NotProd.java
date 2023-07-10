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
                        .steamId("12412412411")
                        .temperature(37)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userRepository.save(user1);

                User user2 = User.builder()
                        .id(2L)
                        .username("user2")
                        .steamId("12412412412")
                        .temperature(40)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userRepository.save(user2);

                User user3 = User.builder()
                        .id(3L)
                        .username("user3")
                        .steamId("12412412413")
                        .temperature(25)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userRepository.save(user3);

                User user4 = User.builder()
                        .id(4L)
                        .username("user4")
                        .steamId("12412412414")
                        .temperature(66)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userRepository.save(user4);

                User user5 = User.builder()
                        .id(5L)
                        .username("user5")
                        .steamId("12412412415")
                        .temperature(80)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userRepository.save(user5);

                User user6 = User.builder()
                        .id(6L)
                        .username("user6")
                        .steamId("12412412416")
                        .temperature(75)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userRepository.save(user6);

                User user7 = User.builder()
                        .id(7L)
                        .username("user7")
                        .steamId("12412412417")
                        .temperature(53)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userRepository.save(user7);

                User user8 = User.builder()
                        .id(8L)
                        .username("user8")
                        .steamId("12412412418")
                        .temperature(50)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userRepository.save(user8);

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



                for (int i = 0; i <= 2; i++){

                    String title = "Matching " + i;
                    String content = "으악1";
                    GenreTagType genre = GenreTagType.valueOf("삼인칭슈팅");
                    int gameTagId = 41000;
                    String gender = "성별무관";
                    long capacity = 4L;
                    int startTime = 20;
                    int endTime = 269770;

                    Matching matching = matchingService.create(user1, title, content, genre, gameTagId, gender, capacity, startTime, endTime, null);
                    chatRoomService.createAndConnect(matching.getTitle(), matching, user1.getId());
                }
            }
        };
    }
}