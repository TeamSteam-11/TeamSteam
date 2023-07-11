package com.ll.TeamSteam.domain.matchingPartner.repository;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchingPartnerRepository extends JpaRepository<MatchingPartner, Long> {
    boolean existsByMatchingAndUser(Matching matching, User user);

    Optional<MatchingPartner> findByMatchingAndUser(Matching matching, User user);
    List<MatchingPartner> findByMatching(Matching matching);

    List<MatchingPartner> findByMatchingId(Long matchingId);

	List<MatchingPartner> findByUserId(Long userId);

    MatchingPartner findByMatchingIdAndUserId(Long matchingId, Long userId);
}
