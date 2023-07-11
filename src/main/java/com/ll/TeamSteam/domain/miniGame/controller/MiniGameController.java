package com.ll.TeamSteam.domain.miniGame.controller;

import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MiniGameController {

    private final UserService userService;

    @GetMapping("/minigame")
    public String miniGame(){
//        User currentUser = userService.findByIdElseThrow(user.getId());
//        String avatar = currentUser.getAvatar();
//
//        model.addAttribute("avatar", avatar);

        return "minigame/index";
    }


}
