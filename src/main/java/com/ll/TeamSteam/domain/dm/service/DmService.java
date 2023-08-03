package com.ll.TeamSteam.domain.dm.service;

import com.ll.TeamSteam.domain.dm.dto.DmDto;
import com.ll.TeamSteam.domain.dm.entity.Dm;
import com.ll.TeamSteam.domain.dm.exception.NoDmException;
import com.ll.TeamSteam.domain.dm.repository.DmRepository;
import com.ll.TeamSteam.domain.dmUser.entity.DmUser;
import com.ll.TeamSteam.domain.dmUser.service.DmUserService;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class DmService {

    private final DmRepository dmRepository;
    private final UserService userService;
    private final DmUserService dmUserService;

    public Dm findByDmId(Long dmId) {
        return dmRepository.findById(dmId)
                .orElseThrow(() -> new NoDmException("DM을 찾을 수 없습니다."));
    }

    @Transactional
    public Dm createDmAndEnterDm(Long receiverId, Long dmSenderId) {
        User dmSender = userService.findByIdElseThrow(dmSenderId);
        log.info("dmSenderId = {}", dmSender.getId());
        User receiver = userService.findByIdElseThrow(receiverId);
        log.info("receiver = {}", receiver.getId());

        Dm dm = Dm.create(dmSender, receiver);

        Dm dmSave = dmRepository.save(dm);
        addDmUser(dm, dmSender);
        addDmUser(dm, receiver);

        return dmSave;
    }

    @Transactional
    public DmDto validEnterDm(Long dmId, Long userId) {
        User user = userService.findByIdElseThrow(userId);

        Dm dm = findByDmId(dmId);

        dmUserService.findDmUserByUserId(dm, user.getId());

        return DmDto.fromDmDto(dm, user);
    }

    private void addDmUser(Dm dm, User user) {
        if (getDmUser(dm, user.getId()).isEmpty()) {
            dm.addDmUser(user);
        }
    }

    private Optional<DmUser> getDmUser(Dm dm, Long userId) {
        Optional<DmUser> existingUser = dm.getDmUsers().stream()
                .filter(DmUser -> DmUser.getUser().getId().equals(userId))
                .findFirst();

        return existingUser;
    }

    public Optional<Dm> findByDmSenderAndDmReceiver(User dmSender, User dmReceiver) {
        return dmRepository.findByDmSenderAndDmReceiver(dmSender, dmReceiver);
    }

    public List<Dm> findByDmSenderIdOrDmReceiverId(Long dmSenderId, Long dmReceiverId) {
        return dmRepository.findByDmSenderIdOrDmReceiverId(dmSenderId, dmReceiverId);
    }
}
