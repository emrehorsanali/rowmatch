package com.dreamgames.rowmatch.exceptions;

public class NoActiveTournamentException extends RuntimeException {
    public NoActiveTournamentException() {
        super("No active tournament found currently!");
    }
}
