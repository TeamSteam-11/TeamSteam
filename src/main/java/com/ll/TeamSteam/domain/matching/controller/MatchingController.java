package com.ll.TeamSteam.domain.matching.controller;

import com.ll.TeamSteam.domain.chatRoom.entity.ChatRoom;
import com.ll.TeamSteam.domain.chatRoom.exception.KickedUserEnterException;
import com.ll.TeamSteam.domain.chatRoom.exception.NoChatRoomException;
import com.ll.TeamSteam.domain.chatRoom.service.ChatRoomService;
import com.ll.TeamSteam.domain.matching.entity.CreateForm;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.service.MatchingService;
import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

        CreateForm createForm = new CreateForm();

        if (!rq.isLogin()) {
            return "redirect:/user/login";
        }

        // CreateForm 객체를 모델에 추가
        model.addAttribute("createForm", createForm);
        List<GameTag> userGameTags = userRepository.findById(user.getId()).orElseThrow().getUserTag().getGameTag();
        List<GenreTag> userGenreTags = userRepository.findById(user.getId()).orElseThrow().getUserTag().getGenreTag();
        model.addAttribute("userGameTags", userGameTags);
        model.addAttribute("userGenreTags", userGenreTags);

        return "matching/create";
    }

    // 매칭 등록 기능 구현
    @PostMapping("/create")
    public String matchingCreate(@Valid CreateForm createForm,
                                 @AuthenticationPrincipal SecurityUser user) {

        Long userId = user.getId();

        User user1 = userRepository.findById(userId).orElseThrow();

        LocalDateTime modifyDate = LocalDateTime.now();
        int selectedHours = createForm.getSelectedHours();

        LocalDateTime deadlineDate = matchingService.calculateDeadline(modifyDate, selectedHours);

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
            throw new IllegalArgumentException("게시글이 작성되지 않았습니다.");
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

        Matching matching = matchingService.findById(matchingId)
                .orElseThrow(() -> new NoChatRoomException("현재 존재하지 않는 방입니다."));

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
    public String modifyMatching(@PathVariable Long matchingId, Model model, @AuthenticationPrincipal SecurityUser user) {

        Matching matching = matchingService.findById(matchingId).orElse(null);

        if (matching != null) {
            CreateForm createForm = matchingService.setCreateForm(matching);
            model.addAttribute("createForm", createForm);
            model.addAttribute("matching", matching);

            List<GameTag> userGameTags = userRepository.findById(user.getId()).orElseThrow().getUserTag().getGameTag();
            List<GenreTag> userGenreTags = userRepository.findById(user.getId()).orElseThrow().getUserTag().getGenreTag();
            model.addAttribute("userGameTags", userGameTags);
            model.addAttribute("userGenreTags", userGenreTags);
        }

        return "matching/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{matchingId}")
    public String modifyMatching(@Valid CreateForm createForm, @PathVariable Long matchingId, @AuthenticationPrincipal SecurityUser user) {

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
            // 추후 수정
             throw new IllegalArgumentException("너 이미 매칭파트너에 참여중이야");
        }

        ChatRoom chatRoom = chatRoomService.findById(matchingId);
        RsData rsData = chatRoomService.canAddChatRoomUser(chatRoom, user.getId(), matching);
        log.info("rsData.getData = {} ", rsData.getData());

        if (rsData.isError()){
            throw new KickedUserEnterException("강퇴당한 모임입니다.");
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
            // 추후 수정
            throw new IllegalArgumentException("매칭 파트너로 등록되어 있지 않아");
        }

        MatchingPartner matchingPartner = matchingPartnerService.findByMatchingIdAndUserId(matchingId, user.getId());

        if(matchingPartner.isInChatRoomTrueFalse()) {
            return String.format("redirect:/chat/rooms/%d", matchingId);
        }

        if (!matching.canAddParticipant()) {
            throw new IllegalArgumentException("채팅방 정원이 가득 찼습니다");
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
