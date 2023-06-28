package com.ll.TeamSteam.domain.user.controller;



import com.ll.TeamSteam.domain.steam.service.SteamService;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.global.security.SecurityUser;
import com.ll.TeamSteam.global.security.UserInfoResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

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



    @GetMapping("/user/login")
    public String login() {
        return "/user/login";
    }

    @GetMapping("/login/check")
    public String check(
            @RequestParam(value = "openid.ns") String openidNs,
            @RequestParam(value = "openid.mode") String openidMode,
            @RequestParam(value = "openid.op_endpoint") String openidOpEndpoint,
            @RequestParam(value = "openid.claimed_id") String openidClaimedId,
            @RequestParam(value = "openid.identity") String openidIdentity,
            @RequestParam(value = "openid.return_to") String openidReturnTo,
            @RequestParam(value = "openid.response_nonce") String openidResponseNonce,
            @RequestParam(value = "openid.assoc_handle") String openidAssocHandle,
            @RequestParam(value = "openid.signed") String openidSigned,
            @RequestParam(value = "openid.sig") String openidSig,
            HttpSession session
    ) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://steamcommunity.com/openid/login")
                .queryParam("openid.ns", openidNs)
                .queryParam("openid.mode", "check_authentication")
                .queryParam("openid.op_endpoint", openidOpEndpoint)
                .queryParam("openid.claimed_id", openidClaimedId)
                .queryParam("openid.identity", openidIdentity)
                .queryParam("openid.return_to", openidReturnTo)
                .queryParam("openid.response_nonce", openidResponseNonce)
                .queryParam("openid.assoc_handle", openidAssocHandle)
                .queryParam("openid.signed", openidSigned)
                .queryParam("openid.sig", openidSig);



        String block = WebClient.create("https://steamcommunity.com")
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
                .bodyToMono(String.class)
                .block();
        System.out.println(block);

        boolean isTrue = Objects.requireNonNull(block).contains("true");

        // 회원가입
            // 1. findBySteamId(steamId)
            // 2. 없으면 회원가입 Member or 로그인
        // 회원가입 안 할 때
            // security 객체를 만들고 session에 저장


        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(openidIdentity);
        String username;
        if (matcher.find()) {
            username = matcher.group();
        } else {
            throw new IllegalArgumentException();
        }

        // member저장

        SecurityUser user = SecurityUser.builder()
                .username("steam_" + username)
                .build();

        Authentication authentication =
                new OAuth2AuthenticationToken(user, user.getAuthorities(), "steam");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT"
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        UserInfoResponse userInfo = getUserInfo(session);
        session.setAttribute("userInfo", userInfo);
        return "redirect:/user/checkFirstVisit";

    }




    @GetMapping("/user/check")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public String check() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        return user.getUsername();
    }

    @GetMapping("/user/isLogin")
    @PreAuthorize("isAuthenticated()")
    public String isLogin() {
        return "user/isLogin";
    }

    @GetMapping("/user/info")
    public UserInfoResponse getUserInfo(HttpSession session) {
        //UserInfoResponse 객체를 사용
        SecurityContext securityContext = (SecurityContext) session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        if (securityContext != null) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
                SecurityUser user = (SecurityUser) authentication.getPrincipal();
                String steamId = extractSteamIdFromUsername(user.getUsername());
                return steamService.getUserInformation(steamId);
            }
        }
        throw new IllegalStateException("User not authenticated");


    }

    @GetMapping("/user/checkFirstVisit")
    public String checkLogin(HttpSession session){
        //처음일 때 create 아닐 때 바로넘기기
        userService.create(getUserInfo(session));

        return "redirect:/main/home";
    }


    private String extractSteamIdFromUsername(String username) {
        if (username.startsWith("steam_")) {
            return username.substring(6);
        } else {
            throw new IllegalArgumentException("Invalid username format");
        }
    }
}
