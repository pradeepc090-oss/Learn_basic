package com.example;

import java.util.Random;

public class Game {

    public enum Result {
        TOO_LOW, TOO_HIGH, CORRECT
    }

    private final int secret;
    private final int max;
    private int attempts;
    private boolean solved;

    Game(int secret, int max) {
        if (max < 1 || secret < 1 || secret > max) {
            throw new IllegalArgumentException("invalid game bounds");
        }
        this.secret = secret;
        this.max = max;
    }

    public static Game create(int max, Random random) {
        return new Game(random.nextInt(max) + 1, max);
    }

    public Result guess(int value) {
        if (value < 1 || value > max) {
            throw new IllegalArgumentException("guess must be between 1 and " + max);
        }
        attempts++;
        if (value < secret) {
            return Result.TOO_LOW;
        }
        if (value > secret) {
            return Result.TOO_HIGH;
        }
        solved = true;
        return Result.CORRECT;
    }

    public int getMax() {
        return max;
    }

    public int getAttempts() {
        return attempts;
    }

    public boolean isSolved() {
        return solved;
    }
}
