package com.ll.TeamSteam.domain.recentlyUser.service;

import com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner;
import com.ll.TeamSteam.domain.matchingPartner.service.MatchingPartnerService;
import com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser;
import com.ll.TeamSteam.domain.recentlyUser.repository.RecentlyUserRepository;
import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecentlyUserService {

	private final UserService userService;
	private final MatchingPartnerService matchingPartnerService;
	private final RecentlyUserRepository recentlyUserRepository;

	public List<Long> getMatchingIdList(Long userId) {

		List<MatchingPartner> matchingPartnerList = matchingPartnerService.findByUserId(userId);

		return matchingPartnerList.stream()
			.map(m -> m.getMatching().getId())
			.collect(Collectors.toList());

	}
	@Transactional
	public void updateRecentlyUser(Long userId) {

		User user = userService.findByIdElseThrow(userId);
		List<Long> matchingIdList = getMatchingIdList(userId);
		List<RecentlyUser> recentlyUserList = getRecentlyUsersToUpdate(user, matchingIdList);
		saveRecentlyUsers(recentlyUserList);
	}
	@Transactional
	public void saveRecentlyUsers(List<RecentlyUser> recentlyUserList) {

		recentlyUserRepository.saveAll(recentlyUserList);
	}

	public List<MatchingPartner> filterMatchingPartnerExceptMe(User user,List<MatchingPartner> matchingPartners){

		Long userId = user.getId();

		List<MatchingPartner> matchingPartnerList = matchingPartners.stream()
			.filter(u -> u.isInChatRoomTrueFalse() == true)
			.filter(t -> t.getUser().getId() != userId)
			.collect(Collectors.toList());

		log.info("matchingPartnerList = {}", matchingPartnerList);

		return matchingPartnerList;
	}


	public List<RecentlyUser> getRecentlyUsersToUpdate(User user, List<Long> matchingIdList) {

		List<RecentlyUser> recentlyUserList = matchingIdList.stream()
			.flatMap(matchingListId -> {
				List<MatchingPartner> matchingPartners = matchingPartnerService.findByMatchingId(matchingListId);
				return filterMatchingPartnerExceptMe(user, matchingPartners).stream();
			})
			.filter(matchingPartner -> canAddRecentlyUser(user, matchingPartner))
			.map(matchingPartner -> createRecentlyUser(user, matchingPartner))
			.collect(Collectors.toList());

		log.info("recentlyUserList = {}", recentlyUserList);

		return recentlyUserList;
	}

	public boolean canAddRecentlyUser(User user, MatchingPartner matchingPartner) {

		return !existsByUserAndMatchingPartner(user, matchingPartner) && !anotherMatchingExists(user, matchingPartner);
	}

	public RecentlyUser createRecentlyUser(User user, MatchingPartner matchingPartner) {

		return RecentlyUser.builder()
			.user(user)
			.matchingPartner(matchingPartner)
			.matchingPartnerName(matchingPartner.getUser().getUsername())
			.build();
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

	@Transactional(readOnly = true)
	public List<RecentlyUser> findAllByUserId(Long userId) {

		return recentlyUserRepository.findAllByUserId(userId);
	}
}
