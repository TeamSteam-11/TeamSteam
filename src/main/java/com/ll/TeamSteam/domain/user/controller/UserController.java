package com.ll.TeamSteam.domain.user.controller;


import com.ll.TeamSteam.domain.friend.entity.Friend;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.notification.service.NotificationService;
import com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser;
import com.ll.TeamSteam.domain.recentlyUser.service.RecentlyUserService;
import com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary;
import com.ll.TeamSteam.domain.steam.service.SteamService;
import com.ll.TeamSteam.domain.user.entity.Gender;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.rq.Rq;
import com.ll.TeamSteam.global.rsData.RsData;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    private final SteamService steamService;

    private final RecentlyUserService recentlyUserService;

    private final NotificationService notificationService;

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

        ResponseEntity<String> block = WebClient.create("https://steamcommunity.com")
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/openid/login")
                        .queryParam("openid.ns", openidNs)
                        .queryParam("openid.mode", "check_authentication")
                        .queryParam("openid.op_endpoint", openidOpEndpoint)
                        .queryParam("openid.claimed_id", openidClaimedId)
                        .queryParam("openid.identity", openidIdentity)
                        .queryParam("openid.return_to", openidReturnTo)
                        .queryParam("openid.response_nonce", openidResponseNonce)
                        .queryParam("openid.assoc_handle", openidAssocHandle)
                        .queryParam("openid.signed", openidSigned)
                        .queryParam("openid.sig", openidSig)
                        .build()
                )
                .retrieve()
                .toEntity(String.class)
                .block();


        log.info("block = {} ", block);

        //200이 나오지않아도 트루가 뜰 수 있음
        boolean isTrue = Objects.requireNonNull(block).getBody().contains("true");

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

        log.info("user.getId() = {} ", user.getId());
        log.info("user.getUsername() = {}", user.getUsername());
        log.info("user.getSteamId() = {} ", user.getSteamId());
        log.info("user.getAuthorities() = {}", user.getAuthorities());

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
        @RequestParam(defaultValue = "9") int size,
        @AuthenticationPrincipal SecurityUser user, Model model
        ) throws ParseException {

        String steamId = user.getSteamId();
        List<SteamGameLibrary> haveGameListData = steamService.getUserGameList(steamId);

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
    public String createGenre(Model model){
        model.addAttribute("genreTags",GenreTagType.values());

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

        // DB에 저장
        userService.updateUserData(gender, genreTagTypes, id);
        return "redirect:/user/createGameTag";
    }

    public UserInfoResponse getUserInfo(String steamId) {

        return steamService.getUserInformation(steamId);
    }


    @GetMapping(value = "/user/profile/{userId}", produces = MediaType.TEXT_HTML_VALUE)
    public String userProfile(@PathVariable long userId, @AuthenticationPrincipal SecurityUser user, Model model,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "9") int size) throws ParseException {


        User targetUser = userService.findById(userId).orElseThrow();

        List<SteamGameLibrary> haveGameListData = steamService.getUserGameList(targetUser.getSteamId());

        int totalItems = haveGameListData.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int start = page * size;
        int end = Math.min(start + size, totalItems);
        List<SteamGameLibrary> pagedGameList = haveGameListData.subList(start, end);

        List<RecentlyUser> recentlyUserList =recentlyUserService.findAllByUserId(userId);

        List<Friend> friendsList = userService.getFriends(user.getId());


        long loginedId = user.getId();//프로필 본인인지 아닌지 검증하는 용도

        //친구 검증
        boolean isFriend =userService.isFriend(userId,loginedId);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("gameList", pagedGameList);
        model.addAttribute("targetUser", targetUser);
        model.addAttribute("loginedId", loginedId);
        model.addAttribute("friendsList", friendsList);
        model.addAttribute("recentlyUserList",recentlyUserList);
        model.addAttribute("isFriend", isFriend);

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
        model.addAttribute("genreTags",GenreTagType.values());

        return "user/createGenre";
    }
}