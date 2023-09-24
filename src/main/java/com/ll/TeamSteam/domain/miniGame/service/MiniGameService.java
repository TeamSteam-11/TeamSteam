package com.ll.TeamSteam.domain.miniGame.service;

import com.ll.TeamSteam.domain.miniGame.entity.MiniGame;
import com.ll.TeamSteam.domain.miniGame.repository.MiniGameRepository;
import com.ll.TeamSteam.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MiniGameService {
    private final MiniGameRepository miniGameRepository;

    @Transactional
    public void scoreSave(User user, Long score) {
        MiniGame miniGame = miniGameRepository.findByUser(user);
        log.info("miniGame = {} ", miniGame);

        // 리팩토링 예정
        if (miniGame != null) {
            if (miniGame.getScore() < score){
                miniGame.updateScore(score);
            }
            miniGame.updateScore(miniGame.getScore());
        } else {
            miniGame = MiniGame.builder()
                    .user(user)
                    .score(score)
                    .build();
        }

        miniGameRepository.save(miniGame);
    }

    public List<MiniGame> scoreDesc() {
        List<MiniGame> byOrderByScoreDesc = miniGameRepository.findTop3ByOrderByScoreDesc();
        log.info("byOrderByScoreDesc = {} ", byOrderByScoreDesc);
        return miniGameRepository.findTop3ByOrderByScoreDesc();
    }
}
