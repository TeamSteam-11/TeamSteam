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

    private Long id;

    private String username;

    private String steamId;

    private int temperature;

    public SecurityUser(Long id, String username, String steamId, int temperature) {
        this.id = id;
        this.username = username;
        this.steamId = steamId;
        this.temperature = temperature;
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
