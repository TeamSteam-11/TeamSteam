package com.ll.TeamSteam.domain.user.service;

import com.ll.TeamSteam.domain.friend.entity.Friend;
import com.ll.TeamSteam.domain.friend.repository.FriendRepository;
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
import com.ll.TeamSteam.global.rsData.RsData;
import com.ll.TeamSteam.global.security.SecurityUser;
import com.ll.TeamSteam.global.security.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final UserTagRepository userTagRepository;

    private final GameTagRepository gameTagRepository;

    private final GenreTagRepository genreTagRepository;

    private final SteamGameLibraryRepository steamGameLibraryRepository;

    private final FriendRepository friendRepository;


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

        SteamGameLibrary steamGameLibrary = SteamGameLibrary.builder().user(createdUser).build();
        steamGameLibraryRepository.save(steamGameLibrary);
    }

    @Transactional
    public User createUser(UserInfoResponse userInfo) {
        String username = userInfo.getResponse().getPlayers().get(0).getPersonaname();
        String steamId = userInfo.getResponse().getPlayers().get(0).getSteamid();
        String avatar = userInfo.getResponse().getPlayers().get(0).getAvatarmedium();

        User user = new User(username, steamId, avatar);
        user.setTemperature(36);
        user.setType(Gender.Wait);

        return user;
    }

    @Transactional(readOnly = true)
    public User findByIdElseThrow(Long ownerId) {
        return userRepository.findById(ownerId).orElseThrow();
    }

    @Transactional
    public void updateUserData(String gender, List<GenreTagType> genreTagTypes, Long id) {

        User user = findById(id).orElseThrow();
        UserTag userTag = userTagRepository.findByUserId(id).orElseThrow();
        genreTagRepository.deleteAll(userTag.getGenreTag());

        //장르태그
        List<GenreTag> genreTags = new ArrayList<>();

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

    @Transactional(readOnly = true)
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }


    @Transactional
    public void updateGameList(List<SteamGameLibrary> gameList, String steamId) {
        List<SteamGameLibrary> gameLibraries = new ArrayList<>();

        User user = findBySteamId(steamId).orElseThrow();
        Long userId = user.getId();
        UserTag userTag = userTagRepository.findByUserId(userId).orElseThrow();
        gameTagRepository.deleteAll(userTag.getGameTag());
        if(!steamGameLibraryRepository.findAllByUserId(userId).isEmpty()){
            log.info("steamGameLibraryRepository.findAllByUserId(userId).isEmpty() = {}", steamGameLibraryRepository.findAllByUserId(userId).isEmpty());
            log.info("userId = {}", userId);
            steamGameLibraryRepository.deleteByUserId(userId);
        }

        for (SteamGameLibrary game : gameList) {
            SteamGameLibrary newGameLibrary = SteamGameLibrary.builder()
                .appid(game.getAppid())
                .name(game.getName())
                .user(userRepository.findBySteamId(steamId).orElseThrow())
                .build();

            gameLibraries.add(newGameLibrary);
        }

        steamGameLibraryRepository.saveAll(gameLibraries);
    }

    @Transactional
    public void updateTemperature(Long userId, int like) {
        log.info("like = {} ", like);
//        log.info("user.getTemperature = {}", user.getTemperature());
        User user = findById(userId).orElseThrow();
        int updateTemperature = user.getTemperature();

        if (like == 1) {
            updateTemperature++;
        }
        if (like == 0) {
            updateTemperature--;
        }

        user.setTemperature(updateTemperature);

        userRepository.save(user);
    }


    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void addFriends(Long targetId,Long loginedId){

        if(!isFriend(targetId, loginedId)) {//친구가 아닐 때
            //서로 저장
            Friend meToFriends = Friend.builder()
                    .user(findByIdElseThrow(loginedId))
                    .friend(findByIdElseThrow(targetId))
                    .build();
            Friend friendToMe = Friend.builder()
                    .user(findByIdElseThrow(targetId))
                    .friend(findByIdElseThrow(loginedId))
                    .build();


            friendRepository.save(meToFriends);
            friendRepository.save(friendToMe);
        }
        else{//친구일 때


        }
    }

    public boolean isFriend(Long targetId, Long loginedId){
        List<Friend> friends = getFriends(loginedId);

        for(Friend friend : friends){
            if(friend.getFriend().getId() == targetId) return true;
        }
        return false;
    }

    @Transactional
    public void saveSelectedGames(List<Integer> selectedGames, String steamId) {

        User user = userRepository.findBySteamId(steamId).orElseThrow();

        gameTagRepository.deleteAll();


        for (Integer appId : selectedGames) {
            SteamGameLibrary gameLibrary = steamGameLibraryRepository.findByAppid(appId);
            if (gameLibrary != null) {
                GameTag gameTag = new GameTag();
                gameTag.setAppid(gameLibrary.getAppid());
                gameTag.setName(gameLibrary.getName());
                gameTag.setUserTag(user.getUserTag());


                gameTagRepository.save(gameTag);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Friend> getFriends(Long userId) {
        return friendRepository.findAllByUserId(userId);
    }

}
