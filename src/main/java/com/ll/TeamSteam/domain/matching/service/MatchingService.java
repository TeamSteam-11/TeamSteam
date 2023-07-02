package com.ll.TeamSteam.domain.matching.service;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.repository.MatchingRepository;
import com.ll.TeamSteam.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchingService {
    public final MatchingRepository matchingRepository;

    // 매칭 등록 기능
    public RsData<Matching> create(String title, String content, Long capacity, int startTime, int endTime, LocalDateTime deadlineDate) {
        Matching matching = Matching
                .builder()
                .title(title)
                .content(content)
                .capacity(capacity)
                .startTime(startTime)
                .endTime(endTime)
                .deadlineDate(deadlineDate)
                .build();

        matchingRepository.save(matching);

        return RsData.of("S-1", "입력하신 매칭이 등록되었습니다.", matching);
    }

    // 마감 시간 구현
    public LocalDateTime setDeadline(LocalDateTime modifyDate, int hours) {
        return modifyDate.plusHours(hours);
    }

    public List<Matching> getMachingList() {
        return matchingRepository.findAll();
    }

    public Matching getMatching(Long id) {
        Optional<Matching> matching = matchingRepository.findById(id);

        return matching.get();
    }
}
