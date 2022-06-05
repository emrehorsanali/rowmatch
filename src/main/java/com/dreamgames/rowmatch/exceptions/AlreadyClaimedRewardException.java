package com.dreamgames.rowmatch.exceptions;

public class AlreadyClaimedRewardException extends RuntimeException {
    public AlreadyClaimedRewardException() {
        super("User already claimed reward!");
    }
}
