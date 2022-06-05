package com.dreamgames.rowmatch.service;

import com.dreamgames.rowmatch.entity.Progress;
import com.dreamgames.rowmatch.entity.User;
import com.dreamgames.rowmatch.payloads.AuthenticationRequest;

import java.util.Optional;

public interface UserService {
    Progress createUser(AuthenticationRequest authenticationRequest);
    Optional<User> getUser(Long id);
}
