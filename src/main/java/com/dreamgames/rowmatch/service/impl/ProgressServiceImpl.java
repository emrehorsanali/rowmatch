package com.dreamgames.rowmatch.service.impl;

import com.dreamgames.rowmatch.entity.Progress;
import com.dreamgames.rowmatch.entity.User;
import com.dreamgames.rowmatch.exceptions.ResourceNotFoundException;
import com.dreamgames.rowmatch.repository.ProgressRepository;
import com.dreamgames.rowmatch.service.ProgressService;
import com.dreamgames.rowmatch.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {
    private final ProgressRepository progressRepository;

    @Override
    public Progress save(Progress progress) {
        return progressRepository.save(progress);
    }

    @Override
    public Progress createProgress(User user) {
        Progress progress = new Progress(user, 1, 5000);
        return progressRepository.save(progress);
    }

    @Override
    public Progress getProgressByUser(User user) {
        return this.progressRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", user.getId()));
    }

    @Override
    public Progress updateProgress(User user) {
        Progress selectedProgress = getProgressByUser(user);
        selectedProgress.setLevel(selectedProgress.getLevel() + 1);
        selectedProgress.setCoins(selectedProgress.getCoins() + 25);
        progressRepository.save(selectedProgress);
        return selectedProgress;
    }
}
