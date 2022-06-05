package com.dreamgames.rowmatch.controller;

import com.dreamgames.rowmatch.entity.Progress;
import com.dreamgames.rowmatch.payloads.AuthenticationRequest;
import com.dreamgames.rowmatch.payloads.JWTResponse;
import com.dreamgames.rowmatch.payloads.ProgressResponse;
import com.dreamgames.rowmatch.security.CustomUserDetailsService;
import com.dreamgames.rowmatch.security.JWTUtils;
import com.dreamgames.rowmatch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        final UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        JWTResponse jwt = new JWTResponse(true, jwtUtils.generateToken(userDetails));
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        Progress createdProgress = this.userService.createUser(authenticationRequest);
        ProgressResponse response = new ProgressResponse(createdProgress.getLevel(), createdProgress.getCoins());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
