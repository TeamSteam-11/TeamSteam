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
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserService userService;


	@BeforeEach
		// 아래 메서드는 각 테스트케이스가 실행되기 전에 실행된다.
	void beforeEach() {
		UserInfoResponse.User user = UserInfoResponse.User.builder()
				.steamid("11331")
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

		UserInfoResponse.User user2 = UserInfoResponse.User.builder()
				.steamid("11332")
				.personaname("JohnDoe")
				.profilestate(1)
				.avatarmedium("https://example.com/avatar.jpg")
				.build();

		UserInfoResponse response2 = UserInfoResponse.builder()
				.response(UserInfoResponse.Response.builder()
						.players(Collections.singletonList(user2))
						.build())
				.build();

		userService.create(response2);
	}


	@Test
	@DisplayName("유저 create()")
	void t001(){

		userService.findBySteamId("11331");
		assertThat(userService.findBySteamId("11331").isPresent());
	}

	@Test
	@DisplayName("유저 updateUserData()")
	void t002(){
		//given
		User updateUser = userService.findBySteamId("11331").orElseThrow();
		List<GenreTagType> genreTagTypes = new ArrayList<>();
		genreTagTypes.add(GenreTagType.건설);
		//when
		userService.updateUserData("남성", genreTagTypes, updateUser.getId());


		//then
		assertThat(updateUser.getType()).isEqualTo(Gender.남성);

	}



	@Test
	@DisplayName("유저 updateGameList")
	void t003() {

	}

	@Test
	@DisplayName("유저 updateTemperature")
	void t004(){
		//given
		User updateUser = userService.findBySteamId("11331").orElseThrow();
		int tempTemperature = updateUser.getTemperature();

		userService.updateTemperature(updateUser.getId(), 1);
		userService.updateTemperature(updateUser.getId(), 1);
		userService.updateTemperature(updateUser.getId(), 0);


		//then
		assertThat(updateUser.getTemperature()).isEqualTo(tempTemperature+1);

	}
	@Test
	@DisplayName("유저 addFriends")
	void t005() {

	}

	@Test
	@DisplayName("유저 deleteFriend")
	void t006() {

	}

	@Test
	@DisplayName("유저 isFriend")
	void t007() {

	}

	@Test
	@DisplayName("유저 getFriends")
	void t008() {

	}


}
