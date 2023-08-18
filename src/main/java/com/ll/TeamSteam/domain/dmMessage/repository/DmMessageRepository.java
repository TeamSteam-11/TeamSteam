package com.ll.TeamSteam.domain.dmMessage.repository;

import com.ll.TeamSteam.domain.dmMessage.entity.DmMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DmMessageRepository extends JpaRepository<DmMessage, Long> {

    List<DmMessage> findByDmId(Long dmId);
}
