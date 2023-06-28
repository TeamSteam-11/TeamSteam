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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public String startSession( @RequestParam(value = "openid.identity") String openidIdentity, HttpSession session ) {

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(openidIdentity);
        String steamId;
        if (matcher.find()) {
            steamId = matcher.group();
        } else {
            throw new IllegalArgumentException();
        }

        // member저장

        SecurityUser user = SecurityUser.builder()
                .steamId("steam_" + steamId)
                .build();

        Authentication authentication =
                new OAuth2AuthenticationToken(user, user.getAuthorities(), "steam");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT"
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        UserInfoResponse userInfo = getUserInfo(session);
        session.setAttribute("userInfo", userInfo);
        session.setAttribute("steamId", steamId);
        return "redirect:/user/checkFirstVisit";

    }




    @GetMapping("/user/check")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public String check() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        return user.getSteamId();
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
                String steamId = extractSteamIdFromUsername(user.getSteamId());
                return steamService.getUserInformation(steamId);
            }
        }
        throw new IllegalStateException("User not authenticated");

    }

    @GetMapping("/user/checkFirstVisit")
    public String checkLogin(HttpSession session){

        String steamId = (String)session.getAttribute("steamId");
        if(!userService.findBySteamId(steamId).isPresent()){
            userService.create(getUserInfo(session));
            return "user/createGenre";
        }

        return "redirect:/main/home";
    }

    @PostMapping("/user/createGenre")
    @ResponseBody
    public String genreFormPost(){
        return "성공";
    }


    private String extractSteamIdFromUsername(String username) {
        if (username.startsWith("steam_")) {
            return username.substring(6);
        } else {
            throw new IllegalArgumentException("Invalid username format");
        }
    }
}
