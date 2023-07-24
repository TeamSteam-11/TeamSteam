package com.ll.TeamSteam.domain.user.service;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.ll.TeamSteam.domain.friend.service.FriendService;
import com.ll.TeamSteam.domain.gameTag.entity.GameTag;
import com.ll.TeamSteam.domain.gameTag.service.GameTagService;
import com.ll.TeamSteam.domain.genreTag.entity.GenreTag;
import com.ll.TeamSteam.domain.genreTag.service.GenreTagService;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary;
import com.ll.TeamSteam.domain.steam.service.SteamGameLibraryService;
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
	private UserTagService userTagService;
	@Autowired
	private GameTagService gameTagService;
	@Autowired
	private GenreTagService genreTagService;
	@Autowired
	private SteamGameLibraryService steamGameLibraryService;
	@Autowired
	private FriendService friendService;

	// 의존성 주입을 위해 목(Mock) 객체 생성
	@Autowired
	private UserService userService;

	@BeforeEach
		// 아래 메서드는 각 테스트케이스가 실행되기 전에 실행된다.
	void beforeEach() {

		// 모든 데이터 삭제
		// userRepository.deleteAll();

		// 목(Mock) 객체 초기화


	}


	@Test
	@DisplayName("유저 create()")
	void t001() throws Exception {

	}


}
