package com.ll.TeamSteam.domain.steam.repository;


import com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SteamGameLibraryRepository extends JpaRepository<SteamGameLibrary, Long> {

	SteamGameLibrary findByAppidAndUserId(Integer appId, Long UserId);
	void deleteByUserId(Long userId);
	List<SteamGameLibrary> findAllByUserId(Long userId);

}


