package com.ll.TeamSteam.domain.matching.repository;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long>, MatchingRepositoryCustom{
    Optional<Matching> findById(Long id);

    //Test를 위한 코드입니다. 이 메서드를 사용시 중복 타이틀이 있을 경우 문제가 생깁니다.
    Optional<Matching> findByTitle(String title);

    Page<Matching> findAll(Pageable pageable);

    // LIKE 검색을 없애자

    Page<Matching> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Matching> findByContentContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Matching> findByUserUsernameContainingIgnoreCase(String keyword, Pageable pageable);
}
