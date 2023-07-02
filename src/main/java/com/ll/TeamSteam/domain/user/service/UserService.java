package com.ll.TeamSteam.domain.user.service;

import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary;
import com.ll.TeamSteam.domain.steam.repository.SteamGameLibraryRepository;
import com.ll.TeamSteam.domain.user.entity.Gender;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.userTag.gameTag.GameTagRepository;
import com.ll.TeamSteam.domain.userTag.genreTag.GenreTagRepository;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import com.ll.TeamSteam.domain.userTag.UserTagRepository;
import com.ll.TeamSteam.domain.userTag.UserTag;
import com.ll.TeamSteam.domain.userTag.gameTag.GameTag;
import com.ll.TeamSteam.domain.userTag.genreTag.GenreTag;
import com.ll.TeamSteam.global.security.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final UserTagRepository userTagRepository;

    private final GameTagRepository gameTagRepository;

    private final GenreTagRepository genreTagRepository;

    private final SteamGameLibraryRepository steamGameLibraryRepository;


    @Transactional(readOnly = true)
    public Optional<User> findBySteamId(String steamId) {
        return userRepository.findBySteamId(steamId);
    }



    @Transactional
    public void create(UserInfoResponse userInfo) {

        User createdUser = createUser(userInfo);
        userRepository.save(createdUser);

        UserTag userTag = UserTag.builder().user(createdUser).build();
        userTagRepository.save(userTag);

        GenreTag genreTag = GenreTag.builder().userTag(userTag).build();
        genreTagRepository.save(genreTag);

        GameTag gameTag = GameTag.builder().userTag(userTag).build();
        gameTagRepository.save(gameTag);

        SteamGameLibrary steamGameLibrary =SteamGameLibrary.builder().user(createdUser).build();
        steamGameLibraryRepository.save(steamGameLibrary);
    }

    @Transactional
    public User createUser(UserInfoResponse userInfo) {
        String username = userInfo.getResponse().getPlayers().get(0).getPersonaname();
        String steamId = userInfo.getResponse().getPlayers().get(0).getSteamid();
        String avatar = userInfo.getResponse().getPlayers().get(0).getAvatarmedium();

        User user = new User(username,steamId,avatar);
        user.setTemperature(36);
        user.setType(Gender.Wait);

        return user;
    }

    public User findByIdElseThrow(Long ownerId) {
        return userRepository.findById(ownerId).orElseThrow();
    }

    @Transactional
    public void updateUserData(String gender, List<GenreTagType> genreTagTypes, Long id){
        User user = findById(id).orElseThrow();
        UserTag userTag = userTagRepository.findByUserId(id).orElseThrow();
        genreTagRepository.deleteAll(userTag.getGenreTag());

        List<GenreTag> genreTags = new ArrayList<>();

        // GenreTagType enum 값을 이용하여 GenreTag 생성 후 리스트에 추가
        for (GenreTagType genreTagType : genreTagTypes) {
            GenreTag genreTag = GenreTag.builder()
                    .genre(genreTagType)
                    .userTag(userTag)
                    .build();
            genreTags.add(genreTag);
        }

        genreTagRepository.saveAll(genreTags);




        // Gender 업데이트
        user.setType(Gender.valueOf(gender));

        // 변경된 데이터 저장
        userRepository.save(user);
        userTagRepository.save(userTag);

    }

    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }


    public void saveGameList(List<SteamGameLibrary> gameList, String steamId) {
        List<SteamGameLibrary> gameLibraries = new ArrayList<>();

        for (SteamGameLibrary game : gameList) {
            SteamGameLibrary newGameLibrary = SteamGameLibrary.builder()
                .appid(game.getAppid())
                .name(game.getName())
                .imageUrl(game.getImageUrl())
                .user(userRepository.findBySteamId(steamId).orElseThrow())
                .build();
            gameLibraries.add(newGameLibrary);
        }

        steamGameLibraryRepository.saveAll(gameLibraries);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
