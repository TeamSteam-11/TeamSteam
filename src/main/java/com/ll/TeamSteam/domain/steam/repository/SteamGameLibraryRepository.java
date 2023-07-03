package com.ll.TeamSteam.domain.steam.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary;

@Repository
public interface SteamGameLibraryRepository extends JpaRepository<SteamGameLibrary, Long> {

	SteamGameLibrary findByAppid(Integer appId);
}


