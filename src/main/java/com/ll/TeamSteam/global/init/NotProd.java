package com.ll.TeamSteam.global.init;

import com.ll.TeamSteam.domain.chatRoom.service.ChatRoomService;
import com.ll.TeamSteam.domain.gameTag.entity.GameTag;
import com.ll.TeamSteam.domain.gameTag.repository.GameTagRepository;
import com.ll.TeamSteam.domain.genreTag.entity.GenreTag;
import com.ll.TeamSteam.domain.genreTag.repository.GenreTagRepository;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.service.MatchingService;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.user.entity.Gender;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import com.ll.TeamSteam.domain.userTag.entity.UserTag;
import com.ll.TeamSteam.domain.userTag.repository.UserTagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile({"test"})
@Slf4j
public class NotProd {
    @Bean
    CommandLineRunner initData(
            ChatRoomService chatRoomService,
            UserRepository userRepository,
            MatchingService matchingService,
            UserTagRepository userTagRepository,
            GameTagRepository gameTagRepository,
            GenreTagRepository genreTagRepository
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {
                List<User> userList = new ArrayList<>(); // 유저태그를 담기위한 리스트

                User user1 = User.builder()
                        .id(1L)
                        .username("user1")
                        .steamId("12412412411")
                        .type(Gender.남성)
                        .temperature(37)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userList.add(user1);
                userRepository.save(user1);

                User user2 = User.builder()
                        .id(2L)
                        .username("user2")
                        .steamId("12412412412")
                        .type(Gender.여성)
                        .temperature(40)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userList.add(user2);
                userRepository.save(user2);

                User user3 = User.builder()
                        .id(3L)
                        .username("user3")
                        .steamId("12412412413")
                        .temperature(25)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userList.add(user3);
                userRepository.save(user3);

                User user4 = User.builder()
                        .id(4L)
                        .username("user4")
                        .steamId("12412412414")
                        .temperature(33)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userList.add(user4);
                userRepository.save(user4);

                User user5 = User.builder()
                        .id(5L)
                        .username("user5")
                        .steamId("12412412415")
                        .temperature(80)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userList.add(user5);
                userRepository.save(user5);

                User user6 = User.builder()
                        .id(6L)
                        .username("user6")
                        .steamId("12412412416")
                        .temperature(75)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userList.add(user6);
                userRepository.save(user6);

                User user7 = User.builder()
                        .id(7L)
                        .username("user7")
                        .steamId("12412412417")
                        .temperature(53)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userList.add(user7);
                userRepository.save(user7);

                User user8 = User.builder()
                        .id(8L)
                        .username("user8")
                        .steamId("12412412418")
                        .temperature(21)
                        .avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
                        .build();

                userList.add(user8);
                userRepository.save(user8);

                int appidPlus = 41000;
                for(User userEach : userList){
                    UserTag userTag =UserTag.builder()
                            .user(userEach)
                            .build();

                    userTagRepository.save(userTag);

                    userEach.setUserTag(userTag);

                    GameTag gameTag1 = GameTag.builder()
                            .userTag(userTag)
                            .appid(appidPlus++)
                            .name("시리우스샘")
                            .build();

                    gameTag1.setUserTag(userTag);
                    gameTagRepository.save(gameTag1);

                    GameTag gameTag2 = GameTag.builder()
                            .userTag(userTag)
                            .appid(appidPlus++)
                            .name("시리우스샘")
                            .build();

                    gameTag2.setUserTag(userTag);
                    gameTagRepository.save(gameTag2);

                    GameTag gameTag3 = GameTag.builder()
                            .userTag(userTag)
                            .appid(appidPlus++)
                            .name("시리우스샘")
                            .build();

                    gameTag3.setUserTag(userTag);
                    gameTagRepository.save(gameTag3);


                    GenreTag genreTag1 = GenreTag.builder()
                            .userTag(userTag)
                            .genre(GenreTagType.농업)
                            .build();

                    genreTagRepository.save(genreTag1);

                    GenreTag genreTag2 = GenreTag.builder()
                            .userTag(userTag)
                            .genre(GenreTagType.건설)
                            .build();

                    genreTagRepository.save(genreTag2);

                    GenreTag genreTag3 = GenreTag.builder()
                            .userTag(userTag)
                            .genre(GenreTagType.낚시)
                            .build();

                    genreTagRepository.save(genreTag3);

                    userRepository.save(userEach);
                }

                for (int i = 0; i <= 5; i++){

                    String title = "Matching " + i;
                    String content = "으악1";
                    GenreTagType genre = GenreTagType.valueOf("삼인칭슈팅");
                    int gameTagId = 41000;
                    String gender = "성별무관";
                    long capacity = 2L;
                    Integer startTime = 20;
                    Integer endTime = 22;
                    boolean mic = false;

                    Matching matching = matchingService.create(user1, title, content, genre, gameTagId, gender, capacity, startTime, endTime, LocalDateTime.now().plusHours(3), mic);
                    chatRoomService.createChatRoomAndConnectMatching(matching.getTitle(), matching, user1.getId());

                }
            }
        };
    }
}