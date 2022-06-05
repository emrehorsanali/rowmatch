package com.dreamgames.rowmatch.exceptions;

public class RewardNotClaimedException extends RuntimeException {
    public RewardNotClaimedException(Long tournamentId) {
        super(String.format("Tournament reward not claimed, id: %s", tournamentId));
    }
}
