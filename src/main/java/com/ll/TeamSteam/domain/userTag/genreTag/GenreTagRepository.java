package com.ll.TeamSteam.domain.userTag.genreTag;

import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.userTag.gameTag.GameTag;
import com.ll.TeamSteam.domain.userTag.genreTag.GenreTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreTagRepository extends JpaRepository<GenreTag, Long> {
    List<GenreTag> findByUserTagId(Long id);

}
