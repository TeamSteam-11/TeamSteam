package com.ll.TeamSteam.global.security;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SecurityUser implements SecurityUserAdapter {
    private String steamId;

    public SecurityUser(String username) {
        this.steamId = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return steamId;
    }
}
