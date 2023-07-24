package com.ll.TeamSteam.domain.genreTag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.ll.TeamSteam.domain.genreTag.entity.GenreTag;

@Repository
public interface GenreTagRepository extends JpaRepository<GenreTag, Long> {
    List<GenreTag> findByUserTagId(Long id);

}
