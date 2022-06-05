package com.dreamgames.rowmatch.service.impl;

import com.dreamgames.rowmatch.entity.Progress;
import com.dreamgames.rowmatch.entity.User;
import com.dreamgames.rowmatch.payloads.AuthenticationRequest;
import com.dreamgames.rowmatch.repository.UserRepository;
import com.dreamgames.rowmatch.service.ProgressService;
import com.dreamgames.rowmatch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProgressService progressService;

    @Override
    public Progress createUser(AuthenticationRequest authenticationRequest) {
        User user = new User(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        user = userRepository.save(user);
        return progressService.createProgress(user);
    }

    @Override
    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }
}
