package com.ll.TeamSteam.domain.steam.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary;
import com.ll.TeamSteam.domain.steam.repository.SteamGameLibraryRepository;
import com.ll.TeamSteam.global.security.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SteamGameLibraryService {

    @Value("${steam.apiKey}")
    private String apiKey;

    @Value("${steam.baseUrl}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    private final SteamGameLibraryRepository steamGameLibraryRepository;

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

    // public String getBaseUrl(String steamId, String role){
    //     String url = "";
    //     if(role.equals("gameList")) { // 이렇게까지 해야할까... ㅋㅋㅋㅋㅋㅋ 한번봐주세요
    //         url = getGameListUrl();
    //     }
    //     if(role.equals("userInfo")){
    //         url = getUserInfoUrl();
    //     }
    //     UriComponents builder = UriComponentsBuilder
    //         .fromHttpUrl(url)
    //         .buildAndExpand(apiKey, steamId);
    //
    //     return builder.toUriString();
    // }
    //
    // public String getGameListUrl(){
    //     return baseUrl + "/IPlayerService/GetOwnedGames/v0001/?key={apiKey}&steamid={steam_id}" +
    //         "&include_appinfo=true&format=json";
    // }
    //
    // public String getUserInfoUrl(){
    //     return baseUrl
    //         + "/ISteamUser/GetPlayerSummaries/v2?key={apiKey}&steamids={steam_id}";
    // }
    @Transactional
    public void save(SteamGameLibrary steamGameLibrary) {
         steamGameLibraryRepository.save(steamGameLibrary);
    }

    public List<SteamGameLibrary> findAllByUserId(Long userId) {
        return steamGameLibraryRepository.findAllByUserId(userId);
    }
    @Transactional
    public void deleteByUserId(Long userId) {
        steamGameLibraryRepository.deleteByUserId(userId);
    }
    @Transactional
    public void saveAll(List<SteamGameLibrary> gameLibraries) {
        steamGameLibraryRepository.saveAll(gameLibraries);
    }

    public SteamGameLibrary findByAppidAndUserId(Integer appId, Long userId) {
       return steamGameLibraryRepository.findByAppidAndUserId(appId, userId);
    }
}



