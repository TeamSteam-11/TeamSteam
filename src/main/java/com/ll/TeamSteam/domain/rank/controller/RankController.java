package com.ll.TeamSteam.domain.rank.controller;

import com.ll.TeamSteam.domain.rank.service.RankService;
import com.ll.TeamSteam.domain.user.controller.UserController;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RankController {

    private final UserController userController;
    private final UserService userService;

    private final RankService rankService;

    @GetMapping("/rank")
    public String showRank(Model model, @AuthenticationPrincipal SecurityUser user) {
        List<User> topUserList = getTopUsersWithHighTemperature();
        model.addAttribute("topUserList", topUserList);

        if (user == null) { // 미로그인 상태
            return "rank/rank";
        }

        //리더보드 최상단에 포함되는지 확인 추후 스트림 사용가능할 때 리팩토링
        boolean listContainUser = false;
        for (User a : topUserList) {
            if (a.getId() == user.getId()) {
                listContainUser = true;
            }
        }

        long userRank = rankService.getUserOfTemperatureRank(user.getId());
        if (!listContainUser) {
            model.addAttribute("myRank", userRank);
            model.addAttribute("myRankProfile", userService.findById(user.getId()).orElseThrow());
        }

        return "rank/rank";
    }

    public List<User> getTopUsersWithHighTemperature() {
        return rankService.getTopUserList();
    }

}
