package com.ll.TeamSteam.domain.user.service;

import com.ll.TeamSteam.domain.user.entity.Gender;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import com.ll.TeamSteam.global.security.UserInfoResponse;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findByUsername(String username) {
        return null;
    }



    @Transactional
    public void create(UserInfoResponse userInfo) {
        String steamId = userInfo.getResponse().getPlayers().get(0).getSteamid();
        if (userRepository.findBySteamId(steamId).isPresent()) {
            return;
        }
        User createdUser = createUser(userInfo);
        userRepository.save(createdUser);
    }

    private User createUser(UserInfoResponse userInfo) {
        String username = userInfo.getResponse().getPlayers().get(0).getPersonaname();
        String steamId = userInfo.getResponse().getPlayers().get(0).getSteamid();
        String avatar = userInfo.getResponse().getPlayers().get(0).getAvatarmedium();

        User user = new User(username,steamId,avatar);
                user.setTemperature(36);
                user.setType(Gender.남성);

        return user;
    }

}
