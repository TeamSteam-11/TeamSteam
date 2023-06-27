package com.ll.TeamSteam.domain.matching.repository;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
}
