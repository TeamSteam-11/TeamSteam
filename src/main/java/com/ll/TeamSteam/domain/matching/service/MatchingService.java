package com.ll.TeamSteam.domain.matching.service;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.ll.TeamSteam.domain.matching.controller.MatchingController;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.repository.MatchingRepository;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.model.jdbc.OptionalTableUpdateOperation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchingService {
    public final MatchingRepository matchingRepository;

    // 매칭 등록 기능
    public Matching create(User user, String title, String content, Long capacity, int startTime, int endTime, LocalDateTime deadlineDate) {
        Matching matching = Matching
                .builder()
                .user(user)
                .title(title)
                .content(content)
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

    public List<Matching> getMachingList() {
        return matchingRepository.findAll();
    }

    public Matching findById(Long id) {
        Optional<Matching> matching = matchingRepository.findById(id);

        // 존재하지 않는 매칭의 id가 입력되는 경우 에러 처리
        if (matching.isPresent() == false) throw new NoSuchElementException("매칭이 존재하지 않습니다.");

        return matching.get();
    }

    @Transactional
    public void deleteById(Long id) {
        matchingRepository.deleteById(id);
    }

    @Transactional
    public RsData<Matching> modify(Matching matching, User user, @Valid MatchingController.CreateForm createForm) {
        try {
            matching.setUser(user);
            matching.setTitle(createForm.getTitle());
            matching.setContent(createForm.getContent());
            matching.setCapacity(createForm.getCapacity());
            matching.setStartTime(createForm.getStartTime());
            matching.setEndTime(createForm.getEndTime());

            matchingRepository.save(matching);

            return RsData.of("S-1", "매칭이 수정되었습니다", matching);
        } catch (Exception e) {
            e.printStackTrace();
            return RsData.of("F-1", "매칭 수정 중 오류가 발생했습니다.");
        }
    }

}
