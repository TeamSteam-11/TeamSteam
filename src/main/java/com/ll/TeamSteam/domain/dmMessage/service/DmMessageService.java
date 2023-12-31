package com.ll.TeamSteam.domain.dmMessage.service;

import com.ll.TeamSteam.domain.dm.entity.Dm;
import com.ll.TeamSteam.domain.dm.service.DmService;
import com.ll.TeamSteam.domain.dmMessage.dto.DmMessageDto;
import com.ll.TeamSteam.domain.dmMessage.entity.DmMessage;
import com.ll.TeamSteam.domain.dmMessage.repository.DmMessageRepository;
import com.ll.TeamSteam.domain.dmUser.entity.DmUser;
import com.ll.TeamSteam.domain.dmUser.service.DmUserService;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.filter.CleanXss;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DmMessageService {

    private final DmService dmService;
    private final DmMessageRepository dmMessageRepository;
    private final DmUserService dmUserService;

    @Transactional
    public DmMessage createAndSave(String content, User sender, Long dmId) {

        Dm dm = dmService.findByDmId(dmId);

        String cleanContent = CleanXss.replaceXssAnsSqlInjection(content);

        if (sender == null){
            throw new IllegalArgumentException("너 로그인 안했어~!");
        }

        dmUserService.findDmUserByUserId(dm, sender.getId());

        DmMessage dmMessage = DmMessage.create(cleanContent, sender.getId(), sender.getUsername(), sender.getAvatar(), dm.getId());

        dmMessage.validateLength(dmMessage.getContent());

        return dmMessageRepository.save(dmMessage);
    }

    public List<DmMessage> getChatMessagesByRoomId(Long dmId) {

        return dmMessageRepository.findByDmId(dmId);
    }

    public List<DmMessageDto> getByDmIdAndUserIdAndFromId(Long dmId, Long userId, String fromMessageId) {

        Dm dm = dmService.findByDmId(dmId);

        DmUser dmuser = dm.getDmUsers().stream()
                .filter(dmUser -> dmUser.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("하하"));

        log.info("dmUser = {}", dmuser);

        List<DmMessage> dmMessages = dmMessageRepository.findByDmId(dmId);

        List<DmMessage> list = dmMessages.stream()
                .filter(chatMessage -> new ObjectId(chatMessage.getId()).compareTo(new ObjectId(fromMessageId)) > 0)
                .sorted(Comparator.comparing(DmMessage::getId))
                .collect(Collectors.toList());

        return DmMessageDto.fromDmMessages(list);
    }
}
