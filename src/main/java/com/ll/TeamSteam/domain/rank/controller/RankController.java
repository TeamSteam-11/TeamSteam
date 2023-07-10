package com.ll.TeamSteam.domain.rank.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/rank")
public class RankController {

    @GetMapping("/rank")
    public String showRank(){



        return "rank/rank";
    }

}
