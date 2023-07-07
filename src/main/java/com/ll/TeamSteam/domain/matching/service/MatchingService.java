package com.ll.TeamSteam.domain.matching.service;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.entity.SearchQuery;
import com.ll.TeamSteam.domain.matching.repository.MatchingRepository;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {
    public final MatchingRepository matchingRepository;

    // 매칭 등록 기능
    public Matching create(User user, String title, String content, GenreTagType genreTag, Integer gameTagId, Long capacity, int startTime, int endTime, LocalDateTime deadlineDate) {
        Matching matching = Matching
                .builder()
                .user(user)
                .title(title)
                .content(content)
                .genre(genreTag)
                .gameTagId(gameTagId)
                .capacity(capacity)
                .startTime(startTime)
                .endTime(endTime)
                .deadlineDate(deadlineDate)
                .build();

        matchingRepository.save(matching);

        return matching;
    }

    // 마감 시간 구현
    public LocalDateTime setDeadline(LocalDateTime modifyDate, int hours) {
        return modifyDate.plusHours(hours);
    }

    public Page<Matching> getMatchingList(Pageable pageable) {

        return matchingRepository.findAll(pageable);
    }

    public Optional<Matching> findById(Long id) {
        return matchingRepository.findById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        matchingRepository.deleteById(id);
    }

    @Transactional
    public RsData<Matching> modify(Matching matching, String title, String content, GenreTagType genreTag, Long capacity, int startTime, int endTime) {
        try {
            matching.update(title, content, genreTag, capacity, startTime, endTime);
            matchingRepository.save(matching);

            return RsData.of("S-1", "매칭이 수정되었습니다", matching);
        } catch (Exception e) {
            e.printStackTrace();
            return RsData.of("F-1", "매칭 수정 중 오류가 발생했습니다.");
        }
    }

    public Page<Matching> searchMatching(String name, String keyword, Pageable pageable) {
        SearchQuery searchQuery = new SearchQuery(keyword);
        log.info("keyword = {}", keyword);

        Page<Matching> matchingList;

        if (name.equals("title")) {
            matchingList = matchingRepository.findByTitleContainingIgnoreCase(searchQuery.getValue(), pageable);
        } else if (name.equals("content")) {
            matchingList = matchingRepository.findByContentContainingIgnoreCase(searchQuery.getValue(), pageable);
        } else {
            throw new IllegalArgumentException("검색 쿼리가 잘 작성되지 않았음");
        }

        return matchingList;
    }
}
