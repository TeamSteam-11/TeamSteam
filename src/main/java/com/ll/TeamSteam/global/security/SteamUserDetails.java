package com.ll.TeamSteam.global.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SteamUserDetails implements UserDetails {

	private final String steamId;
	private final Collection<? extends GrantedAuthority> authorities;

	public SteamUserDetails(String steamId, Collection<? extends GrantedAuthority> authorities) {
		this.steamId = steamId;
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return getSteamId();
	}

	public String getSteamId() {
		return steamId;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}