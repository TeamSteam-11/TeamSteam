package com.ll.TeamSteam.domain.rank.controller;

import com.ll.TeamSteam.domain.rank.service.RankService;
import com.ll.TeamSteam.domain.user.controller.UserController;
import com.ll.TeamSteam.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RankController {

    private final UserController userController;

    private final RankService rankService;

    @GetMapping("/rank")
    public String showRank(Model model){
        List<User> topSevenUserList = getTopSevenUsersWithHighTemperature();
        List<User> oneToThreeUserList = new ArrayList<>(topSevenUserList.subList(0, 3));
        List<User> fourToSevenUserList = new ArrayList<>(topSevenUserList.subList(3, topSevenUserList.size()));

        model.addAttribute("fourToSevenUserList", fourToSevenUserList);
        model.addAttribute("oneToThreeUserList", oneToThreeUserList);
        return "rank/rank";
    }

    public List<User> getTopSevenUsersWithHighTemperature (){
        return rankService.getTopSeven();
    }

}
