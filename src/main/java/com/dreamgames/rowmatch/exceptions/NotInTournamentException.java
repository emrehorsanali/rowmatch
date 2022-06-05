package com.dreamgames.rowmatch.exceptions;

public class NotInTournamentException extends RuntimeException {
    public NotInTournamentException() {
        super("User not in the tournament!");
    }
}
