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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

		Set<Long> uniquePartnerUserIds = new HashSet<>();

		List<RecentlyUser> nonDuplicateRecentlyUsers = new ArrayList<>();

		for (RecentlyUser recentlyUser : recentlyUserList) {
			Long partnerUserId = recentlyUser.getPartnerUserId();
			if (!uniquePartnerUserIds.contains(partnerUserId)) {
				nonDuplicateRecentlyUsers.add(recentlyUser);
				uniquePartnerUserIds.add(partnerUserId);
			}
		}

		saveRecentlyUsers(nonDuplicateRecentlyUsers);
	}

	@Transactional
	public void saveRecentlyUsers(List<RecentlyUser> recentlyUserList) {

		log.info("recentlyUserList = {} ", recentlyUserList);
		recentlyUserRepository.saveAll(recentlyUserList);
	}

	public List<MatchingPartner> filterMatchingPartnerExceptMe(User user,List<MatchingPartner> matchingPartners){

		Long userId = user.getId();

		List<MatchingPartner> matchingPartnerList = matchingPartners.stream()
			.filter(MatchingPartner::isInChatRoomTrueFalse)
			.filter(t -> !t.getUser().getId().equals(userId))
			.collect(Collectors.toList());

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

		return recentlyUserList;
	}

	public boolean canAddRecentlyUser(User user, MatchingPartner matchingPartner) {

		return !existsByUserAndPartnerUserId(user, matchingPartner);
	}

	public RecentlyUser createRecentlyUser(User user, MatchingPartner matchingPartner) {

		return RecentlyUser.builder()
			.user(user)
			.matchingPartner(matchingPartner)
			.partnerUserId(matchingPartner.getUser().getId())
			.build();
	}

	public boolean existsByUserAndPartnerUserId(User user, MatchingPartner matchingPartner) {

		return recentlyUserRepository.existsByUserIdAndPartnerUserId(user.getId(), matchingPartner.getUser().getId());
	}

	@Transactional(readOnly = true)
	public List<RecentlyUser> findAllByUserId(Long userId) {

		return recentlyUserRepository.findAllByUserId(userId);
	}
}
