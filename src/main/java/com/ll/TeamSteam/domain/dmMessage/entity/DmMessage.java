package com.ll.TeamSteam.domain.dmMessage.entity;

import com.ll.TeamSteam.domain.dm.entity.Dm;
import com.ll.TeamSteam.domain.dmUser.entity.DmUser;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
@Slf4j
public class DmMessage extends BaseEntity {
    private String content; // 내용

    @ManyToOne(fetch = LAZY)
    private DmUser sender; // 작성자

    @ManyToOne(fetch = LAZY)
    private Dm dm; // 해당 채팅방 룸

    public static DmMessage create(String content, DmUser dmSender, Dm dm) {

        DmMessage dmMessage = DmMessage.builder()
                .content(content)
                .sender(dmSender)
                .dm(dm)
                .build();

        return dmMessage;
    }
}
