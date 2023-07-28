package com.ll.TeamSteam.domain.recentlyUser.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.ll.TeamSteam.domain.user.service.UserService;
import org.junit.jupiter.api.Assertions;
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
import com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser;
import com.ll.TeamSteam.domain.user.entity.Gender;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.repository.UserRepository;


@SpringBootTest
//@Transactional
@ActiveProfiles("test")
class RecentlyUserServiceTest {

	@Autowired
	private MatchingPartnerRepository matchingPartnerRepository;
	@Autowired
	private MatchingRepository matchingRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RecentlyUserService recentlyUserService;
	@Autowired
	private MatchingPartnerService matchingPartnerService;
	@Autowired
	private UserService userService;

	@BeforeEach
	void beforeEach() {

		userRepository.deleteAll();
		matchingRepository.deleteAll();
		matchingPartnerRepository.deleteAll();

		User user1 = User.builder()
			.username("user1")
			.steamId("13131")
			.type(Gender.남성)
			.temperature(37)
			.avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
			.build();

		userService.userSave(user1);

		User user2 = User.builder()
			.username("user2")
			.steamId("13132")
			.type(Gender.여성)
			.temperature(40)
			.avatar("https://avatars.steamstatic.com/f80eb3343279cedd2534ae543c8386bfb1ca0223_medium.jpg")
			.build();

		userService.userSave(user2);

		Matching matching1 = Matching.builder()
			.title("매칭1")
			.content("매칭1입니다")
//			.id(7L)
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
//			.id(1L)
			.user(user1)
			.inChatRoomTrueFalse(false)
			.build();

		matchingPartnerRepository.save(matchingPartner1);

		MatchingPartner matchingPartner2 = MatchingPartner.builder()
			.matching(matching1)
//			.id(2L)
			.user(user2)
			.inChatRoomTrueFalse(false)
			.build();

		matchingPartnerRepository.save(matchingPartner2);


	}

	@Test
	@DisplayName("유저의 매칭아이디리스트 가져오기")
	void test001() {
		//given
		User user1 = userService.findBySteamId("13131").orElseThrow();
		Long userId = user1.getId();
		// When
		List<Long> matchingIdList = recentlyUserService.getMatchingIdList(userId);

		// Then
		assertEquals(1, matchingIdList.size());

	}
	@Test
	@DisplayName("매칭파트너 중 나를 제외하는 필터링")
	void test002(){
		//given
		Optional<User> user = userRepository.findBySteamId("13131");
		Optional<User> user2 = userRepository.findBySteamId("13132");

		Long userId = user.orElseThrow().getId();
		Long userTwoId = user2.orElseThrow().getId();

		Long matchingId = matchingRepository.findByTitle("매칭1").orElseThrow().getId();



		MatchingPartner matchingPartner2 =matchingPartnerRepository.findByMatchingIdAndUserId(matchingId,userTwoId).orElseThrow();
		matchingPartner2.updateInChatRoomTrueFalse(true);
		matchingPartnerRepository.save(matchingPartner2);

		List<MatchingPartner> matchingPartners = matchingPartnerService.findByMatchingId(matchingId);
		//When
		List<MatchingPartner> filteredMatchingPartners =recentlyUserService.filterMatchingPartnerExceptMe(user.orElseThrow(),matchingPartners);

		//Then
		assertEquals(2, matchingPartners.size());
		assertEquals(1, filteredMatchingPartners.size());
	}


	// 두번째 케이스까지 확인 완료
	//3,4는 현재 왜 통과되는지 모르겠음...

	@Test
	@DisplayName("최근 매칭된 유저에 추가할 수 있는지 검증")
	void test003(){
		//given
		Optional<User> user = userRepository.findBySteamId("13131");
		Long matchingId = matchingRepository.findByTitle("매칭1").orElseThrow().getId();
		List<MatchingPartner> matchingPartners = matchingPartnerService.findByMatchingId(matchingId);

		for(MatchingPartner matchingPartner : matchingPartners){
			boolean canAdd = recentlyUserService.canAddRecentlyUser(user.orElseThrow(),matchingPartner);

			assertTrue(canAdd);
		}
	}

	@Test
	@DisplayName("최근 매칭된 유저 생성")
	void test004(){
		//given
		Optional<User> user = userRepository.findById(9L);
		List<MatchingPartner> matchingPartners = matchingPartnerService.findByMatchingId(7L);

		int expectedRecentlyUsersCount = matchingPartners.size();
		int actualRecentlyUsersCount = 0;

		//when
		for(MatchingPartner matchingPartner : matchingPartners){
			recentlyUserService.createRecentlyUser(user.orElseThrow(),matchingPartner);

			actualRecentlyUsersCount++;
		}
		//then
		assertEquals(expectedRecentlyUsersCount,actualRecentlyUsersCount);
	}

	@Test
	@DisplayName("최근 매칭된 유저목록 가져오기")
	void test005(){
		//given
		Optional<User> user = userRepository.findById(9L);
		List<Long> matchingIdList = recentlyUserService.getMatchingIdList(9L);

		MatchingPartner matchingPartner1 =matchingPartnerRepository.findById(1L).orElseThrow();
		MatchingPartner matchingPartner2 =matchingPartnerRepository.findById(2L).orElseThrow();
		matchingPartner1.updateInChatRoomTrueFalse(true);
		matchingPartner2.updateInChatRoomTrueFalse(true);

		//when
		List<RecentlyUser> recentlyUserList =recentlyUserService.getRecentlyUsersToUpdate(user.orElseThrow(),matchingIdList);

		//Then
		//왜 빈 배열 리턴?
		assertEquals(1,recentlyUserList.size());
	}
}
