package com.ll.TeamSteam.global.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 추후 사용 예정
 */
public class CrossScriptingFilter implements Filter {
    public FilterConfig filterConfig;

    public void init(FilterConfig filterConfig) throws SecurityException {
        this.filterConfig = filterConfig;
    }

    public void destroy(){
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new RequestWrapper((HttpServletRequest) request), response);
    }
}
