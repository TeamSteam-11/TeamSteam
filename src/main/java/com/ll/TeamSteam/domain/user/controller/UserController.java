package com.ll.TeamSteam.domain.user.controller;


import com.ll.TeamSteam.domain.friend.entity.Friend;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.notification.service.NotificationService;
import com.ll.TeamSteam.domain.rank.service.RankService;
import com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser;
import com.ll.TeamSteam.domain.recentlyUser.service.RecentlyUserService;
import com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary;
import com.ll.TeamSteam.domain.steam.service.SteamGameLibraryService;
import com.ll.TeamSteam.domain.user.entity.Gender;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.rq.Rq;
import com.ll.TeamSteam.global.security.SecurityUser;
import com.ll.TeamSteam.global.security.UserInfoResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @Value("${custom.site.baseUrl}")
    private String baseUrl;

    private final UserService userService;

    private final SteamGameLibraryService steamGameLibraryService;

    private final RecentlyUserService recentlyUserService;

    private final NotificationService notificationService;

    private final RankService rankService;

    private final Rq rq;

    @GetMapping("/user/login")
    public String login(Model model) {
        model.addAttribute("baseUrl", baseUrl);
        log.info("baseUrl = {}", baseUrl);
        return "user/login";
    }

    @GetMapping("/login/check")
    public String startSession(@RequestParam(value = "openid.ns") String openidNs,
                               @RequestParam(value = "openid.mode") String openidMode,
                               @RequestParam(value = "openid.op_endpoint") String openidOpEndpoint,
                               @RequestParam(value = "openid.claimed_id") String openidClaimedId,
                               @RequestParam(value = "openid.identity") String openidIdentity,
                               @RequestParam(value = "openid.return_to") String openidReturnTo,
                               @RequestParam(value = "openid.response_nonce") String openidResponseNonce,
                               @RequestParam(value = "openid.assoc_handle") String openidAssocHandle,
                               @RequestParam(value = "openid.signed") String openidSigned,
                               @RequestParam(value = "openid.sig") String openidSig,
                               HttpServletRequest request,
                               HttpServletResponse response) throws InterruptedException {


        log.info("openidResponseNonce = {} ", openidResponseNonce);

        //로그인인증 체크과정 2xx가 나오면 정상작동
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("openid.ns", openidNs);
        formData.add("openid.mode", "check_authentication");
        formData.add("openid.op_endpoint", openidOpEndpoint);
        formData.add("openid.claimed_id", openidClaimedId);
        formData.add("openid.identity", openidIdentity);
        formData.add("openid.return_to", openidReturnTo);
        formData.add("openid.response_nonce", openidResponseNonce);
        formData.add("openid.assoc_handle", openidAssocHandle);
        formData.add("openid.signed", openidSigned);
        formData.add("openid.sig", openidSig);

        String body = WebClient.create("https://steamcommunity.com")
                .post()
                .uri("/openid/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        boolean isTrue = Objects.requireNonNull(body).contains("true");

        log.info("isTrue = {} ", isTrue);
        if (!isTrue) {
            return "redirect:https://steamcommunity.com/openid/login?openid.ns=http://specs.openid.net/auth/2.0&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select&openid.identity=http://specs.openid.net/auth/2.0/identifier_select&openid.return_to=" + baseUrl + "/login/check&openid.realm=" + baseUrl + "&openid.mode=checkid_setup";
        }

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(openidIdentity);
        String steamId;

        if (matcher.find()) {
            steamId = matcher.group();
        } else {
            throw new IllegalArgumentException();
        }

        if (!userService.findBySteamId(steamId).isPresent()) {
            userService.create(getUserInfo(steamId));
        }

        SecurityUser user = SecurityUser.builder()
                .id(userService.findBySteamId(steamId).get().getId())
                .username(userService.findBySteamId(steamId).get().getUsername())
                .steamId(steamId)
                .build();

        Authentication authentication = new OAuth2AuthenticationToken(user, user.getAuthorities(), "steam");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // 새로운 세션 생성
        session = request.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        // 세션 ID를 쿠키에 설정
        Cookie cookie = new Cookie("JSESSIONID", session.getId());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/user/checkFirstVisit";

    }


    @GetMapping(value = "/user/createGameTag", produces = MediaType.TEXT_HTML_VALUE)
    public String userGameListSave(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "15") int size,
        @AuthenticationPrincipal SecurityUser user, Model model
    ) throws ParseException {

        String steamId = user.getSteamId();
        List<SteamGameLibrary> haveGameListData = steamGameLibraryService.getUserGameList(steamId);

        int totalItems = haveGameListData.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int start = page * size;
        int end = Math.min(start + size, totalItems);
        List<SteamGameLibrary> pagedGameList = haveGameListData.subList(start, end);

        userService.updateGameList(haveGameListData, steamId);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("gameList", pagedGameList);
        return "user/createGameTag";

    }


    @PostMapping(value = "/user/createGameTag/save-gametag")
    public String saveGameTags(
        @RequestParam(value = "selectedGames", required = false) String selectedGames,
        @AuthenticationPrincipal SecurityUser user,
        HttpSession session) {

        String steamId = user.getSteamId();
        List<Integer> selectedGameIds = Arrays.stream(selectedGames.split(","))
            .filter(s -> !s.isEmpty()) // 빈 문자열 필터링
            .map(Integer::parseInt)
            .collect(Collectors.toList());

        // 선택한 게임 목록을 세션에 저장
        session.setAttribute("selectedGames", selectedGameIds);

        userService.saveSelectedGames(selectedGameIds, steamId);

        return "redirect:/main/home";
    }

    @GetMapping("/user/checkFirstVisit")
    public String checkLogin(@AuthenticationPrincipal SecurityUser user) {

        String steamId = user.getSteamId();
        log.info("userService.findBySteamId() = {}", userService.findBySteamId(steamId));
        if (userService.findBySteamId(steamId).get().getType().equals(Gender.Wait)) {
            log.info("userService.findBySteamId() = {}", userService.findBySteamId(steamId));
            return "redirect:/user/createGenre";
        }

        return "redirect:/main/home";
    }


    @GetMapping("user/createGenre")
    public String createGenre(Model model,@AuthenticationPrincipal SecurityUser user){

        if(user.getId() == null){
            return "redirect:/error/commonError";
        }

        Set<String> action = new HashSet<>(Arrays.asList("일인칭슈팅", "삼인칭슈팅", "격투", "슛뎀업", "아케이드", "러너", "핵앤슬래시"));
        Set<String> rpg = new HashSet<>(Arrays.asList("JRPG", "로그라이크", "액션RPG", "어드벤처RPG", "전략RPG", "현대RPG", "파티"));
        Set<String> strategy = new HashSet<>(Arrays.asList("군사", "대전략", "도시", "실시간전략", "카드", "타워디펜스", "턴제전략"));
        Set<String> adventure = new HashSet<>(Arrays.asList("매트로베니아", "비주얼노벨", "캐주얼", "퍼즐", "풍부한스토리", "히든오브젝트"));
        Set<String> simulation = new HashSet<>(Arrays.asList("건설", "농업", "샌드박스", "생활", "연애", "우주", "취미"));
        Set<String> sportsAndRacing = new HashSet<>(Arrays.asList("스포츠", "낚시", "레이싱", "스포츠시뮬레이션"));

        model.addAttribute("action", action);
        model.addAttribute("rpg", rpg);
        model.addAttribute("strategy", strategy);
        model.addAttribute("adventure", adventure);
        model.addAttribute("simulation", simulation);
        model.addAttribute("sportsAndRacing", sportsAndRacing);
        model.addAttribute("action", action);
        model.addAttribute("rpg", rpg);

        return "user/createGenre";
    }

    @PostMapping("/user/createGenre")
    public String genreFormPost(@RequestParam(required = false) String gender, @RequestParam(value = "gameGenre", required = false) String[] gameGenres,
        @AuthenticationPrincipal SecurityUser user) {
        Long id = user.getId();

        if(gender == null || gameGenres == null){
            return "redirect:/user/createGenre";
            //둘중 하나라도 비어있을 시 리다이렉트 에러문구 출력이 가장좋음
        }

        // GenreTagType enum으로 변환하여 리스트로 저장
        List<GenreTagType> genreTagTypes = Arrays.stream(gameGenres)
            .map(GenreTagType::valueOf)
            .collect(Collectors.toList());

        log.info("genretypes ={}", genreTagTypes.size());
        // DB에 저장
        userService.updateUserData(gender, genreTagTypes, id);

        return "redirect:/user/createGameTag";
    }

    public UserInfoResponse getUserInfo(String steamId) {

        return steamGameLibraryService.getUserInformation(steamId);
    }


    @GetMapping(value = "/user/profile/{userId}", produces = MediaType.TEXT_HTML_VALUE)
    public String userProfile(@PathVariable long userId, @AuthenticationPrincipal SecurityUser user, Model model,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "15") int size) throws ParseException {


        User targetUser = userService.findById(userId).orElseThrow();

        List<SteamGameLibrary> haveGameListData = steamGameLibraryService.getUserGameList(targetUser.getSteamId());
        int totalItems = haveGameListData.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = page * size;
        int end = Math.min(start + size, totalItems);
        List<SteamGameLibrary> pagedGameList = haveGameListData.subList(start, end);

        //최근 플레이한 게임
        List<SteamGameLibrary> haveRecentlyPlayedGameList = steamGameLibraryService.getUserRecentlyPlayedGames(targetUser.getSteamId(),userId);


        recentlyUserService.updateRecentlyUser(targetUser.getId());
        List<RecentlyUser> recentlyUserList =recentlyUserService.findAllByUserId(userId);

        List<Friend> friendsList = userService.getFriends(user.getId());


        long loginedId = user.getId();//프로필 본인인지 아닌지 검증하는 용도

        //친구 검증
        boolean isFriend =userService.isFriend(userId,loginedId);

        //매너온도 랭킹
        long userRank = rankService.getUserOfTemperatureRank(userId);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("gameList", pagedGameList);
        model.addAttribute("recentlyGameList", haveRecentlyPlayedGameList);
        model.addAttribute("targetUser", targetUser);
        model.addAttribute("loginedId", loginedId);
        model.addAttribute("friendsList", friendsList);
        model.addAttribute("recentlyUserList",recentlyUserList);
        model.addAttribute("isFriend", isFriend);
        model.addAttribute("myRank", userRank);

        return "user/profile";

    }


    @GetMapping("/user/profile/{userId}/{like}")
    public String getLike(
        @PathVariable long userId,
        @PathVariable int like,
        RedirectAttributes redirectAttributes,
        @AuthenticationPrincipal SecurityUser user) {


        userService.updateTemperature(userId, like);

        Long profileuserId = user.getId();

        redirectAttributes.addAttribute("profileuserId", profileuserId);
        return "redirect:/user/profile/{profileuserId}";
    }


    @PostMapping("/user/profile/{userId}/addFriend")
    public String friend(@PathVariable long userId, @AuthenticationPrincipal SecurityUser user) {

        long loginedId = user.getId();

        User invitingUser = userService.findByIdElseThrow(loginedId);
        User invitedUser = userService.findByIdElseThrow(userId);
        if (!userService.isFriend(invitingUser.getId(), invitedUser.getId())
            && !notificationService.isDupNotification(invitingUser, invitedUser)){
            //이미 친구인지, 이미 친구신청을 보내놨는지 검증
            notificationService.makeFriend(invitingUser, invitedUser);

        }
        return "redirect:/user/profile/" + userId;
    }

    @GetMapping("/user/profile/{userId}/deleteFriend")
    public String deleteFriend(@PathVariable long userId, @AuthenticationPrincipal SecurityUser user){

        Long targetId =userId;
        Long loginedId =user.getId();

        userService.deleteFriend(targetId,loginedId);
        return "redirect:/user/profile/" + loginedId;
    }

    @PostMapping("/user/profile/editprofile")
    public String editProfile(@AuthenticationPrincipal SecurityUser user, Model model) {
        //프로필만 업데이트
        userService.updateUserAvatar(getUserInfo(user.getSteamId()), user.getId());

        Set<String> action = new HashSet<>(Arrays.asList("일인칭슈팅", "삼인칭슈팅", "격투", "슛뎀업", "아케이드", "러너", "핵앤슬래시"));
        Set<String> rpg = new HashSet<>(Arrays.asList("JRPG", "로그라이크", "액션RPG", "어드벤처RPG", "전략RPG", "현대RPG", "파티"));
        Set<String> strategy = new HashSet<>(Arrays.asList("군사", "대전략", "도시", "실시간전략", "카드", "타워디펜스", "턴제전략"));
        Set<String> adventure = new HashSet<>(Arrays.asList("매트로베니아", "비주얼노벨", "캐주얼", "퍼즐", "풍부한스토리", "히든오브젝트"));
        Set<String> simulation = new HashSet<>(Arrays.asList("건설", "농업", "샌드박스", "생활", "연애", "우주", "취미"));
        Set<String> sportsAndRacing = new HashSet<>(Arrays.asList("스포츠", "낚시", "레이싱", "스포츠시뮬레이션"));

        model.addAttribute("action", action);
        model.addAttribute("rpg", rpg);
        model.addAttribute("strategy", strategy);
        model.addAttribute("adventure", adventure);
        model.addAttribute("simulation", simulation);
        model.addAttribute("sportsAndRacing", sportsAndRacing);
        model.addAttribute("action", action);
        model.addAttribute("rpg", rpg);

        return "user/createGenre";
    }
}