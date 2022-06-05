package com.dreamgames.rowmatch.service;

import com.dreamgames.rowmatch.entity.Progress;
import com.dreamgames.rowmatch.entity.User;

public interface ProgressService {
    Progress save(Progress progress);
    Progress createProgress(User user);
    Progress getProgressByUser(User user);
    Progress updateProgress(User user);
}
