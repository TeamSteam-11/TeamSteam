package com.ll.TeamSteam.domain.matching.controller;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.service.ChatRoomService;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.exception.MatchingPartnerNotFoundException;
import com.ll.TeamSteam.domain.matching.service.MatchingService;
import com.ll.TeamSteam.domain.matchingPartner.service.MatchingPartnerService;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.recentlyUser.service.RecentlyUserService;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import com.ll.TeamSteam.domain.userTag.gameTag.GameTag;
import com.ll.TeamSteam.domain.userTag.genreTag.GenreTag;
import com.ll.TeamSteam.global.rq.Rq;
import com.ll.TeamSteam.global.rsData.RsData;
import com.ll.TeamSteam.global.security.SecurityUser;
import com.ll.TeamSteam.domain.user.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    private final RecentlyUserService recentlyUserService;

    @GetMapping("/list")
    public String matchingList(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "8") int size,
                               @RequestParam(defaultValue = "createDate") String sortCode,
                               @RequestParam(defaultValue = "DESC") String direction,
                               /*@PageableDefault(sort = "createDate", direction = Sort.Direction.DESC, size = 12) Pageable pageable,*/
                               Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortCode));
        Page<Matching> matchingList = matchingService.getMatchingList(pageable);
        model.addAttribute("matchingList", matchingList);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", null);

        return "matching/list";
    }

    @GetMapping("/create")
    public String matchingCreate(@AuthenticationPrincipal SecurityUser user, Model model) {

        if (!rq.isLogin()) {
            return "redirect:/user/login";
        }

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
        @NotBlank
        private String title;
        private String content;
        @NotNull
        private GenreTagType genre;
        @NotNull
        private Integer gameTagId;
        @Min(value = 1)
        @Max(value = 5)
        private Long capacity;
        private String gender;
        @NotNull
        @Min(value = 0)
        private Integer startTime;
        @NotNull
        @Min(value = 0)
        private Integer endTime;
        private int selectedHours;

        public CreateForm() {
            this.title = "제목";
            this.content = "내용";
            this.genre =GenreTagType.valueOf("삼인칭슈팅");
            this.gameTagId=1;
            this.capacity = 1L;
            this.gender = "성별무관";
            this.startTime = null;
            this.endTime = 0;
            this.selectedHours = 0;
        }
    }

    // 매칭 등록 기능 구현
    @PostMapping("/create")
    public String matchingCreate(@Valid CreateForm createForm, BindingResult bindingResult,
                                 @AuthenticationPrincipal SecurityUser user) {

        if (bindingResult.hasErrors()) {
            // 유효성 검사 오류가 있을 시 등록 페이지로 다시 이동
//            return "redirect:/match/create";
            log.info("createForm = {} ", createForm);
            log.info("createForm = {} ", createForm.getStartTime());
            log.info("createForm = {} ", createForm.getEndTime());

            return rq.redirectWithMsg("/match/create", "필수 값이 입력되지 않았습니다");
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
                createForm.getGender(),
                createForm.getCapacity(),
                createForm.getStartTime(),
                createForm.getEndTime(),
                deadlineDate
        );

        // 매칭 등록 실패 시
        if (matching == null) {
//            throw new IllegalArgumentException("게시글이 작성되지 않았습니다.");
            return "redirect:/main/error";
        }

        log.info("createRsData.getId = {}", matching.getId());

        matchingPartnerService.addPartner(matching.getId(), user1.getId());
        matchingPartnerService.updateTrue(matching.getId(), user1.getId());

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
        createForm.setGender(matching.getGender());
        createForm.setCapacity(matching.getCapacity());
        createForm.setStartTime(matching.getStartTime());
        createForm.setEndTime(matching.getEndTime());
        createForm.setSelectedHours(matchingService.calculateSelectedHours(matchingId, matching.getDeadlineDate()));

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
                createForm.getGenre(), createForm.getGender(), createForm.getCapacity(), createForm.getStartTime(), createForm.getEndTime(), createForm.getSelectedHours());


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
//             throw new IllegalArgumentException("너 이미 매칭파트너에 참여중이야");
            return "redirect:/main/error";
        }

        ChatRoom chatRoom = chatRoomService.findById(matchingId);
        RsData rsData = chatRoomService.canAddChatRoomUser(chatRoom, user.getId(), matching);
        log.info("rsData.getData = {} ", rsData.getData());

        if (rsData.isError()){
            return rq.historyBack("강퇴당한 모임입니다.");
        }

        if (rsData.isFail()){
            return rq.historyBack("이미 가득찬 방입니다.");
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
            return "redirect:/main/error";
        }

        try {
            matchingPartnerService.updateTrue(matching.getId(), user.getId());
        } catch (MatchingPartnerNotFoundException e) {
            return "redirect:/main/error";
        }

        matchingPartnerService.updateTrue(matching.getId(), user.getId());

        recentlyUserService.updateRecentlyUser(user.getId());

        return String.format("redirect:/chat/rooms/%d", matchingId);
    }

    @GetMapping("/list/search")
    public String searchMatching(@RequestParam String name, @RequestParam String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "8") int size,
                                 @RequestParam(defaultValue = "createDate") String sortCode,
                                 @RequestParam(defaultValue = "DESC") String direction,
                                 Model model){

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortCode));
        Page<Matching> matchingList = matchingService.searchMatching(name, keyword, pageable);
        model.addAttribute("matchingList", matchingList);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchOption", name);  // 검색 옵션 추가

        return "matching/list";
    }

    @GetMapping("/list/filter")
    public String filterMatching(@RequestParam(name = "genretype", required = false) String genreTypeStr,
                                 @RequestParam(name = "starttime", required = false) Integer startTime,
                                 @RequestParam(name = "gender", required = false) String gender,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "8") int size,
                                 @RequestParam(defaultValue = "createDate") String sortCode,
                                 @RequestParam(defaultValue = "DESC") String direction,
                                 Model model) {

        GenreTagType genreType = null;
        if (genreTypeStr != null && !genreTypeStr.isEmpty()) {
            genreType = GenreTagType.ofCode(genreTypeStr);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortCode));
        Page<Matching> matchingList = matchingService.filterMatching(genreType, startTime,gender, pageable);
        model.addAttribute("matchingList", matchingList);
        model.addAttribute("currentPage", page);
        model.addAttribute("genretype", genreTypeStr); // This line is changed
        model.addAttribute("starttime", startTime);
        model.addAttribute("gender", gender);

        return "matching/list";
    }
}
