package com.dreamgames.rowmatch.controller;

import com.dreamgames.rowmatch.entity.Progress;
import com.dreamgames.rowmatch.entity.User;
import com.dreamgames.rowmatch.exceptions.NoActiveTournamentException;
import com.dreamgames.rowmatch.payloads.ProgressResponse;
import com.dreamgames.rowmatch.service.ProgressService;
import com.dreamgames.rowmatch.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ProgressController {
    private final ProgressService progressService;
    private final TournamentService tournamentService;

    @PostMapping(value = "/progress")
    public ResponseEntity<?> updateProgress() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Progress updatedProgress = progressService.updateProgress(user);
        try {
            tournamentService.updateUserActiveTournamentRanking(user);
        } catch (NoActiveTournamentException ignored) {}
        ProgressResponse response = new ProgressResponse(updatedProgress.getLevel(), updatedProgress.getCoins());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
