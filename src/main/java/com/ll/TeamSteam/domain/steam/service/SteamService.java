package com.ll.TeamSteam.domain.steam.service;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import com.ll.TeamSteam.global.security.UserInfoResponse;

import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

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

    public Map getUserGameList(String steamId) throws ParseException {
        String url = baseUrl
                + "/IPlayerService/GetOwnedGames/v1/?key={apiKey}&steamid={steam_id}" +
                "&include_appinfo=1&include_played_free_games=1";

        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl(url)
                .buildAndExpand(apiKey, steamId);

        ResponseEntity<String> response =
                restTemplate.getForEntity(builder.toUriString(), String.class);

        String responseBody = response.getBody();

        // Json-simple을 사용하여 String 형태의 응답을 JSON 객체로 파싱합니다.
        JSONParser parser = new JSONParser();
        JSONObject jsonResponse = (JSONObject) parser.parse(responseBody);

        Map<Integer, String> haveGameList = new HashMap<>();

        //필요한 필드만 추출
        JSONArray gamesArray = (JSONArray) ((JSONObject) jsonResponse.get("response")).get("games");
        for (Object gameObj : gamesArray) {
            JSONObject game = (JSONObject) gameObj;
            String name = (String) game.get("name");
            int appid = (int) game.get("appid");

            // 필요한 작업 수행
            haveGameList.put(appid, name);
            System.out.println("Name: " + name);
            System.out.println("App ID: " + appid);
        }

        return haveGameList;
    }
}
