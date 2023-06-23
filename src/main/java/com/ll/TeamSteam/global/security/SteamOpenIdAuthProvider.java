package com.ll.TeamSteam.global.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class SteamOpenIdAuthProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		SteamAuthenticationToken token = (SteamAuthenticationToken) authentication;
		String steamId = token.getSteamId();

		// 여기서 사용자 검증 및 가져오기를 처리하거나, 새로운 사용자를 등록할 수 있습니다.

		// 검증된 사용자를 인증 객체에 추가하세요.
		// 예: SteamUserDetails userDetails = userService.loadUserBySteamId(steamId);
		SteamUserDetails userDetails = getUserDetails(steamId);

		// 인증을 마친 후 사용자 정보를 포함한 새 토큰을 생성합니다.
		SteamAuthenticationToken authenticatedToken = new SteamAuthenticationToken(userDetails);
		return authenticatedToken;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return SteamAuthenticationToken.class.isAssignableFrom(authentication);
	}

	// Steam 사용자 정보를 가져오는 예제 메소드
	private SteamUserDetails getUserDetails(String steamId) {
		// Steam API 호출 또는 데이터베이스 조회를 사용하여 사용자 정보를 얻을 수 있습니다.
		// 예: SteamUserDetails userDetails = userService.loadUserBySteamId(steamId);
		return new SteamUserDetails(steamId);
	}

}
