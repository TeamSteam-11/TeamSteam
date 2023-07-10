package com.ll.TeamSteam.domain.rank.service;

import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> getTopSeven() {
        return userRepository.findTopSevenByOrderByTemperatureDesc();
    }
}
