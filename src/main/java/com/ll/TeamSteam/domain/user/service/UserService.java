package com.ll.TeamSteam.domain.user.service;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.*;

import com.ll.TeamSteam.domain.user.entity.Gender;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import com.ll.TeamSteam.global.security.SecurityUser;
import com.ll.TeamSteam.global.security.UserInfoResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
