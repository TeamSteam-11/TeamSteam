package com.ll.TeamSteam.domain.home;

import com.ll.TeamSteam.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    private final Rq rq;

    @GetMapping("/")
    public String showMain() {
        if (rq.isLogout()) return "redirect:/main/home";

        return "redirect:/main/home";
    }

    @GetMapping("/main/home")
    public String showHome() {

        return "main/home";
    }
}
