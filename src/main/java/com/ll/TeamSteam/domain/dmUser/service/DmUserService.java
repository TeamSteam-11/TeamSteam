package com.ll.TeamSteam.domain.dmUser.service;

import com.ll.TeamSteam.domain.chatRoom.exception.NotInChatRoomException;
import com.ll.TeamSteam.domain.dm.entity.Dm;
import com.ll.TeamSteam.domain.dmUser.entity.DmUser;
import com.ll.TeamSteam.domain.dmUser.repository.DmUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DmUserService {

    private final DmUserRepository dmUserRepository;

    public DmUser findDmUserByUserId(Dm dm, Long userId) {
        return dm.getDmUsers().stream()
                .filter(chatUser -> chatUser.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotInChatRoomException("해당 방에 참가하지 않았어!"));
    }
}
