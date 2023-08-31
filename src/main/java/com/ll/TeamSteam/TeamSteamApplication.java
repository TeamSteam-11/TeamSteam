package com.ll.TeamSteam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TeamSteamApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamSteamApplication.class, args);
	}
}