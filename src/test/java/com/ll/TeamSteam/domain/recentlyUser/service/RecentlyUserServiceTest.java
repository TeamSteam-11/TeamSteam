package com.ll.TeamSteam.domain.recentlyUser.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.repository.MatchingRepository;
import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.matchingPartner.repository.MatchingPartnerRepository;
import com.ll.TeamSteam.domain.matchingPartner.service.MatchingPartnerService;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.user.entity.Gender;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.repository.UserRepository;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
class RecentlyUserServiceTest {


	@Autowired
	private MatchingPartnerService matchingPartnerService;
	@Autowired
	private MatchingPartnerRepository matchingPartnerRepository;
	@Autowired
	private MatchingRepository matchingRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RecentlyUserService recentlyUserService;

	@BeforeEach
	void beforeEach() {

		User user1 = User.builder()
			.id(9L)
			.username("user1")
			.steamId("13131")
			.type(Gender.남성)
			.temperature(37)
			.avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
			.build();

		userRepository.save(user1);

		User user2 = User.builder()
			.id(10L)
			.username("user2")
			.steamId("13132")
			.type(Gender.여성)
			.temperature(40)
			.avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
			.build();

		userRepository.save(user2);

		Matching matching1 = Matching.builder()
			.title("매칭1")
			.content("매칭1입니다")
			.id(7L)
			.user(user1)
			.genre(GenreTagType.건설)
			.gameTagId(41000)
			.gender("성별무관")
			.capacity(4L)
			.startTime(20)
			.endTime(22)
			.build();

		matchingRepository.save(matching1);

		MatchingPartner matchingPartner1 = MatchingPartner.builder()
			.matching(matching1)
			.id(1L)
			.user(user1)
			.inChatRoomTrueFalse(false)
			.build();

		matchingPartnerRepository.save(matchingPartner1);

		MatchingPartner matchingPartner2 = MatchingPartner.builder()
			.matching(matching1)
			.id(2L)
			.user(user2)
			.inChatRoomTrueFalse(false)
			.build();

		matchingPartnerRepository.save(matchingPartner2);


	}

	@Test
	@DisplayName("유저의 매칭아이디리스트 가져오기")
	void test001() {
		//given
		Long userId =9L;
		// When
		List<Long> matchingIdList = recentlyUserService.getMatchingIdList(userId);

		// Then
		assertEquals(1, matchingIdList.size());

	}
}
