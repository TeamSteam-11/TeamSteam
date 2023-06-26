package com.ll.TeamSteam.domain.matching.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchingController {

    @GetMapping("/list")
    public String matchingList() {
        return "matching/list";
    }

    @GetMapping("/create")
    public String matchingCreate(Model model) {
        // CreateForm 객체를 모델에 추가
        model.addAttribute("createForm", new CreateForm());

        return "matching/create";
    }

    // 입력받은 매칭 글 가져오기
    @AllArgsConstructor
    @Getter
    @Setter
    public static class CreateForm {
        private String title;
        private String content;
        private Long capacity;

        public CreateForm() {
            this.title = "제목";
            this.content = "내용";
            this.capacity = 1L;
        }
    }

    // 매칭 등록 기능 구현
    @PostMapping("/create")
    public String matchingCreate(@Valid CreateForm createForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 유효성 검사 오류가 있을 시 등록 페이지로 다시 이동
            return "matching/create";
        }

        // 서비스에서 추가 기능 구현


        // 등록 게시글 작성 후 매칭 목록 페이지로 이동
        return "matching/list";
    }


}
