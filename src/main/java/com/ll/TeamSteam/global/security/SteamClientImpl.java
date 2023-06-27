package com.ll.TeamSteam.global.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Service
public class SteamClientImpl implements SteamClient {


    @Value("${steam.apiKey}")
    private String apiKey;

    @Value("${steam.baseUrl}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public SteamClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public UserInfoResponse getUserInfo(String steamId) {
        String url = baseUrl
                + "/ISteamUser/GetPlayerSummaries/v2?key={apiKey}&steamids={steam_id}";

        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl(url)
                .buildAndExpand(apiKey, steamId);

        ResponseEntity<UserInfoResponse> response =
                restTemplate.getForEntity(builder.toUriString(), UserInfoResponse.class);

        return response.getBody();
    }

}