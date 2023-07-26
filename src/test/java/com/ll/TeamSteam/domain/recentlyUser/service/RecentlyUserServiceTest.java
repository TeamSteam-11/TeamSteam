package com.ll.TeamSteam.domain.recentlyUser.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.aot.hint.TypeReference.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.ll.TeamSteam.domain.friend.service.FriendService;
import com.ll.TeamSteam.domain.gameTag.service.GameTagService;
import com.ll.TeamSteam.domain.genreTag.service.GenreTagService;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.repository.MatchingRepository;
import com.ll.TeamSteam.domain.matching.service.MatchingService;
import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.matchingPartner.repository.MatchingPartnerRepository;
import com.ll.TeamSteam.domain.matchingPartner.service.MatchingPartnerService;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.recentlyUser.repository.RecentlyUserRepository;
import com.ll.TeamSteam.domain.steam.service.SteamGameLibraryService;
import com.ll.TeamSteam.domain.user.entity.Gender;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import com.ll.TeamSteam.domain.user.service.UserService;
import com.ll.TeamSteam.domain.userTag.service.UserTagService;
import com.ll.TeamSteam.global.security.UserInfoResponse;

@SpringBootTest
@Transactional
class RecentlyUserServiceTest {


	@Autowired
	private MatchingPartnerService matchingPartnerService;
	@Autowired
	private RecentlyUserRepository recentlyUserRepository;
	@Autowired
	private MatchingRepository matchingRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserTagService userTagService;
	@Autowired
	private GameTagService gameTagService;
	@Autowired
	private GenreTagService genreTagService;
	@Autowired
	private SteamGameLibraryService steamGameLibraryService;
	@Autowired
	private FriendService friendService;



	@Autowired
	private RecentlyUserService recentlyUserService;
	@Autowired
	private UserService userService;





	@BeforeEach
	@Rollback(false)
	void beforeEach() {

		Matching matching1 = Matching.builder()
			.title("매칭1")
			.content("매칭1입니다")
			.id(6L)
			.genre(GenreTagType.건설)
			.gameTagId(41000)
			.gender("성별무관")
			.capacity(4L)
			.startTime(20)
			.endTime(22)
			.build();

		Matching matching2 = Matching.builder()
			.title("매칭2")
			.content("매칭2입니다")
			.id(7L)
			.genre(GenreTagType.격투)
			.gameTagId(41000)
			.gender("성별무관")
			.capacity(4L)
			.startTime(20)
			.endTime(22)
			.build();

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
			.username("user1")
			.steamId("13132")
			.type(Gender.남성)
			.temperature(37)
			.avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
			.build();

		userRepository.save(user2);

		List<MatchingPartner> matchingPartners = new ArrayList<>();

		MatchingPartner matchingPartner1 =new MatchingPartner().builder()
			.user(user2)
			.inChatRoomTrueFalse(true)
				.id(1L)
			.build();

		MatchingPartner matchingPartner2 =new MatchingPartner().builder()
			.user(user2)
			.inChatRoomTrueFalse(true)
				.id(2L)
			.build();

		matchingPartner1.setMatching(matching1);
		matchingPartner2.setMatching(matching2);

		matchingPartners.add(matchingPartner1);
		matchingPartners.add(matchingPartner2);

		matching1.getMatchingPartners().add(matchingPartner1);
		matching2.getMatchingPartners().add(matchingPartner2);


		matchingRepository.save(matching1);
		matchingRepository.save(matching2);
		matchingPartnerService.saveAll(matchingPartners);
		recentlyUserRepository.saveAll(new ArrayList<>());

	}

	@Test
	void testGetMatchingIdList() {
		// When
		List<Long> matchingIdList = recentlyUserService.getMatchingIdList(10L);

		// Then
		assertEquals(0, matchingIdList.size());

	}
}
