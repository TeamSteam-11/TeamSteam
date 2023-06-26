package com.ll.TeamSteam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class TeamSteamApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamSteamApplication.class, args);
	}

}