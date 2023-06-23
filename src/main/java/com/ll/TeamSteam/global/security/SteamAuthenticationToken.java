package com.ll.TeamSteam.global.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class SteamAuthenticationToken extends AbstractAuthenticationToken {

	private final String steamId;

	public SteamAuthenticationToken(String steamId) {
		super(null);
		this.steamId = steamId;
		setAuthenticated(false);
	}

	public SteamAuthenticationToken(SteamUserDetails userDetails) {
		super(userDetails.getAuthorities());
		this.steamId = userDetails.getSteamId();
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return steamId;
	}

	public String getSteamId() {
		return steamId;
	}

}