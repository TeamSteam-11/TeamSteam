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

	// public void saveRecentlyUser(MatchingPartner matchingPartner){
	//
	// }
	@Transactional
	public void updateRecentlyUser(Long userId){

		User user =	userService.findByIdElseThrow(userId);

		log.info("user = {} ",user);
		//유저가 가지고있는 매칭리스트들의 아이디리스트
		List<MatchingPartner> matchingPartnerList = matchingPartnerService.findByUserId(userId);

		List<Long> matchingIdList = matchingPartnerList.stream()
			.map(m->m.getMatching().getId())
			.collect(Collectors.toList());

		log.info("matchingListIdList = {} ",matchingIdList);
		List<RecentlyUser> recentlyUserList = new ArrayList<>();

		//매칭리스트의 아이디리스트로 매칭파트너 찾기
		for (Long matchingListId : matchingIdList) {
			List<MatchingPartner> matchingPartners = matchingPartnerService.findByMatchingId(matchingListId);
			//찾은 매칭파트너 목록에서 필터링
			List<MatchingPartner> matchingPartnersNoContainsMe = matchingPartners.stream()
				.filter(u -> u.isInChatRoomTrueFalse() == true)
				.filter(t ->t.getUser().getId() != userId)//요부분
				.collect(Collectors.toList());

			log.info("matchingPartners = {} ",matchingPartners);

			for (MatchingPartner matchingPartner : matchingPartnersNoContainsMe) {

				boolean exists = existsByUserAndMatchingPartner(user, matchingPartner);
				boolean anotherMatchingExist = anotherMatchingExists(user,matchingPartner);

				if (!exists) {
					if(!anotherMatchingExist) {

						RecentlyUser recentlyUser = RecentlyUser.builder()
							.user(user)
							.matchingPartner(matchingPartner)
							.matchingPartnerName(matchingPartner.getUser().getUsername())
							.build();

						log.info("recentlyUser = {} ", recentlyUser);
						log.info("recentlyUser.getMatchingPartner() = {} ", recentlyUser.getMatchingPartner());
						log.info("recentlyUser.getUsername() = {} ", recentlyUser.getMatchingPartnerName());

						recentlyUserList.add(recentlyUser);

					}
				}
			}

		}
		recentlyUserRepository.saveAll(recentlyUserList);
	}

	private boolean anotherMatchingExists(User user, MatchingPartner matchingPartner) {

		return recentlyUserRepository.findByUserId(user.getId()).stream()
			.map(m->m.getMatchingPartnerName().equals(matchingPartner.getUser().getUsername()))
			.findFirst()
			.isPresent();
	}

	public boolean existsByUserAndMatchingPartner(User user, MatchingPartner matchingPartner) {
		return recentlyUserRepository.existsByUserAndMatchingPartner(user, matchingPartner);
	}



	// public void deleteRecentlyUser(){
	//
	// }
	//
	// public Optional<RecentlyUser> findById(){
	// 	// return recentlyUserRepository.findById();
	// 	return null;
	// }
	//
	// public RecentlyUser findByIdOrElseThrow(){
	// 	// return recentlyUserRepository.findById().orElseThrow();
	// 	return null;
	// }

	public List<RecentlyUser> findAllByUserId(Long userId) {
		return recentlyUserRepository.findAllByUserId(userId);
	}
}
