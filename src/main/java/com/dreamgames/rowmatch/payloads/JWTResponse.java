package com.dreamgames.rowmatch.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JWTResponse {
    private Boolean authenticationSuccess;
    private String jwt;
}

