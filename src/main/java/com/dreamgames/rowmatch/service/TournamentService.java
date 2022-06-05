package com.dreamgames.rowmatch.service;

import com.dreamgames.rowmatch.entity.*;
import com.dreamgames.rowmatch.payloads.LeaderboardResponse;
import com.dreamgames.rowmatch.payloads.TournamentRankResponse;

import java.util.List;
import java.util.Optional;

public interface TournamentService {
    Optional<Tournament> getLastTournament();
    Tournament getLastActiveTournament();
    List<LeaderboardResponse> getLeaderboard(Group group);
    List<LeaderboardResponse> addToLeaderboard(Group group, User user, Integer score);
    Optional<Ranking> getUserTournamentRanking(Tournament tournament, User user);
    TournamentRankResponse getUserTournamentRank(Tournament tournament, User user);
    Optional<Ranking> getUserActiveTournamentRanking(User user);
    Long claimWaitingTournamentId(User user);
    void updateUserActiveTournamentRanking(User user);
    List<LeaderboardResponse> enterTournament(Tournament tournament, User user);
    Optional<Tournament> getTournament(Long id);
    Progress claimTournamentReward(Tournament tournament, User user);
    Optional<Group> getGroup(Long id);
    void endAndCreateTournament();
    List<Long> getGroupIds(Tournament tournament);
}
