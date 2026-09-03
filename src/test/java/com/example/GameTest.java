package com.example;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {

    @Test
    void reportsTooLowAndTooHigh() {
        Game game = new Game(42, 100);
        assertEquals(Game.Result.TOO_LOW, game.guess(10));
        assertEquals(Game.Result.TOO_HIGH, game.guess(90));
        assertFalse(game.isSolved());
        assertEquals(2, game.getAttempts());
    }

    @Test
    void reportsCorrectAndMarksSolved() {
        Game game = new Game(7, 100);
        assertEquals(Game.Result.CORRECT, game.guess(7));
        assertTrue(game.isSolved());
        assertEquals(1, game.getAttempts());
    }

    @Test
    void rejectsGuessesOutsideRange() {
        Game game = new Game(5, 10);
        assertThrows(IllegalArgumentException.class, () -> game.guess(0));
        assertThrows(IllegalArgumentException.class, () -> game.guess(11));
        assertEquals(0, game.getAttempts());
    }

    @Test
    void createdSecretStaysWithinBounds() {
        Random random = new Random(1234L);
        for (int i = 0; i < 200; i++) {
            Game game = Game.create(50, random);
            assertEquals(50, game.getMax());
            boolean found = false;
            for (int n = 1; n <= 50 && !found; n++) {
                found = game.guess(n) == Game.Result.CORRECT;
            }
            assertTrue(found, "secret must be within 1..50");
        }
    }
}
