package com.ll.TeamSteam.domain.genreTag.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ll.TeamSteam.domain.genreTag.entity.GenreTag;
import com.ll.TeamSteam.domain.genreTag.repository.GenreTagRepository;
import com.ll.TeamSteam.domain.userTag.entity.UserTag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreTagService {

	private final GenreTagRepository genreTagRepository;

	public void save(GenreTag genreTag){
		genreTagRepository.save(genreTag);
	}

	public void saveAll(List<GenreTag> genreTags) {
		genreTagRepository.saveAll(genreTags);
	}

	public void deleteAll(List<GenreTag> genreTag) {
		genreTagRepository.deleteAll(genreTag);
	}
}
