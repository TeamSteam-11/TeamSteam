package com.ll.TeamSteam.domain.recentlyUser.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser;
import com.ll.TeamSteam.domain.user.entity.User;

@Repository
public interface RecentlyUserRepository extends JpaRepository<RecentlyUser, Long> {
	Optional<RecentlyUser> findById(Long id);

	List<RecentlyUser> findAllByUserId(Long userId);

	boolean existsByUserAndMatchingPartner(User user, MatchingPartner matchingPartner);

	List<RecentlyUser> findByUserId(Long userId);
}
