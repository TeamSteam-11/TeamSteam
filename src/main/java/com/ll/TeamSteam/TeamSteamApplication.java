package com.ll.TeamSteam;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.ToString;
import org.springframework.data.mongodb.core.MongoTemplate;

@EnableJpaAuditing
@SpringBootApplication
public class TeamSteamApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamSteamApplication.class, args);
	}

//	@Bean
//	@Profile("local")
//	public CommandLineRunner initData(MongoTemplate mongoTemplate) {
//		return (args) -> {
//			// 데이터베이스 삭제
//			mongoTemplate.getDb().drop();
//
//			// 이후의 초기화 로직이나 데이터 추가 등의 작업을 여기에 추가할 수 있습니다.
//		};
//	}

}