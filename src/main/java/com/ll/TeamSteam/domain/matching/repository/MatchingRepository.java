package com.ll.TeamSteam.domain.matching.repository;

import com.ll.TeamSteam.domain.matching.entity.Matching;
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
}
