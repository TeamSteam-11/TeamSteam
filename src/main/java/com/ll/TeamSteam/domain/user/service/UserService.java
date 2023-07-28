package com.ll.TeamSteam.domain.user.service;

import com.ll.TeamSteam.domain.friend.entity.Friend;
import com.ll.TeamSteam.domain.friend.repository.FriendRepository;
import com.ll.TeamSteam.domain.friend.service.FriendService;
import com.ll.TeamSteam.domain.gameTag.service.GameTagService;
import com.ll.TeamSteam.domain.genreTag.service.GenreTagService;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary;
import com.ll.TeamSteam.domain.steam.repository.SteamGameLibraryRepository;
import com.ll.TeamSteam.domain.steam.service.SteamGameLibraryService;
import com.ll.TeamSteam.domain.user.entity.Gender;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.gameTag.repository.GameTagRepository;
import com.ll.TeamSteam.domain.genreTag.repository.GenreTagRepository;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import com.ll.TeamSteam.domain.userTag.repository.UserTagRepository;
import com.ll.TeamSteam.domain.userTag.entity.UserTag;
import com.ll.TeamSteam.domain.gameTag.entity.GameTag;
import com.ll.TeamSteam.domain.genreTag.entity.GenreTag;
import com.ll.TeamSteam.domain.userTag.service.UserTagService;
import com.ll.TeamSteam.global.security.UserInfoResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    //전부 서비스로 수정
    private final UserTagService userTagService;

    private final GameTagService gameTagService;

    private final GenreTagService genreTagService;

    private final SteamGameLibraryService steamGameLibraryService;

    private final FriendService friendService;


    @Transactional(readOnly = true)
    public Optional<User> findBySteamId(String steamId) {
        return userRepository.findBySteamId(steamId);
    }


    public void create(UserInfoResponse userInfo) {

        User createdUser = createUser(userInfo);
        userSave(createdUser);

        UserTag userTag = UserTag.builder().user(createdUser).build();
        userTagService.save(userTag);

        GenreTag genreTag = GenreTag.builder().userTag(userTag).build();
        genreTagService.save(genreTag);

        GameTag gameTag = GameTag.builder().userTag(userTag).build();
        gameTagService.save(gameTag);

        SteamGameLibrary steamGameLibrary = SteamGameLibrary.builder().user(createdUser).build();
        steamGameLibraryService.save(steamGameLibrary);
        //서비스 만들어서 서비스단에서 저장

    }

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
        log.info("user ={}",user);
        UserTag userTag = userTagService.findByUserId(id);
        log.info("userTag ={}",userTag);
        //userTag가 비어있을 때는 태그를 삭제하지 않음
        // if(Optional.ofNullable(userTag).isPresent()) {
        //     if (Optional.ofNullable(userTag.getGenreTag()).isPresent()) {
        //         deleteAllGenreTag(userTag);
        //     }
        // }
       deleteAllGenreTag(userTag);
        // genreTagRepository.deleteAll(userTag.getGenreTag());

        //장르태그
        List<GenreTag> genreTags = new ArrayList<>();
        log.info("genreTag ={}",genreTagTypes.size());
        log.info("genreTags ={}",genreTags.size());
        for (GenreTagType genreTagType : genreTagTypes) {
            GenreTag genreTag = GenreTag.builder()
                    .genre(genreTagType)
                    .userTag(userTag)
                    .build();

            log.info("genreTag ={}",genreTag);
            log.info("genreTag ={}",userTag != null);
            log.info("genreTag ={}",userTag.getGenreTag() != null);
            genreTags.add(genreTag);
        }
        log.info("genreTags ={}",genreTags.size());
        log.info("genreTags ={}",genreTags.get(0).getId());
        log.info("genreTags.get(0).getGenre() ={}",genreTags.get(0).getGenre());
        log.info("genreTags.getUserTag().getId() ={}",genreTags.get(0).getUserTag().getId());
        log.info("genreTags ={}",genreTags.size());
        log.info("genreTagService.saveAll(genreTags) ={}",genreTags);
        genreTagService.saveAll(genreTags);




        // Gender 업데이트
        user.setType(Gender.valueOf(gender));

        // 변경된 데이터 저장
        userTag.setGenreTag(genreTags);
        userSave(user);
        userTagService.save(userTag);

    }

    @Transactional
    public void deleteAllGenreTag(UserTag userTag){
        //Optional로 감싸 null인지 체크 후 널이 아닐 때만 deleteAll
//        Optional<UserTag> userTagcheck = Optional.ofNullable(userTag);
        log.info("userService.userTag", userTag.getGenreTag());
        genreTagService.deleteAll(userTag.getGenreTag());
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
        UserTag userTag = userTagService.findByUserId(userId);
        // if(Optional.ofNullable(userTag.getGameTag()).isPresent()) { //존재하면 지움
            gameTagService.deleteAll(userTag.getGameTag());
        // }
        if(!steamGameLibraryService.findAllByUserId(userId).isEmpty()){
            log.info("steamGameLibraryService.findAllByUserId(userId).isEmpty() = {}", steamGameLibraryService.findAllByUserId(userId).isEmpty());
            log.info("userId = {}", userId);
            steamGameLibraryService.deleteByUserId(userId);
        }

        for (SteamGameLibrary game : gameList) {
            SteamGameLibrary newGameLibrary = SteamGameLibrary.builder()
                .appid(game.getAppid())
                .name(game.getName())
                .user(userRepository.findBySteamId(steamId).orElseThrow())
                .build();

            gameLibraries.add(newGameLibrary);
        }

        steamGameLibraryService.saveAll(gameLibraries);
    }

    @Transactional
    public void updateTemperature(Long userId, int like) {
        User user = findById(userId).orElseThrow();
        int updateTemperature = user.getTemperature();

        if (like == 1) {
            updateTemperature++;
        }
        if (like == 0) {
            updateTemperature--;
        }

        user.setTemperature(updateTemperature);

        userSave(user);
    }

    @Transactional
    public void addFriends(Long targetId,Long loginedId){

        if(!isFriend(targetId, loginedId)) {//친구가 아닐 때 이중검증
            User targetUser = findByIdElseThrow(targetId);
            User loginedUser = findByIdElseThrow(loginedId);
            //서로 저장

            Friend meToFriends = Friend.builder()
                    .user(loginedUser)
                    .friend(targetUser)
                    .build();
            Friend friendToMe = Friend.builder()
                    .user(targetUser)
                    .friend(loginedUser)
                    .build();

            friendService.save(meToFriends);
            friendService.save(friendToMe);
        }
    }
    @Transactional
    public void deleteFriend(Long targetId, Long loginedId) {
        if (isFriend(targetId, loginedId)) {
            User targetUser = findByIdElseThrow(targetId);
            User loginedUser = findByIdElseThrow(loginedId);

            // 내가 친구인 관계 삭제
            Friend meToFriend = friendService.findByUserAndFriend(loginedUser, targetUser);
            friendService.delete(meToFriend);

            // 상대방이 나를 친구로 추가한 관계 삭제
            Friend friendToMe = friendService.findByUserAndFriend(targetUser, loginedUser);
            friendService.delete(friendToMe);
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

        gameTagService.deleteByUserTag(user.getUserTag());

        Set<Integer> distinctGameIds = new HashSet<>(); // 중복을 제거하기 위한 Set

        for (Integer appId : selectedGames) {
            if (!distinctGameIds.contains(appId)) { // 중복된 게임 ID가 아닌 경우에만 처리
                SteamGameLibrary gameLibrary = steamGameLibraryService.findByAppidAndUserId(appId, user.getId());
                if (gameLibrary != null) {
                    GameTag gameTag = new GameTag();
                    gameTag.setAppid(gameLibrary.getAppid());
                    gameTag.setName(gameLibrary.getName());
                    gameTag.setUserTag(user.getUserTag());

                    gameTagService.save(gameTag);
                }
                distinctGameIds.add(appId); // 처리한 게임 ID를 추가
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Friend> getFriends(Long userId) {
        return friendService.findAllByUserId(userId);
    }

    @Transactional
    public void updateUserAvatar(UserInfoResponse userInfo, Long userId) {

        User user = findByIdElseThrow(userId);
        user.setAvatar(userInfo.getResponse().getPlayers().get(0).getAvatarmedium());

        userRepository.save(user);
    }

    @Transactional
    public void userSave(User user){
        userRepository.save(user);
    }

}