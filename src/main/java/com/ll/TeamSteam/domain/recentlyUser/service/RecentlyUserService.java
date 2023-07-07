package com.ll.TeamSteam.domain.recentlyUser.service;

import static jakarta.persistence.FetchType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.service.MatchingService;
import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.matchingPartner.service.MatchingPartnerService;
import com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser;
import com.ll.TeamSteam.domain.recentlyUser.repository.RecentlyUserRepository;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;

import jakarta.persistence.ManyToOne;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecentlyUserService {

	private final UserService userService;
	private final MatchingPartnerService matchingPartnerService;
	private final RecentlyUserRepository recentlyUserRepository;

	public void saveRecentlyUser(MatchingPartner matchingPartner){

	}
	@Transactional
	public void updateRecentlyUser(Long userId){

		User user =	userService.findByIdElseThrow(userId);

	try {
		//유저가 가지고있는 매칭리스트들의 아이디리스트
		List<Long> matchingListIdList = user.getMatchingList().stream()
			.map(m -> m.getId()).collect(Collectors.toList());

		log.info("matchingListIdList = {} ",matchingListIdList);

		//매칭리스트의 아이디리스트로 매칭파트너 찾기
		for (Long matchingListId : matchingListIdList) {
			List<MatchingPartner> matchingPartnerList = matchingPartnerService.findByMatchingId(matchingListId);
			//찾은 매칭파트너 목록에서 필터링
			matchingPartnerList.stream()
				.filter(u -> u.isInChatRoomTrueFalse() == true)
				.collect(Collectors.toList());

			log.info("matchingPartnerList = {} ",matchingPartnerList);

			List<RecentlyUser> recentlyUserList = new ArrayList<>();

			for (MatchingPartner matchingPartner : matchingPartnerList) {

				RecentlyUser recentlyUser = RecentlyUser.builder()
					.user(user)
					.matchingPartner(matchingPartner)
					.username(matchingPartner.getUser().getUsername())
					.build();

				log.info("recentlyUser = {} ",recentlyUser);

				recentlyUserList.add(recentlyUser);

			}
			recentlyUserRepository.saveAll(recentlyUserList);
		}
	}
	catch(Exception e){
		e.printStackTrace();
	}
	}

	public void deleteRecentlyUser(){

	}

	public Optional<RecentlyUser> findById(){
		// return recentlyUserRepository.findById();
		return null;
	}

	public RecentlyUser findByIdOrElseThrow(){
		// return recentlyUserRepository.findById().orElseThrow();
		return null;
	}

	public List<RecentlyUser> findAllByUserId(Long userId) {
		return recentlyUserRepository.findAllByUserId(userId);
	}
}
