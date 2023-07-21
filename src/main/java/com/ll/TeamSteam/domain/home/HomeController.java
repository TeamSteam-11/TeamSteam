package com.ll.TeamSteam.domain.home;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.service.MatchingService;
import com.ll.TeamSteam.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    private final Rq rq;
    private final MatchingService matchingService;

    @GetMapping("/")
    public String showMain() {
        if (rq.isLogout()) return "redirect:/main/home";

        return "redirect:/main/home";
    }

    @GetMapping("/main/home")
    public String showHome(Model model) {
        List<Matching> approachingDeadlineList = matchingService.getSortedMatchingByDeadline();
        model.addAttribute("matchingListSortedByDeadline", approachingDeadlineList);

        List<Matching> sortedByParticipantList = matchingService.getSortedMatchingByParticipant();
        model.addAttribute("matchingListSortedByParticipant", sortedByParticipantList);

        return "main/home";
    }

    @GetMapping("/main/error")
    public String error(){
        // 에러 처리 페이지

        return "common/error";
    }

}
