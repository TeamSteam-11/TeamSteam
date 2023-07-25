package com.ll.TeamSteam.domain.user.service;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

import com.ll.TeamSteam.domain.friend.entity.Friend;
import com.ll.TeamSteam.domain.friend.service.FriendService;
import com.ll.TeamSteam.domain.gameTag.entity.GameTag;
import com.ll.TeamSteam.domain.gameTag.service.GameTagService;
import com.ll.TeamSteam.domain.genreTag.entity.GenreTag;
import com.ll.TeamSteam.domain.genreTag.service.GenreTagService;
import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.matchingPartner.service.MatchingPartnerService;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser;
import com.ll.TeamSteam.domain.recentlyUser.repository.RecentlyUserRepository;
import com.ll.TeamSteam.domain.recentlyUser.service.RecentlyUserService;
import com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary;
import com.ll.TeamSteam.domain.steam.service.SteamGameLibraryService;
import com.ll.TeamSteam.domain.user.entity.Gender;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import com.ll.TeamSteam.domain.userTag.entity.UserTag;
import com.ll.TeamSteam.domain.userTag.service.UserTagService;
import com.ll.TeamSteam.global.security.UserInfoResponse;

@SpringBootTest
@Transactional
public class UserServiceTest {
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

	// 의존성 주입을 위해 목(Mock) 객체 생성
	@InjectMocks
	private UserService userService;


	@BeforeEach
		// 아래 메서드는 각 테스트케이스가 실행되기 전에 실행된다.
	void beforeEach() {
		MockitoAnnotations.initMocks(this);
		// 모든 데이터 삭제
		// userRepository.deleteAll();

		// 목(Mock) 객체 초기화
		// recentlyUserService = new RecentlyUserService(userService, matchingPartnerService, recentlyUserRepository);

	}


	@Test
	@DisplayName("유저 create()")
	void t001(){
		UserInfoResponse.User user = UserInfoResponse.User.builder()
			.steamid("123456789")
			.personaname("JohnDoe")
			.profilestate(1)
			.avatarmedium("https://example.com/avatar.jpg")
			.build();

		UserInfoResponse response = UserInfoResponse.builder()
			.response(UserInfoResponse.Response.builder()
				.players(Collections.singletonList(user))
				.build())
			.build();



		userService.create(response);
		userService.findBySteamId("123456789");
		assertThat(userService.findBySteamId("123456789").isPresent());

	}

	@Test
	@DisplayName("유저 create()")
	void t002(){
		UserInfoResponse.User user = UserInfoResponse.User.builder()
			.steamid("123456789")
			.personaname("JohnDoe")
			.profilestate(1)
			.avatarmedium("https://example.com/avatar.jpg")
			.build();

		UserInfoResponse response = UserInfoResponse.builder()
			.response(UserInfoResponse.Response.builder()
				.players(Collections.singletonList(user))
				.build())
			.build();



		userService.create(response);
		userService.findBySteamId("123456789");
		assertThat(userService.findBySteamId("123456789").isPresent());

	}


}
