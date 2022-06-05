package com.dreamgames.rowmatch.controller;

import com.dreamgames.rowmatch.entity.*;
import com.dreamgames.rowmatch.exceptions.*;
import com.dreamgames.rowmatch.payloads.*;
import com.dreamgames.rowmatch.service.TournamentService;
import com.dreamgames.rowmatch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tournaments")
@RequiredArgsConstructor
public class TournamentController {
    private final TournamentService tournamentService;
    private final UserService userService;

    @PostMapping("/enter")
    public ResponseEntity<?> enter() {
        try {
            Tournament tournament = tournamentService.getLastActiveTournament();
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<LeaderboardResponse> leaderboard = tournamentService.enterTournament(tournament, user);
            return new ResponseEntity<>(leaderboard, HttpStatus.OK);
        } catch (RewardNotClaimedException | ProgressUnfitException | NoActiveTournamentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/rank")
    public ResponseEntity<?> rank(@Valid @RequestBody TournamentRankRequest tournamentRankRequest) {
        Optional<Tournament> tournament = tournamentService.getTournament(tournamentRankRequest.getTournamentId());
        Optional<User> user = userService.getUser(tournamentRankRequest.getUserId());
        if (tournament.isPresent() && user.isPresent()) {
            TournamentRankResponse tournamentRankResponse;
            try {
                tournamentRankResponse = tournamentService
                        .getUserTournamentRank(tournament.get(), user.get());
            } catch (NotInTournamentException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(tournamentRankResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/claim")
    public ResponseEntity<?> claim(@Valid @RequestBody TournamentClaimRequest tournamentClaimRequest) {
        Optional<Tournament> tournament = tournamentService.getTournament(tournamentClaimRequest.getTournamentId());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (tournament.isPresent()) {
            Progress progress;
            try {
                progress = tournamentService.claimTournamentReward(tournament.get(), user);
            } catch (AlreadyClaimedRewardException | NotInTournamentException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            ProgressResponse response = new ProgressResponse(progress.getLevel(), progress.getCoins());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/leaderboard")
    public ResponseEntity<?> leaderboard(
            @Valid @RequestBody TournamentLeaderboardRequest tournamentLeaderboardRequest) {
        Optional<Group> group = tournamentService.getGroup(tournamentLeaderboardRequest.getGroupId());
        if (group.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        List<LeaderboardResponse> leaderboard = tournamentService
                .getLeaderboard(group.get());
        return new ResponseEntity<>(leaderboard, HttpStatus.OK);
    }
}
