package com.ll.TeamSteam.domain.matching.repository;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;


public interface MatchingRepositoryCustom {

    Page<Matching> filterByGenreAndStartTimeAndGender(GenreTagType genreType, Integer startTime, String gender, Pageable pageable);
}
