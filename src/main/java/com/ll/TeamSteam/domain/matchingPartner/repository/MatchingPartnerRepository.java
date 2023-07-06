package com.ll.TeamSteam.domain.matchingPartner.repository;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingPartnerRepository extends JpaRepository<MatchingPartner, Long> {
    boolean existsByMatchingAndUser(Matching matching, User user);
}
