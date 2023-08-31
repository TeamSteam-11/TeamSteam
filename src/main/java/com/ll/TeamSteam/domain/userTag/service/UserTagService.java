package com.ll.TeamSteam.domain.userTag.service;

import com.ll.TeamSteam.domain.userTag.entity.UserTag;
import com.ll.TeamSteam.domain.userTag.repository.UserTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserTagService {

	private final UserTagRepository userTagRepository;
	@Transactional
	public void save(UserTag userTag){
		userTagRepository.save(userTag);
	}

	public UserTag findByUserId(Long id) {
		return userTagRepository.findByUserId(id).orElseThrow();
	}
}
