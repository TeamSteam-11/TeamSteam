package com.ll.TeamSteam.domain.genreTag.service;

import com.ll.TeamSteam.domain.genreTag.entity.GenreTag;
import com.ll.TeamSteam.domain.genreTag.repository.GenreTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreTagService {

	private final GenreTagRepository genreTagRepository;
	@Transactional
	public void save(GenreTag genreTag){
		genreTagRepository.save(genreTag);
	}
	@Transactional
	public void saveAll(List<GenreTag> genreTags) {
		genreTagRepository.saveAll(genreTags);
	}
	@Transactional
	public void deleteAll(List<GenreTag> genreTag) {
		genreTagRepository.deleteAll(genreTag);
	}
}
