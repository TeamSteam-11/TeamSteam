package com.ll.TeamSteam.domain.friend.service;

import com.ll.TeamSteam.domain.friend.entity.Friend;
import com.ll.TeamSteam.domain.friend.repository.FriendRepository;
import com.ll.TeamSteam.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService {

	private final FriendRepository friendRepository;

	public List<Friend> findAllByUserId(Long userId){
		return friendRepository.findAllByUserId(userId);
	}

	public void save(Friend friend) {
		friendRepository.save(friend);
	}

	public Friend findByUserAndFriend(User loginedUser, User targetUser) {
		return friendRepository.findByUserAndFriend(loginedUser,targetUser);
	}

	public void delete(Friend friend) {
		friendRepository.delete(friend);
	}
}
