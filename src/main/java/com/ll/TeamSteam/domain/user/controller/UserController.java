package com.ll.TeamSteam.domain.user.controller;



import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary;
import com.ll.TeamSteam.domain.steam.service.SteamService;
import com.ll.TeamSteam.domain.user.entity.Gender;
import com.ll.TeamSteam.domain.userTag.gameTag.GameTagRepository;
import com.ll.TeamSteam.domain.userTag.genreTag.GenreTagRepository;
import com.ll.TeamSteam.domain.userTag.UserTagRepository;
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
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final SteamService steamService;

    private final GameTagRepository gameTagRepository;

    private final GenreTagRepository genreTagRepository;

    private final UserTagRepository userTagRepository;

    private final Rq rq;

    @GetMapping("/user/login")
    public String login() {
        return "/user/login";
    }

    @GetMapping("/login/check")
    public String startSession( @RequestParam(value = "openid.ns") String openidNs,
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
                                HttpServletResponse response ) {




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
        if(!isTrue){
            return rq.redirectWithMsg("/main/home", "로그인을 실패했습니다.");
        }

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(openidIdentity);
        String steamId;

        if (matcher.find()) {
            steamId = matcher.group();
        } else {
            throw new IllegalArgumentException();
        }

        if(!userService.findBySteamId(steamId).isPresent()){
            userService.create(getUserInfo(steamId));
        }

        SecurityUser user = SecurityUser.builder()
                .id(userService.findBySteamId(steamId).get().getId())
                .username(userService.findBySteamId(steamId).get().getUsername())
                .steamId(steamId)
                .temperature(userService.findBySteamId(steamId).get().getTemperature())
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

    @GetMapping(value = "/user/profile/{userId}", produces = MediaType.TEXT_HTML_VALUE)
    public String userGameListSave(@PathVariable long userId, @AuthenticationPrincipal SecurityUser user, Model model) throws ParseException {
        String steamId = user.getSteamId();
        RsData<List<SteamGameLibrary>> haveGameListData = steamService.getUserGameList(steamId);

        if (haveGameListData.isSuccess()) {
            List<SteamGameLibrary> haveGameList = haveGameListData.getData();
            userService.saveGameList(haveGameList, steamId);

            model.addAttribute("gameList", haveGameList);
            model.addAttribute("user", user);

            return "user/profile";
        } else {
            String errorMessage = haveGameListData.getMsg();

            model.addAttribute("error", errorMessage);
            return "error";
        }
    }



    @GetMapping("/user/check")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public String check(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        return user.getSteamId();
    }

    @GetMapping("/user/isLogin")
    @PreAuthorize("isAuthenticated()")
    public String isLogin() {
        return "user/isLogin";
    }


    public UserInfoResponse getUserInfo(String steamId) {
        //UserInfoResponse 객체를 사용

        return steamService.getUserInformation(steamId);
    }

    @GetMapping("user/createGenre")
    public String test(){
        return "user/createGenre";
    }


    @GetMapping("/user/checkFirstVisit")
    public String checkLogin(@AuthenticationPrincipal SecurityUser user){


        String steamId = user.getSteamId();
        log.info("userService.findBySteamId() = {}", userService.findBySteamId(steamId));
        if(userService.findBySteamId(steamId).get().getType().equals(Gender.Wait)){
            log.info("userService.findBySteamId() = {}", userService.findBySteamId(steamId));
            return "redirect:/user/createGenre";
        }

        return "redirect:/main/home";
    }

    @PostMapping("/user/createGenre")
    public String genreFormPost(@RequestParam String gender, @RequestParam("gameGenre") String[] gameGenres,
                                  @AuthenticationPrincipal SecurityUser user){
        Long id = user.getId();

        // GenreTagType enum으로 변환하여 리스트로 저장
        List<GenreTagType> genreTagTypes = Arrays.stream(gameGenres)
                .map(GenreTagType::valueOf)
                .collect(Collectors.toList());

        // DB에 저장
        userService.updateUserData(gender, genreTagTypes, id);
        return "redirect:/main/home";
    }

    @GetMapping("/user/profile/{userId}/{like}")
    public String getLike(@PathVariable long userId, @PathVariable int like, RedirectAttributes redirectAttributes){
        userService.updateTemperature(userId, like);

        redirectAttributes.addAttribute("userId", userId);
        return "redirect:/user/profile/{userId}";
    }
}
