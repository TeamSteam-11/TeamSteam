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

                Matching matching1 = matchingService.create(user1, "오늘 한강에서 러닝하실 분 구합니다!!",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),199932, 8L, 8, 10, null);
                Matching matching2 = matchingService.create(user2, "오늘 한강에서 러닝하실 분 구합니다!!",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),22222, 4L, 7, 9, null);
                Matching matching3 = matchingService.create(user1, "도ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ",
                        "ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ", GenreTagType.valueOf("삼인칭슈팅"),13579, 5L, 17, 19, null);
                Matching matching4 = matchingService.create(user2, "ㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎ",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),50000, 4L, 18, 20, null);
                Matching matching5 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching6 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching7 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching8 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching9 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching10 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching11 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching12 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching13 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching14 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching15 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching16 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching17 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching18 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching19 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching20 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching21 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching22 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching23 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching24 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching25 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching26 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);
                Matching matching27 = matchingService.create(user2, "코코코콬코코코코코코콬",
                        "한강에서 2시간 정도 같이 달리실 분 구합니다!", GenreTagType.valueOf("삼인칭슈팅"),41000, 4L, 20, 22, null);



                ChatRoom chatRoom1 = chatRoomService.createAndConnect(matching1.getTitle(), matching1, user1.getId());
                ChatRoom chatRoom2 = chatRoomService.createAndConnect(matching2.getTitle(), matching2, user2.getId());
                ChatRoom chatRoom3 = chatRoomService.createAndConnect(matching3.getTitle(), matching3, user2.getId());
                ChatRoom chatRoom4 = chatRoomService.createAndConnect(matching4.getTitle(), matching4, user2.getId());
                ChatRoom chatRoom5 = chatRoomService.createAndConnect(matching5.getTitle(), matching5, user2.getId());



            }
        };
    }
}