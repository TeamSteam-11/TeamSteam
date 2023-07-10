package com.ll.TeamSteam.domain.matching.repository;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
    Optional<Matching> findById(Long id);

    Page<Matching> findAll(Pageable pageable);

    Page<Matching> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Matching> findByContentContainingIgnoreCase(String keyword, Pageable pageable);

    // 장르
    Page<Matching> findByGenre(GenreTagType genreType, Pageable pageable);

    // 시간
    Page<Matching> findByStartTime(Integer startTime, Pageable pageable);

    // 성별
    Page<Matching> findByGender(String gender, Pageable pageable);

    // 장르, 시간
    Page<Matching> findByGenreAndStartTime(GenreTagType genreTagType, Integer startTime, Pageable pageable);

    // 장르, 성별
    Page<Matching> findByGenreAndGender(GenreTagType genreTagType, String Gender, Pageable pageable);

    // 시간, 성별
    Page<Matching> findByStartTimeAndGender(Integer startTime, String Gender, Pageable pageable);

    // 장르, 시간, 성별
    Page<Matching> findByGenreAndStartTimeAndGender(GenreTagType genreTagType, Integer startTime, String Gender, Pageable pageable);
}
