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
import org.springframework.boot.test.context.SpringBootTest;
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

	// @Mock
	// private UserService userService;
	@Mock
	private MatchingPartnerRepository matchingPartnerRepository;
	@Mock
	private RecentlyUserRepository recentlyUserRepository;
	@Mock
	private MatchingRepository matchingRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private UserTagService userTagService;
	@Mock
	private GameTagService gameTagService;
	@Mock
	private GenreTagService genreTagService;
	@Mock
	private SteamGameLibraryService steamGameLibraryService;
	@Mock
	private FriendService friendService;



	@InjectMocks
	private RecentlyUserService recentlyUserService;
	@InjectMocks
	private UserService userService;





	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.initMocks(this);

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

		matchingRepository.save(matching1);

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

		matchingRepository.save(matching2);

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

		UserInfoResponse.User user3 = UserInfoResponse.User.builder()
			.steamid("13133")
			.personaname("JohnDoe")
			.profilestate(1)
			.avatarmedium("https://example.com/avatar.jpg")
			.build();

		UserInfoResponse response1 = UserInfoResponse.builder()
			.response(UserInfoResponse.Response.builder()
				.players(Collections.singletonList(user3))
				.build())
			.build();
		userService.create(response1);

		UserInfoResponse.User user4 = UserInfoResponse.User.builder()
			.steamid("13134")
			.personaname("JohnDoe")
			.profilestate(1)
			.avatarmedium("https://example.com/avatar.jpg")
			.build();

		UserInfoResponse response2 = UserInfoResponse.builder()
			.response(UserInfoResponse.Response.builder()
				.players(Collections.singletonList(user4))
				.build())
			.build();
		userService.create(response2);

		List<MatchingPartner> matchingPartners = new ArrayList<>();

		matchingPartners.add(new MatchingPartner().builder()
			.matching(matching1)
			.user(userRepository.findBySteamId("13134").orElseThrow())
			.inChatRoomTrueFalse(true)
			.build()
		);
		matchingPartners.add(new MatchingPartner().builder()
			.matching(matching2)
			.user(userRepository.findBySteamId("13134").orElseThrow())
			.inChatRoomTrueFalse(true)
			.build()
		);
		matchingPartnerRepository.saveAll(matchingPartners);

	}

	@Test
	void testGetMatchingIdList() {
		// When

		List<Long> matchingIdList = recentlyUserService.getMatchingIdList(userRepository.findBySteamId("13134").orElseThrow().getId());

		// Then
		assertEquals(2, matchingIdList.size());
	}
}
