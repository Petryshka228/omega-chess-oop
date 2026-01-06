package ru.vsu.oop.omegachess.engine;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PawnFirstMoveTest {

    @Test
    void pawnCanMove1to3FromStart() {
        OmegaGame g = OmegaGame.newGame();
        var moves = g.legalMovesFrom(Pos.from("e1"));

        assertTrue(moves.stream().anyMatch(m -> m.to.equals(Pos.from("e2"))));
        assertTrue(moves.stream().anyMatch(m -> m.to.equals(Pos.from("e3"))));
        assertTrue(moves.stream().anyMatch(m -> m.to.equals(Pos.from("e4"))));
    }
}