package com.ll.TeamSteam.domain.friend.repository;

import com.ll.TeamSteam.domain.friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findAllByUserId(Long userId);
}
