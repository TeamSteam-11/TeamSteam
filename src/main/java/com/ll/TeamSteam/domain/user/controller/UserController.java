package com.ll.TeamSteam.domain.user.controller;



import com.ll.TeamSteam.domain.steam.service.SteamService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final SteamService steamService;

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

        return "redirect:/user/check";

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



    @GetMapping("/user/checkFirstVisit")
    public String checkLogin(HttpSession session){

        String steamId = (String)session.getAttribute("steamId");
        if(!userService.findBySteamId(steamId).isPresent()){
            userService.create(getUserInfo(steamId));
            return "user/createGenre";
        }

        return "redirect:/main/home";
    }

    @PostMapping("/user/createGenre")
    @ResponseBody
    public String[] genreFormPost(@RequestParam String gender, @RequestParam("gameGenre") String[] gameGenres){

        return gameGenres;
    }
}
