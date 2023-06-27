package com.ll.TeamSteam.global.security;

import java.util.Map;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface SecurityUserAdapter extends OAuth2User {
    @Override
    default Map<String, Object> getAttributes() {
        return null;
    }
}
