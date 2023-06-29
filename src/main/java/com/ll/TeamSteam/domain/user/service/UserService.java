package com.ll.TeamSteam.domain.user.service;

import com.ll.TeamSteam.domain.user.entity.Gender;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import com.ll.TeamSteam.global.security.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    @Transactional(readOnly = true)
    public Optional<User> findBySteamId(String steamId) {
        return userRepository.findBySteamId(steamId);
    }



    @Transactional
    public void create(UserInfoResponse userInfo) {

        User createdUser = createUser(userInfo);
        userRepository.save(createdUser);
    }


    private User createUser(UserInfoResponse userInfo) {
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
}
