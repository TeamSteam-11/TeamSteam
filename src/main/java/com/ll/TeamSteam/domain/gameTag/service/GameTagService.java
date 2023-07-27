package com.ll.TeamSteam.domain.gameTag.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ll.TeamSteam.domain.gameTag.entity.GameTag;
import com.ll.TeamSteam.domain.gameTag.repository.GameTagRepository;
import com.ll.TeamSteam.domain.userTag.entity.UserTag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameTagService {

	private final GameTagRepository gameTagRepository;

	public void save(GameTag gameTag){
		gameTagRepository.save(gameTag);
	}

	public void saveAll(List<GameTag> gameTags){ gameTagRepository.saveAll(gameTags);}

	public void deleteAll(List<GameTag> gameTag) {
		gameTagRepository.deleteAll(gameTag);
	}

	public void deleteByUserTag(UserTag userTag) {
		deleteByUserTag(userTag);
	}

}
