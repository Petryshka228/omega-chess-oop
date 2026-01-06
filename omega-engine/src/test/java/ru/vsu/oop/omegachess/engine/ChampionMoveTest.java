package ru.vsu.oop.omegachess.engine;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChampionMoveTest {

    @Test
    void championCanJumpIntoGame() {
        OmegaGame g = OmegaGame.newGame();
        var moves = g.legalMovesFrom(Pos.from("a0"));

        assertTrue(moves.stream().anyMatch(m -> m.to.equals(Pos.from("a2"))));
        assertTrue(moves.stream().anyMatch(m -> m.to.equals(Pos.from("c2"))));
    }
}