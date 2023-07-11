package com.ll.TeamSteam.domain.steam.service;


import net.minidev.json.parser.ParseException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary;
import com.ll.TeamSteam.global.rsData.RsData;
import com.ll.TeamSteam.global.security.UserInfoResponse;

import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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

    public List<SteamGameLibrary> getUserGameList(String steamId) throws ParseException {
        String url = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key={apiKey}&steamid={steam_id}" +
            "&include_appinfo=true&format=json";

        UriComponents builder = UriComponentsBuilder
            .fromHttpUrl(url)
            .buildAndExpand(apiKey, steamId);

        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        String responseBody = response.getBody();

        // JSON 파싱 및 게임 리스트 반환
        List<SteamGameLibrary> gameList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(responseBody);
            JsonNode gamesNode = rootNode.path("response").path("games");
            for (JsonNode gameNode : gamesNode) {
                JsonNode appIdNode = gameNode.get("appid");
                JsonNode nameNode = gameNode.get("name");

                Integer appId = (appIdNode != null && !appIdNode.isNull()) ? appIdNode.asInt() : null;
                String name = (nameNode != null && !nameNode.isNull()) ? nameNode.asText() : "";

                if (appId != null) {
                    SteamGameLibrary gameLibrary = new SteamGameLibrary(appId, name);
                    gameList.add(gameLibrary);
                }
            }
        } catch (IOException e) {
            // 예외 처리 로직을 추가하세요.
        }

        return gameList;
    }
}



