package com.ll.TeamSteam.domain.matchingPartner.service;

import java.util.List;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.service.MatchingService;
import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.matchingPartner.repository.MatchingPartnerRepository;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchingPartnerService {
    private final MatchingPartnerRepository matchingPartnerRepository;
    private final UserService userService;
    private final MatchingService matchingService;

    @Transactional
    public void addPartner(Long matchingId, Long currentUserId) {
        User user = userService.findByIdElseThrow(currentUserId);
        Matching matching = matchingService.findById(matchingId).orElseThrow();

        MatchingPartner matchingPartner = MatchingPartner.builder()
                .matching(matching)
                .user(user)
                .inChatRoomTrueFalse(false)
                .build();

        matchingPartnerRepository.save(matchingPartner);
    }

    // 검증 로직
    public boolean isDuplicatedMatchingPartner(Long matchingId, Long currentUserId) {
        User user = userService.findByIdElseThrow(currentUserId);
        Matching matching = matchingService.findById(matchingId).orElseThrow();

        if (matching.getId() == null) {
            throw new IllegalArgumentException("매칭이 존재하지 않음");
        }

        return matchingPartnerRepository.existsByMatchingAndUser(matching, user);
    }

    @Transactional
    public void updateTrue(Long matchingId, Long currentUserId) {
        User user = userService.findByIdElseThrow(currentUserId);
        Matching matching = matchingService.findById(matchingId).orElseThrow();

        if (matching.getId() == null) {
            throw new IllegalArgumentException("매칭이 존재하지 않음");
        }

        /**
         * moveChatController 에서 검증을 한번 한 후 service 쪽에서도 검증
         */
        MatchingPartner matchingPartner = matchingPartnerRepository.findByMatchingAndUser(matching, user)
                .orElseThrow(() -> new IllegalArgumentException("매칭 파트너를 찾을 수 없어"));

        matchingPartner.updateInChatRoomTrueFalse(true);
    }
    public List<MatchingPartner> findByMatching(Matching matching){
        return matchingPartnerRepository.findByMatching(matching);
    }

    // public List<MatchingPartner> findByMatchingByUserId(Long userId) {
    //     return matchingPartnerRepository.findByMatchingByUserId(userId);
    // }

    public List<MatchingPartner> findByAll() {
        return matchingPartnerRepository.findAll();
    }

    public List<MatchingPartner> findByMatchingId(Long matchingId) {
        return matchingPartnerRepository.findByMatchingId(matchingId);
    }
}
