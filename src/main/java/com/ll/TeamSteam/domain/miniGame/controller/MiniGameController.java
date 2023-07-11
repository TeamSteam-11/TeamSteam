package com.ll.TeamSteam.domain.miniGame.controller;

import com.ll.TeamSteam.domain.miniGame.dto.ScoreRequest;
import com.ll.TeamSteam.domain.miniGame.entity.MiniGame;
import com.ll.TeamSteam.domain.miniGame.service.MiniGameService;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MiniGameController {

    private final UserService userService;
    private final MiniGameService miniGameService;

    @GetMapping("/minigame")
    public String miniGame(@AuthenticationPrincipal SecurityUser user, Model model){
        List<MiniGame> miniGameList = new ArrayList<>();

        // 로그인 된 유저
        if (user != null){
            User currentUser = userService.findByIdElseThrow(user.getId());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("avatar", currentUser.getAvatar());
        }

        miniGameList = miniGameService.scoreDesc();
        model.addAttribute("miniGameList", miniGameList);

        return "minigame/index";
    }

//     로그인된 유저만 점수가
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/minigame/{userId}/scoreSave")
    public ResponseEntity<String> saveScore(@PathVariable Long userId, @RequestBody ScoreRequest request) {
        User user = userService.findById(userId).orElseThrow();
        miniGameService.scoreSave(user, request.getScore());
        log.info("request.getScore = {}", request.getScore());
        return ResponseEntity.ok("점수가 저장되었습니다.");
    }


}
