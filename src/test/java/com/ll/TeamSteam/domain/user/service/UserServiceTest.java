package com.ll.TeamSteam.domain.user.service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

import com.ll.TeamSteam.domain.friend.service.FriendService;
import com.ll.TeamSteam.domain.gameTag.entity.GameTag;
import com.ll.TeamSteam.domain.gameTag.service.GameTagService;
import com.ll.TeamSteam.domain.genreTag.entity.GenreTag;
import com.ll.TeamSteam.domain.genreTag.service.GenreTagService;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary;
import com.ll.TeamSteam.domain.user.entity.Gender;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.repository.UserRepository;
import com.ll.TeamSteam.domain.userTag.entity.UserTag;
import com.ll.TeamSteam.domain.userTag.service.UserTagService;
import com.ll.TeamSteam.global.security.UserInfoResponse;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserServiceTest {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserService userService;

	@Autowired
	private UserTagService userTagService;

	@Autowired
	private GameTagService gameTagService;

	@Autowired
	private GenreTagService genreTagService;

	@Autowired
	private FriendService friendService;

	@BeforeEach
		// 아래 메서드는 각 테스트케이스가 실행되기 전에 실행된다.
	void beforeEach() {

		// UserInfoResponse.User user1 = new UserInfoResponse.User();
		// user1.setAvatarmedium("https://example.com/avatar.jpg");
		// user1.setProfilestate(1);
		// user1.setPersonaname("JohnDoe");
		// user1.setSteamid("11331");
		//
		// UserInfoResponse.Response response1 = new UserInfoResponse.Response();
		// response1.setPlayers(Collections.singletonList(user1));

		UserInfoResponse.User user = new UserInfoResponse.User();
		user.setAvatarmedium("https://example.com/avatar.jpg");
		user.setProfilestate(1);
		user.setPersonaname("JohnDoe");
		user.setSteamid("11331");

		UserInfoResponse.Response response = new UserInfoResponse.Response();
		response.setPlayers(Collections.singletonList(user));

		UserInfoResponse userInfoResponse = new UserInfoResponse();
		userInfoResponse.setResponse(response);

		userService.create(userInfoResponse);

		UserInfoResponse.User user2 = new UserInfoResponse.User();
		user2.setAvatarmedium("https://example.com/avatar.jpg");
		user2.setProfilestate(1);
		user2.setPersonaname("JohnDoe");
		user2.setSteamid("11332");

		UserInfoResponse.Response response2 = new UserInfoResponse.Response();
		response2.setPlayers(Collections.singletonList(user2));

		UserInfoResponse userInfoResponse2 = new UserInfoResponse();
		userInfoResponse2.setResponse(response2);

		userService.create(userInfoResponse2);

		UserInfoResponse.User user3 = new UserInfoResponse.User();
		user3.setAvatarmedium("https://example.com/avatar.jpg");
		user3.setProfilestate(1);
		user3.setPersonaname("JohnDoe");
		user3.setSteamid("11333");

		UserInfoResponse.Response response3 = new UserInfoResponse.Response();
		response3.setPlayers(Collections.singletonList(user3));

		UserInfoResponse userInfoResponse3 = new UserInfoResponse();
		userInfoResponse3.setResponse(response3);

		userService.create(userInfoResponse3);

		User user2Data = userService.findBySteamId("11332").orElseThrow();

		UserTag user2Tag = new UserTag();
		ArrayList<GameTag> gameTags = new ArrayList<>();
		ArrayList<GenreTag> genreTag = new ArrayList<>();
		user2Tag.setGenreTag(genreTag);
		user2Tag.setGameTag(gameTags);

		user2Data.setUserTag(user2Tag);

		genreTagService.saveAll(user2Tag.getGenreTag());
		gameTagService.saveAll(user2Tag.getGameTag());

		userTagService.save(user2Tag);

		userService.userSave(user2Data);

		userService.addFriends(userService.findBySteamId("11332").get().getId(),
			userService.findBySteamId("11333").get().getId());
		//유저2와 3은 친구

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
	@DisplayName("유저 updateGameList()")
	void t003() {
		List<SteamGameLibrary> gameLibraries = new ArrayList<>();
		String steamId = "11332";

		SteamGameLibrary game1 = new SteamGameLibrary(41000, "game1");
		gameLibraries.add(game1);
		userService.updateGameList(gameLibraries, steamId);

	}

	@Test
	@DisplayName("유저 updateTemperature()")
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
	@DisplayName("유저 addFriends()")
	void t005() {
		userService.addFriends(userService.findBySteamId("11331").get().getId(),
			userService.findBySteamId("11332").get().getId());

		assertThat(userService.isFriend(userService.findBySteamId("11331").get().getId(),
			userService.findBySteamId("11332").get().getId())).isTrue();
	}

	@Test
	@DisplayName("유저 deleteFriend()")
	void t006() {//이미 친추되어있는 2와 3을 지우기
		userService.deleteFriend(userService.findBySteamId("11332").get().getId(),
			userService.findBySteamId("11333").get().getId());

		assertThat(userService.isFriend(userService.findBySteamId("11331").get().getId(),
			userService.findBySteamId("11332").get().getId())).isFalse();
	}

	@Test
	@DisplayName("유저 isFriend()")
	void t007() {
		assertThat(userService.isFriend(userService.findBySteamId("11332").get().getId(),
			userService.findBySteamId("11333").get().getId())).isTrue();
	}

	@Test
	@DisplayName("유저 getFriends()")
	void t008() {
		assertThat(userService.getFriends(userService.findBySteamId("11332").get().getId())).isNotEmpty();
	}


}
