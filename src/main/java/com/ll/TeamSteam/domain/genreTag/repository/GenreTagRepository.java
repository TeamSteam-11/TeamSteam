package com.ll.TeamSteam.domain.genreTag.repository;

import com.ll.TeamSteam.domain.genreTag.entity.GenreTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreTagRepository extends JpaRepository<GenreTag, Long> {
    List<GenreTag> findByUserTagId(Long id);

}
