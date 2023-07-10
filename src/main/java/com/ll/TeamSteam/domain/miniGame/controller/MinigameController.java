package com.ll.TeamSteam.domain.miniGame.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MinigameController {

    @GetMapping("/minigame")
    public String minigame(){
        return "minigame/index";
    }


}
