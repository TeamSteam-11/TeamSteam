package com.ll.TeamSteam.domain.matching.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchingController {

    @GetMapping("/list")
    public String matchingList() {
        return "matching/list";
    }


}
