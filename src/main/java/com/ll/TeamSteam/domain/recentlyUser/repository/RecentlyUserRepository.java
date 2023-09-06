package com.ll.TeamSteam.domain.recentlyUser.repository;

import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser;
import com.ll.TeamSteam.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentlyUserRepository extends JpaRepository<RecentlyUser, Long> {
	Optional<RecentlyUser> findById(Long id);

	List<RecentlyUser> findAllByUserId(Long userId);
	boolean existsByUserIdAndPartnerUserId(Long userId, Long partnerUserId);

	List<RecentlyUser> findByUserId(Long userId);
}
