package com.ll.TeamSteam.domain.matching.service;

import com.ll.TeamSteam.domain.gameTag.entity.GameTag;
import com.ll.TeamSteam.domain.gameTag.repository.GameTagRepository;
import com.ll.TeamSteam.domain.matching.entity.CreateForm;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {
    public final MatchingRepository matchingRepository;
    public final GameTagRepository gameTagRepository;

    // 매칭 등록 기능
    public Matching create(User user, String title, String content, GenreTagType genreTag, Integer gameTagId, String gender, Long capacity, Integer startTime, Integer endTime, LocalDateTime deadlineDate, boolean isMic) {

        Optional<GameTag> gameTag = gameTagRepository.findByAppidAndUserTagId(gameTagId, user.getUserTag().getId());
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
                .isMic(isMic)
                .build();

        matchingRepository.save(matching);

        return matching;
    }

    // 마감 시간 구현
    public LocalDateTime setDeadline(LocalDateTime modifyDate, int selectedHours) {
        return modifyDate.plusHours(selectedHours);
    }

    // 사용자가 선택한 마감 시간을 설정하여 매칭 생성에 사용
    public LocalDateTime calculateDeadline(LocalDateTime modifyDate, int selectedHours) {
        LocalDateTime deadlineDate;
        if (selectedHours > 0) {
            deadlineDate = setDeadline(modifyDate, selectedHours);
        } else {
            // 마감 시간을 '제한 없음'으로 선택 시 매칭 마감일이 30일 이후로 저장됨
            deadlineDate = modifyDate.plusDays(30);
        }
        return deadlineDate;
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
    public RsData<Matching> modify(Matching matching, String title, String content, GenreTagType genreTag, String gender, Long capacity, Integer startTime, Integer endTime, int selectedHours, boolean mic) {
        try {
            matching.update(title, content, genreTag, gender, capacity, startTime, endTime, mic);

            // 수정된 selectedHours를 기반으로 deadlineDate 계산
            LocalDateTime modifyDate = LocalDateTime.now();
            LocalDateTime deadlineDate;
            if (selectedHours > 0) {
                deadlineDate = setDeadline(modifyDate, selectedHours);
            } else {
                // 마감 시간을 '제한 없음'으로 선택 시 매칭 마감일이 30일 이후로 저장됨
                deadlineDate = modifyDate.plusDays(30);
            }
            matching.setDeadlineDate(deadlineDate);

            matchingRepository.save(matching);

            return RsData.of("S-1", "매칭이 수정되었습니다", matching);
        } catch (Exception e) {
            e.printStackTrace();
            return RsData.of("F-1", "매칭 수정 중 오류가 발생했습니다.");
        }
    }

    public int calculateSelectedHours(long id, LocalDateTime deadlineDate) {
        Matching matching = matchingRepository.findById(id).orElseThrow();

        if (deadlineDate == null) {
            return 0;
        }
        LocalDateTime modifyDate = matching.getModifyDate();
        Duration duration = Duration.between(modifyDate, deadlineDate);
        return (int) duration.toHours();
    }

    public Page<Matching> searchMatching(String name, String keyword, Pageable pageable) {
        SearchQuery searchQuery = new SearchQuery(keyword);
        log.info("keyword = {}", keyword);

        Page<Matching> matchingList;

        Map<String, Function<String, Page<Matching>>> methodMap = new HashMap<>();
        methodMap.put("title", value -> matchingRepository.findByTitleContainingIgnoreCase(value, pageable));
        methodMap.put("content", value -> matchingRepository.findByContentContainingIgnoreCase(value, pageable));
        methodMap.put("writer", value -> matchingRepository.findByUserUsernameContainingIgnoreCase(value, pageable));

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
        return matchingRepository.filterByGenreAndStartTimeAndGender(genreType, startTime, gender, pageable);
    }

    public List<Matching> getSortedMatchingByDeadline() {
        List<Matching> matchingList = matchingRepository.findAll();
        List<Matching> approachingDeadlineList = new ArrayList<>();

        for (Matching matching : matchingList) {
            String deadlineDuration = matching.getDeadlineDuration();
            if (deadlineDuration != null && !deadlineDuration.isEmpty()) {
                approachingDeadlineList.add(matching);
            }
        }

        Comparator<Matching> deadlineComparator = Comparator.comparing(matching -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime deadlineDate = matching.getDeadlineDate();
            if (now != null && deadlineDate != null) {
                return Duration.between(now, deadlineDate);
            }
            return Duration.ZERO;
        });

        Collections.sort(approachingDeadlineList, deadlineComparator);

        return approachingDeadlineList.stream()
                .limit(6)
                .collect(Collectors.toList());
    }

    public List<Matching> getSortedMatchingByParticipant() {
        List<Matching> matchingList = matchingRepository.findAll();

        // capacity에서 participant를 뺀 값을 기준으로 정렬
        return matchingList.stream()
                .filter(matching -> matching.getRemainingCapacity() > 0)
                .sorted(Comparator.comparing(Matching::getRemainingCapacity))
                .limit(6)
                .collect(Collectors.toList());
    }

    public void save(Matching matching) {
        matchingRepository.save(matching);
    }

    public CreateForm setCreateForm(Matching matching) {
        CreateForm createForm = new CreateForm();

        createForm.setTitle(matching.getTitle());
        createForm.setContent(matching.getContent());
        createForm.setGenre(matching.getGenre());
        createForm.setGameTagId(matching.getGameTagId());
        createForm.setGender(matching.getGender());
        createForm.setCapacity(matching.getCapacity());
        createForm.setStartTime(matching.getStartTime());
        createForm.setEndTime(matching.getEndTime());
        createForm.setSelectedHours(calculateSelectedHours(matching.getId(), matching.getDeadlineDate()));
        createForm.setMic(matching.isMic());

        return createForm;
    }

    public Optional<Matching> findByTitle(String title){
        return matchingRepository.findByTitle(title);
    }

}
