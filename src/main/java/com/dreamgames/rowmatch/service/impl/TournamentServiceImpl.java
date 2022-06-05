package com.dreamgames.rowmatch.service.impl;

import com.dreamgames.rowmatch.entity.*;
import com.dreamgames.rowmatch.exceptions.*;
import com.dreamgames.rowmatch.payloads.LeaderboardResponse;
import com.dreamgames.rowmatch.payloads.TournamentRankResponse;
import com.dreamgames.rowmatch.payloads.UserResponse;
import com.dreamgames.rowmatch.repository.GroupRepository;
import com.dreamgames.rowmatch.repository.RankingRepository;
import com.dreamgames.rowmatch.repository.TournamentRepository;
import com.dreamgames.rowmatch.service.ProgressService;
import com.dreamgames.rowmatch.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {
    private final TournamentRepository tournamentRepository;
    private final GroupRepository groupRepository;
    private final RankingRepository rankingRepository;
    private final ProgressService progressService;

    @Autowired
    private final RedisTemplate<String, String> redisTemplate;


    @Resource(name = "redisTemplate")
    private ZSetOperations<String, UserResponse> zSetOperations;

    @Override
    public Optional<Tournament> getLastTournament() {
        return tournamentRepository.findFirstByOrderByIdDesc();
    }

    @Override
    public Tournament getLastActiveTournament() {
        Optional<Tournament> lastTournament = getLastTournament();
        if (lastTournament.isPresent()) {
            Tournament tournament = lastTournament.get();
            LocalDateTime now = LocalDateTime.now();
            if (tournament.getStartDate().isBefore(now) && tournament.getEndDate().isAfter(now)) {
                return tournament;
            }
        }
        throw new NoActiveTournamentException();
    }

    @Override
    public List<LeaderboardResponse> getLeaderboard(Group group) {
        return Objects.requireNonNull(zSetOperations.reverseRangeWithScores(group.getId().toString(), 0, 20))
                .stream().map(e -> new LeaderboardResponse(
                        Objects.requireNonNull(e.getValue()),
                        group.getId(),
                        group.getTournament().getId(),
                        Objects.requireNonNull(e.getScore()))
                ).collect(Collectors.toList());
    }

    @Override
    public List<LeaderboardResponse> addToLeaderboard(Group group, User user, Integer score) {
        UserResponse userResponse = new UserResponse(user.getId(), user.getUsername());
        zSetOperations.add(group.getId().toString(), userResponse, score);
        return getLeaderboard(group);
    }

    @Override
    public Optional<Ranking> getUserTournamentRanking(Tournament tournament, User user) {
        return rankingRepository.findByGroup_TournamentAndUser(tournament, user);
    }

    @Override
    public TournamentRankResponse getUserTournamentRank(Tournament tournament, User user) {
        Optional<Ranking> ranking = getUserTournamentRanking(tournament, user);
        if (ranking.isEmpty())
            throw new NotInTournamentException();
        Long groupId = ranking.get().getGroup().getId();
        Long rank = zSetOperations.reverseRank(groupId.toString(), new UserResponse(user.getId(), user.getUsername()));
        return new TournamentRankResponse(groupId, rank);
    }

    @Override
    public Optional<Ranking> getUserActiveTournamentRanking(User user) {
        Tournament tournament = getLastActiveTournament();
        return rankingRepository.findByGroup_TournamentAndUser(tournament, user);
    }

    @Override
    public Long claimWaitingTournamentId(User user) {
        Optional<Ranking> ranking = rankingRepository
                .findByUserAndDidClaimAndFinishRankBetween(user, false, 1, 10);
        if (ranking.isEmpty()) {
            return -1L;
        }
        return ranking.get().getGroup().getTournament().getId();
    }

    @Override
    public void updateUserActiveTournamentRanking(User user) {
        Optional<Ranking> rankingOptional = getUserActiveTournamentRanking(user);
        if (rankingOptional.isEmpty())
            return;
        Ranking ranking = rankingOptional.get();
        addToLeaderboard(ranking.getGroup(), user, ranking.getScore() + 1);
        ranking.setScore(ranking.getScore() + 1);
        rankingRepository.save(ranking);
    }

    @Override
    public List<LeaderboardResponse> enterTournament(Tournament tournament, User user) {
        Progress progress = progressService.getProgressByUser(user);
        Long claimWaitingTournamentId = claimWaitingTournamentId(user);
        if (!claimWaitingTournamentId.equals(-1L))
            throw new RewardNotClaimedException(claimWaitingTournamentId);
        if (progress.getLevel() >= 20 && progress.getCoins() >= 1000) {
            Integer maxLevel = ((progress.getLevel() + 99) / 100) * 100;
            Optional<Group> findGroup = groupRepository.
                    findByTournamentAndIsFullAndMaxLevel(tournament, false, maxLevel);
            Group group;
            if (findGroup.isEmpty()) {
                group = new Group(maxLevel, tournament);
                group = groupRepository.save(group);
            } else {
                group = findGroup.get();
            }
            progress.setCoins(progress.getCoins() - 1000);
            progressService.save(progress);

            Ranking ranking = new Ranking(user, group, 0, false);
            rankingRepository.save(ranking);

            Integer groupSize = group.getSize();
            if (groupSize == 19) {
                group.setIsFull(true);
            }
            group.setSize(groupSize + 1);
            groupRepository.save(group);

            return addToLeaderboard(group, user, 0);
        }
        throw new ProgressUnfitException();
    }

    @Override
    public Optional<Tournament> getTournament(Long id) {
        return tournamentRepository.findById(id);
    }

    @Override
    public Progress claimTournamentReward(Tournament tournament, User user) {
        Ranking ranking = getUserTournamentRanking(tournament, user).orElseThrow(NotInTournamentException::new);
        if (ranking.getDidClaim().equals(true)) {
            throw new AlreadyClaimedRewardException();
        }
        Integer rank = ranking.getFinishRank();
        Integer reward = 0;
        if (rank == 1) {
            reward = 10000;
        } else if (rank == 2) {
            reward = 5000;
        } else if (rank == 3) {
            reward = 3000;
        } else if ((4 <= rank) && (rank <= 10)) {
            reward = 1000;
        }

        ranking.setDidClaim(true);
        rankingRepository.save(ranking);

        Progress progress = progressService.getProgressByUser(user);
        progress.setCoins(progress.getCoins() + reward);
        progressService.save(progress);
        return progress;
    }

    @Override
    public Optional<Group> getGroup(Long id) {
        return groupRepository.findById(id);
    }

    @Override
    public void endAndCreateTournament() {
        Optional<Tournament> lastTournament = getLastTournament();
        if (lastTournament.isPresent()) {
            if (lastTournament.get().getEndDate().isAfter(LocalDateTime.now()))
                return;
            Tournament tournament = lastTournament.get();
            List<Long> groupsIds = getGroupIds(tournament);
            for (Long groupId : groupsIds) {
                Group group = new Group();
                group.setId(groupId);
                List<LeaderboardResponse> leaderboardResponses = getLeaderboard(group);
                int rank = 1;
                for (LeaderboardResponse leaderboardResponse : leaderboardResponses) {
                    User user = new User();
                    user.setId(leaderboardResponse.getUserId());
                    Ranking ranking = getUserTournamentRanking(tournament, user)
                            .orElseThrow(NotInTournamentException::new);
                    ranking.setFinishRank(rank);
                    rankingRepository.save(ranking);
                    rank++;
                }
            }
            redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("*")));
            Tournament newTournament = new Tournament();
            newTournament.setStartDate(tournament.getEndDate().plusHours(4));
            newTournament.setEndDate(tournament.getEndDate().plusHours(24));
            tournamentRepository.save(newTournament);
            return;
        }
        Tournament newTournament = new Tournament();
        LocalDateTime now = LocalDateTime.now();
        newTournament.setStartDate(now);
        newTournament.setEndDate(now.plusHours(20));
        tournamentRepository.save(newTournament);
    }

    @Override
    public List<Long> getGroupIds(Tournament tournament) {
        Set<String> redisKeys = redisTemplate.keys("*");
        assert redisKeys != null;
        List<Long> keysList = new ArrayList<>();
        for (String data : redisKeys) {
            keysList.add(Long.parseLong(data));
        }
        return keysList;
    }
}
