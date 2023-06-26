package com.ll.TeamSteam.global.rq;

import com.ll.TeamSteam.global.rsData.RsData;
import com.ll.TeamSteam.standard.util.Ut;
import org.springframework.security.core.userdetails.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Date;

@Component
@RequestScope
public class Rq {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private User user;

    public String historyBack(String msg) {
        String referer = request.getHeader("referer");
        String key = "historyBackErrorMsg___" + referer;
        request.setAttribute("localStorageKeyAboutHitoryBackErrorMsg", key);
        request.setAttribute("historyBackErrorMsg", msg);
        return "common/js";
    }

    public String historyBack(RsData rsData) {
        return historyBack(rsData.getMsg());
    }

    public String redirectWithMsg(String url, RsData rsData) {
        return redirectWithMsg(url, rsData.getMsg());
    }

    public String redirectWithMsg(String url, String msg) {
        return "redirect:" + urlWithMsg(url, msg);
    }

    public String urlWithMsg(String url, String msg) {
        return Ut.url.modifyQueryParam(url, "msg", msgWithTtl(msg));
    }

    public String msgWithTtl(String msg) {
        return Ut.url.encode(msg) + ";ttl=" + new Date().getTime();
    }

}
