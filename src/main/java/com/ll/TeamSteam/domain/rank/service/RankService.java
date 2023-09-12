package com.ll.TeamSteam.domain.rank.service;

import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> getTopSeven() {
        return userRepository.findTop7ByOrderByTemperatureDesc();
    }

    /**
     * @param id
     * @return <UserRank, User>
     */
    public Long getMyRank(Long id){
        List<User> userList = userRepository.findAllByOrderByTemperatureDesc();

        User user = userRepository.findById(id).orElseThrow();
        long myRankNum = userList.indexOf(user);

//        Map<Long, User> myRank = new HashMap<>();
//        myRank.put(myRankNum, user);
        return myRankNum;
    }

}
