package com.dreamgames.rowmatch.repository;

import com.dreamgames.rowmatch.entity.Group;
import com.dreamgames.rowmatch.entity.Ranking;
import com.dreamgames.rowmatch.entity.Tournament;
import com.dreamgames.rowmatch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankingRepository extends JpaRepository<Ranking, Long> {
    public Optional<Ranking> findByGroup_TournamentAndUser(Tournament tournament, User user);
    public Optional<Ranking> findByUserAndDidClaimAndFinishRankBetween(User user,
                                                                       Boolean didClaim,
                                                                       Integer startRank,
                                                                       Integer endRank);
}
