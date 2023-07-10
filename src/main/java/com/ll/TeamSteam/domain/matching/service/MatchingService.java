package com.ll.TeamSteam.domain.matching.service;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.entity.SearchQuery;
import com.ll.TeamSteam.domain.matching.repository.MatchingRepository;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.userTag.gameTag.GameTag;
import com.ll.TeamSteam.domain.userTag.gameTag.GameTagRepository;
import com.ll.TeamSteam.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {
    public final MatchingRepository matchingRepository;
    public final GameTagRepository gameTagRepository;

    // 매칭 등록 기능
    public Matching create(User user, String title, String content, GenreTagType genreTag, Integer gameTagId,String gender, Long capacity, int startTime, int endTime, LocalDateTime deadlineDate) {

        Optional<GameTag> gameTag = gameTagRepository.findByAppid(gameTagId);
        String gameTagName = "게임이름을 불러올 수 없습니다";
        if(gameTag.isPresent()){
            GameTag gameTag1 = gameTag.orElseThrow();
            gameTagName = gameTag1.getName();
        }


        Matching matching = Matching
                .builder()
                .user(user)
                .title(title)
                .content(content)
                .genre(genreTag)
                .gameTagId(gameTagId)
                .gender(gender)
                .participant(1L)
                .gameTagName(gameTagName)
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
    public RsData<Matching> modify(Matching matching, String title, String content, GenreTagType genreTag, String gender, Long capacity, int startTime, int endTime) {
        try {
            matching.update(title, content, genreTag, gender, capacity, startTime, endTime);
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

        Map<String, Function<String, Page<Matching>>> methodMap = new HashMap<>();
        methodMap.put("title", value -> matchingRepository.findByTitleContainingIgnoreCase(value, pageable));
        methodMap.put("content", value -> matchingRepository.findByContentContainingIgnoreCase(value, pageable));

        Function<String, Page<Matching>> method = methodMap.get(name);
        if (method != null) {
            matchingList = method.apply(searchQuery.getValue());
        } else {
            throw new IllegalArgumentException("검색 쿼리가 잘 작성되지 않았음");
        }

        return matchingList;
    }

    // 쿼리 DSL
    public Page<Matching> filterMatching(GenreTagType genreType, Integer startTime, String gender, Pageable pageable) {

        if (genreType != null && startTime != null) {
            // 장르와 시간
            return matchingRepository.findByGenreAndStartTime(genreType, startTime, pageable);
        } else if (genreType != null) {
            // 장르
            return matchingRepository.findByGenre(genreType, pageable);
        } else if (startTime != null) {
            // 시작시간
            return matchingRepository.findByStartTime(startTime, pageable);
        } else if (!gender.isEmpty()) {
            // 성별
            return matchingRepository.findByGender(gender, pageable);
        } else if (genreType != null && gender.isEmpty()) {
            // 장르와 성별
            return matchingRepository.findByGenreAndGender(genreType, gender, pageable);
        } else if (startTime != null && gender.isEmpty()) {
            // 성별과 시간
            return matchingRepository.findByStartTimeAndGender(startTime, gender, pageable);
        } else if (genreType != null && startTime != null && gender.isEmpty()){
            // 장르, 성별, 시간
            return matchingRepository.findByGenreAndStartTimeAndGender(genreType, startTime, gender, pageable);
        }
        return matchingRepository.findAll(pageable);
    }
}
