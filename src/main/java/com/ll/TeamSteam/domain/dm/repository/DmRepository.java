package com.ll.TeamSteam.domain.dm.repository;

import com.ll.TeamSteam.domain.dm.entity.Dm;
import com.ll.TeamSteam.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DmRepository extends JpaRepository<Dm, Long> {
    Optional<Dm> findByDmSenderAndDmReceiver(User dmSender,User dmReceiver);
    List<Dm> findByDmSenderIdOrDmReceiverId(Long dmSenderId, Long dmReceiverId);
}
