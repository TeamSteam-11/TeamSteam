package com.ll.TeamSteam.global.security;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoResponse {
    private Response response;

    @Data
    public static class Response {
        private List<User> players;
    }

    @Data
    public static class User {
        private String steamid;
        private String personaname;
        private int profilestate;
        private String avatarmedium;

//        private int lastlogoff;  최근에 한 게임 유닉스 시간기준으로 볼수있음

        // 추가로 가져올 유저 정보 필드들을 여기에 추가합니다.
    }

}