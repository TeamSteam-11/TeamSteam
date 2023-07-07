package com.ll.TeamSteam.domain.recentlyUser.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser;

@Repository
public interface RecentlyUserRepository extends JpaRepository<RecentlyUser, Long> {
	Optional<RecentlyUser> findById(Long id);

	List<RecentlyUser> findAllByUserId(Long userId);
}
