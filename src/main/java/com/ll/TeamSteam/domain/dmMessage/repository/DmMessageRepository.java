package com.ll.TeamSteam.domain.dmMessage.repository;

import com.ll.TeamSteam.domain.dmMessage.entity.DmMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DmMessageRepository extends MongoRepository<DmMessage, Long> {

    List<DmMessage> findByDmId(Long dmId);

}
