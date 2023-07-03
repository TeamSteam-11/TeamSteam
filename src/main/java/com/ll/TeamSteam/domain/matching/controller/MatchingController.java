package com.ll.TeamSteam.domain.matching.controller;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.repository.MatchingRepository;
import com.ll.TeamSteam.domain.matching.service.MatchingService;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import com.ll.TeamSteam.global.rq.Rq;
import com.ll.TeamSteam.global.rsData.RsData;
import com.ll.TeamSteam.global.security.SecurityUser;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchingController {
    private final Rq rq;
    private final MatchingService matchingService;
    private final MatchingRepository matchingRepository;
    private final UserRepository userRepository;

    @GetMapping("/list")
    public String matchingList(Model model) {
        List<Matching> matchingList = matchingService.getMachingList();
        model.addAttribute("matchingList", matchingList);

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
        private int startTime;
        private int endTime;
        private int selectedHours;

        public CreateForm() {
            this.title = "제목";
            this.content = "내용";
            this.capacity = 1L;
            this.startTime = 0;
            this.endTime = 24;
            this.selectedHours = 0;
        }
    }

    // 매칭 등록 기능 구현
    @PostMapping("/create")
    public String matchingCreate(@Valid CreateForm createForm, BindingResult bindingResult,
                                 @AuthenticationPrincipal SecurityUser user) {

        if (bindingResult.hasErrors()) {
            // 유효성 검사 오류가 있을 시 등록 페이지로 다시 이동
            return "match/create";
        }

        Long userId = user.getId();

        User user1 = userRepository.findById(userId).orElseThrow();

        // 사용자가 선택한 마감 시간을 설정하여 매칭 생성에 사용
        LocalDateTime deadlineDate;
        LocalDateTime modifyDate = LocalDateTime.now();
        int selectedHours = createForm.getSelectedHours();
        if (selectedHours > 0) {
            deadlineDate = matchingService.setDeadline(modifyDate, selectedHours);
        } else {
            // 마감 시간을 '제한 없음'으로 선택 시 매칭 마감일이 30일 이후로 저장됨
            deadlineDate = modifyDate.plusDays(30);
        }


        // 서비스에서 추가 기능 구현
        RsData<Matching> createRsData = matchingService.create(
                user1,
                createForm.getTitle(),
                createForm.getContent(),
                createForm.getCapacity(),
                createForm.getStartTime(),
                createForm.getEndTime(),
                deadlineDate
        );

        // 매칭 등록 실패 시
        if (createRsData.isFail()) {
            return rq.historyBack(createRsData);
        }

        // 등록 게시글 작성 후 매칭 목록 페이지로 이동
        return rq.redirectWithMsg("/match/list", createRsData);
    }

    @GetMapping("/detail/{id}")
    public String matchingDetail(Model model, @PathVariable("id") Long id) {
        Matching matching = matchingService.findById(id);

        model.addAttribute("matching", matching);

        return "matching/detail";
    }

    @PostMapping("/detail/delete/{id}")
    public String deleteMatching(@PathVariable("id") Long id, @AuthenticationPrincipal SecurityUser user) {
        Matching matching = matchingService.findById(id);

        if (matching.getUser().getId() != user.getId()) {
            return rq.historyBack("삭제할 수 있는 권한이 없습니다.");
        }

        matchingService.deleteById(id);

        return rq.redirectWithMsg("/match/list", "매칭 게시글이 삭제되었습니다.");
    }

    @GetMapping("/modify/{id}")
    public String modifyMatching(@PathVariable("id") Long id, Model model) {
        Matching matching = matchingService.findById(id);

        if (matching == null) {
            return "/matching/detail";
        }

        model.addAttribute("matching", matching);

        return "matching/modify";
    }

    @PostMapping("/modify/{id}")
    public String modify(@PathVariable("id") Long id, @Valid CreateForm createForm,
                         BindingResult bindingResult, @AuthenticationPrincipal SecurityUser user, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("matching", matchingService.findById(id));
            return "match/modify";
        }

        Long userId = user.getId();

        User user1 = userRepository.findById(userId).orElse(null);

        Matching matching = matchingService.findById(id);

        if (matching.getUser().getId() != user.getId()) {
            return rq.historyBack("수정 권한이 없습니다.");
        }

        RsData<Matching> modifyRsData = matchingService.modify(matching, user1, createForm);

        if (modifyRsData.isFail()) {
            return rq.historyBack(modifyRsData);
        }

        return "redirect:/match/detail/" + modifyRsData.getData().getId();
    }


}
