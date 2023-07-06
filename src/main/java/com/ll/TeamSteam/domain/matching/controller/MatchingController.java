package com.ll.TeamSteam.domain.matching.controller;

import com.ll.TeamSteam.domain.chatRoom.service.ChatRoomService;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.service.MatchingService;
import com.ll.TeamSteam.domain.matchingPartner.service.MatchingPartnerService;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import com.ll.TeamSteam.domain.userTag.gameTag.GameTag;
import com.ll.TeamSteam.domain.userTag.genreTag.GenreTag;
import com.ll.TeamSteam.global.rq.Rq;
import com.ll.TeamSteam.global.rsData.RsData;
import com.ll.TeamSteam.global.security.SecurityUser;
import com.ll.TeamSteam.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Slf4j
public class MatchingController {
    private final Rq rq;
    private final MatchingService matchingService;
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;
    private final MatchingPartnerService matchingPartnerService;

    @GetMapping("/list")
    public String matchingList(Model model) {
        List<Matching> matchingList = matchingService.getMachingList();

        model.addAttribute("matchingList", matchingList);

        return "matching/list";
    }

    @GetMapping("/create")
    public String matchingCreate(@AuthenticationPrincipal SecurityUser user, Model model) {
        // CreateForm 객체를 모델에 추가
        model.addAttribute("createForm", new CreateForm());
        List<GameTag> userGameTags = userRepository.findById(user.getId()).orElseThrow().getUserTag().getGameTag();
        List<GenreTag> userGenreTags = userRepository.findById(user.getId()).orElseThrow().getUserTag().getGenreTag();
        model.addAttribute("userGameTags", userGameTags);
        model.addAttribute("userGenreTags", userGenreTags);

        return "matching/create";
    }

    // 입력받은 매칭 글 가져오기
    @AllArgsConstructor
    @Getter
    @Setter
    public static class CreateForm {
        private String title;
        private String content;
        private GenreTagType genre;
        private Integer gameTagId;
        private Long capacity;
        private int startTime;
        private int endTime;
        private int selectedHours;

        public CreateForm() {
            this.title = "제목";
            this.content = "내용";
            this.genre =GenreTagType.valueOf("삼인칭슈팅");
            this.gameTagId=1;
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
            return "redirect:/match/create";
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
        Matching matching = matchingService.create(
                user1,
                createForm.getTitle(),
                createForm.getContent(),
                createForm.getGenre(),
                createForm.getGameTagId(),
                createForm.getCapacity(),
                createForm.getStartTime(),
                createForm.getEndTime(),
                deadlineDate
        );

        // 매칭 등록 실패 시
        if (matching == null) {
            throw new IllegalArgumentException("게시글이 작성되지 않았습니다.");
            // return "match/create";
        }

        log.info("createRsData.getId = {}", matching.getId());

        matchingPartnerService.addPartner(matching.getId(), user1.getId());
        chatRoomService.createAndConnect(createForm.getTitle(), matching, user.getId());

        // 등록 게시글 작성 후 매칭 목록 페이지로 이동
        return rq.redirectWithMsg("/match/list", "매칭이 게시글이 생성되었습니다.");
    }

    @GetMapping("/detail/{matchingId}")
    public String matchingDetail(Model model, @PathVariable Long matchingId) {

        Matching matching = matchingService.findById(matchingId).orElse(null);

        boolean alreadyWithPartner = false;

        // 로그인 상태인지 확인
        if (rq.isLogin()) {
            alreadyWithPartner = matchingPartnerService.isDuplicatedMatchingPartner(matching.getId(), rq.getUser().getId());
        }

        model.addAttribute("alreadyWithPartner", alreadyWithPartner);
        model.addAttribute("matching", matching);

        return "matching/detail";
    }

    @PostMapping("/detail/delete/{matchingId}")
    public String deleteMatching(@PathVariable Long matchingId, @AuthenticationPrincipal SecurityUser user) {

        Matching matching = matchingService.findById(matchingId).orElse(null);

        if (matching.getUser().getId() != user.getId()) {
            return rq.historyBack("삭제할 수 있는 권한이 없습니다.");
        }

        matchingService.deleteById(matchingId);

        return rq.redirectWithMsg("/match/list", "매칭 게시글이 삭제되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{matchingId}")
    public String modifyMatching(@PathVariable Long matchingId, CreateForm createForm, Model model, @AuthenticationPrincipal SecurityUser user) {

        Matching matching = matchingService.findById(matchingId).orElse(null);

        createForm.setTitle(matching.getTitle());
        createForm.setContent(matching.getContent());
        createForm.setGenre(matching.getGenre());
        createForm.setGameTagId(matching.getGameTagId());
        createForm.setCapacity(matching.getCapacity());
        createForm.setStartTime(matching.getStartTime());
        createForm.setEndTime(matching.getEndTime());

        model.addAttribute("matching", matching);

        List<GameTag> userGameTags = userRepository.findById(user.getId()).orElseThrow().getUserTag().getGameTag();
        List<GenreTag> userGenreTags = userRepository.findById(user.getId()).orElseThrow().getUserTag().getGenreTag();
        model.addAttribute("userGameTags", userGameTags);
        model.addAttribute("userGenreTags", userGenreTags);

        return "matching/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{matchingId}")
    public String modify(@Valid CreateForm createForm, @PathVariable Long matchingId, @AuthenticationPrincipal SecurityUser user) {

        Matching matching = matchingService.findById(matchingId).orElse(null);

        if (matching.getUser().getId() != user.getId()) {
            return rq.historyBack("수정 권한이 없습니다.");
        }

        RsData<Matching> modifyRsData = matchingService.modify(matching, createForm.getTitle(), createForm.getContent(),
                createForm.getGenre(), createForm.getCapacity(), createForm.getStartTime(), createForm.getEndTime());


        chatRoomService.updateChatRoomName(matching.getChatRoom(), matching.getTitle());

        if (modifyRsData.isFail()) {
            return rq.historyBack(modifyRsData);
        }

        return String.format("redirect:/match/detail/%d", matchingId);
    }

    /**
     * 파트너 신청
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/detail/{matchingId}/addPartner")
    public String addPartner(@PathVariable Long matchingId, @AuthenticationPrincipal SecurityUser user,
                             Model model){
        Matching matching = matchingService.findById(matchingId).orElseThrow();

        // 로그인 하지 않은 유저가 매칭파트너 신청을 했을 경우 로그인 페이지로 redirect
        if (user == null){
            return "redirect:/user/login";
        }

        // true 면 matching partner에 저장되어있고, false 면 없음
        boolean alreadyWithPartner = matchingPartnerService.isDuplicatedMatchingPartner(matching.getId(), user.getId());
        log.info("alreadyWithPartner = {} ", alreadyWithPartner);

        model.addAttribute("alreadyWithPartner", alreadyWithPartner);

        // 이미 저장된 사람은 중복 저장되지 않도록 처리
        if (alreadyWithPartner) {
             throw new IllegalArgumentException("너 이미 매칭파트너에 참여중이야");
        }

        matchingPartnerService.addPartner(matching.getId(), user.getId());

        return String.format("redirect:/match/detail/%d", matchingId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/detail/{matchingId}/moveChatRoom")
    public String moveChatRoom(@PathVariable Long matchingId, @AuthenticationPrincipal SecurityUser user){
        Matching matching = matchingService.findById(matchingId).orElseThrow();

        // true 면 matching partner에 저장되어있고, false 면 없음
        boolean alreadyWithPartner = matchingPartnerService.isDuplicatedMatchingPartner(matching.getId(), user.getId());

        if (!alreadyWithPartner) {
            throw new IllegalArgumentException("너 지금 매칭파트너에 등록이 안되어있어, 우선 매칭파트너부터 등록해");
        }

        matchingPartnerService.updateTrue(matching.getId(), user.getId());

        return String.format("redirect:/chat/rooms/%d", matchingId);
    }
}
