package com.ll.TeamSteam.domain.steam.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.ll.TeamSteam.global.security.UserInfoResponse;

import org.springframework.beans.factory.annotation.Value;

@Service
public class SteamService {

	@Value("${steam.apiKey}")
	private String apiKey;

	@Value("${steam.baseUrl}")
	private String baseUrl;

	private final RestTemplate restTemplate;

	public SteamService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public UserInfoResponse getUserInformation(String steamId) {
		String url = baseUrl
			+ "/ISteamUser/GetPlayerSummaries/v2?key={apiKey}&steamids={steam_id}";

		UriComponents builder = UriComponentsBuilder
			.fromHttpUrl(url)
			.buildAndExpand(apiKey, steamId);

		ResponseEntity<UserInfoResponse> response =
			restTemplate.getForEntity(builder.toUriString(), UserInfoResponse.class);

		return response.getBody();
	}

	public UserInfoResponse getUserGameList(String steamId) {
		String url = baseUrl
			+ "/IPlayerService/GetOwnedGames/v1?key={apiKey}&steamids={steam_id}" +
			"&include_appinfo=1&include_played_free_games=1&appids_filter=30";

		UriComponents builder = UriComponentsBuilder
			.fromHttpUrl(url)
			.buildAndExpand(apiKey, steamId);

		ResponseEntity<UserInfoResponse> response =
			restTemplate.getForEntity(builder.toUriString(), UserInfoResponse.class);

		return response.getBody();
	}

}
