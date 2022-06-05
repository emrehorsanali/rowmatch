package com.dreamgames.rowmatch.exceptions;

public class ProgressUnfitException extends RuntimeException {
    public ProgressUnfitException() {
        super("User progress unfit for entering a tournament!");
    }
}
