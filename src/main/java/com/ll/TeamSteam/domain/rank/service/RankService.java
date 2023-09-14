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
    public List<User> getTopUserList() {
        return userRepository.findTop10ByOrderByTemperatureDesc();
    }

    /**
     * 해당 유저의 아이디를 입력받아 온도순위 출력
     *
     * @param id - 온도순위를 받을 유저의 id
     * @return myRankNum - 나의 순위를 리턴
     */
    public Long getUserOfTemperatureRank(Long id) {
        List<User> userList = userRepository.findAllByOrderByTemperatureDesc();

        User user = userRepository.findById(id).orElseThrow();
        long myRankNum = userList.indexOf(user);

        return myRankNum;
    }

}
