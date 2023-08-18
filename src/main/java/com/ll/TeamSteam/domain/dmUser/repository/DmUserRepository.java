package com.ll.TeamSteam.domain.dmUser.repository;

import com.ll.TeamSteam.domain.dmUser.entity.DmUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DmUserRepository extends JpaRepository<DmUser, Long> {
}

