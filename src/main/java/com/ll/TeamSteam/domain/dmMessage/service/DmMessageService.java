package com.ll.TeamSteam.domain.dmMessage.service;

import com.ll.TeamSteam.domain.dm.entity.Dm;
import com.ll.TeamSteam.domain.dm.service.DmService;
import com.ll.TeamSteam.domain.dmMessage.dto.DmMessageDto;
import com.ll.TeamSteam.domain.dmMessage.entity.DmMessage;
import com.ll.TeamSteam.domain.dmMessage.repository.DmMessageRepository;
import com.ll.TeamSteam.domain.dmUser.entity.DmUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DmMessageService {

    private final DmService dmService;
    private final DmMessageRepository dmMessageRepository;

    @Transactional
    public DmMessage createAndSave(String content, Long dmSenderId, Long dmId) {

        Dm dm = dmService.findByDmId(dmId);

        DmUser sender = dm.getDmUsers().stream()
                .filter(dmUser -> dmUser.getUser().getId().equals(dmSenderId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("으아아아아아"));
        // TODO : Exception 만들어서 던져주기

        DmMessage dmMessage = DmMessage.create(content, sender, dm);

        return dmMessageRepository.save(dmMessage);
    }

    public List<DmMessageDto> getByDmIdAndUserIdAndFromId(Long dmId, Long userId, Long fromMessageId) {

        Dm dm = dmService.findByDmId(dmId);

        dm.getDmUsers().stream()
                .filter(dmUser -> dmUser.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow();

        List<DmMessage> dmMessages = dmMessageRepository.findByDmId(dmId);

        List<DmMessage> list = dmMessages.stream()
                .filter(chatMessage -> chatMessage.getId() > fromMessageId)
                .sorted(Comparator.comparing(DmMessage::getId))
                .toList();

        return DmMessageDto.fromDmMessages(list);
    }
}
