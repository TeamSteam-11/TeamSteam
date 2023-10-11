package com.ll.TeamSteam.domain.matching.repository;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MatchingRepositoryCustom {

    Page<Matching> filterAndSearchByConditions(String name, String keyword, GenreTagType genreType, Integer startTime, String gender, Pageable pageable);
}
