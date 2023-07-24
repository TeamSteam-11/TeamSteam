package com.ll.TeamSteam.domain.recentlyUser.service;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ll.TeamSteam.domain.matching.service.MatchingService;
import com.ll.TeamSteam.domain.matchingPartner.service.MatchingPartnerService;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser;
import com.ll.TeamSteam.domain.recentlyUser.repository.RecentlyUserRepository;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;

import static org.assertj.core.api.Assertions.*;
@SpringBootTest
public class RecentlyUserServiceTest {

	@Autowired
	private RecentlyUserService recentlyUserService;
	@Autowired
	private UserService userService;
	@Autowired
	private MatchingPartnerService matchingPartnerService;
	@Autowired
	private MatchingService matchingService;
	@Autowired
	private RecentlyUserRepository recentlyUserRepository;

	@Test
	@DisplayName("updateRecentlyUser")
	void t001() {
		//given 두명의 유저, 두명의 매칭파트너 , 하나의 매칭
		User user1 = userService.findByIdElseThrow(1L);
		User user2 = userService.findByIdElseThrow(2L);

		//두명의 매칭파트너
		matchingPartnerService.addPartner(user1.getId(),user2.getId());

		//하나의 매칭
		matchingService.create(user1,"실험매칭1","실험매칭입니다.", GenreTagType.액션RPG,1,user1.getType().toString(),1L,20,22,
			LocalDateTime.now());
		//when
		recentlyUserService.updateRecentlyUser(user1.getId());

		//then
		List<RecentlyUser> findRecentlyUserList =recentlyUserRepository.findByUserId(user1.getId());
		assertThat(findRecentlyUserList).isNotEmpty();
	}

}
