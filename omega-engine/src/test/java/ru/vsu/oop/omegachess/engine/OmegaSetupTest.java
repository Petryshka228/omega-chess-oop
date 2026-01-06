package ru.vsu.oop.omegachess.engine;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OmegaSetupTest {

    @Test
    void initialSetupHasWizardsAndChampions() {
        OmegaGame g = OmegaGame.newGame();

        assertEquals('W', g.board().get(Pos.from("w1")).symbol());
        assertEquals('W', g.board().get(Pos.from("w2")).symbol());
        assertEquals('w', g.board().get(Pos.from("w3")).symbol());
        assertEquals('w', g.board().get(Pos.from("w4")).symbol());

        assertEquals('C', g.board().get(Pos.from("a0")).symbol());
        assertEquals('C', g.board().get(Pos.from("j0")).symbol());
        assertEquals('c', g.board().get(Pos.from("a9")).symbol());
        assertEquals('c', g.board().get(Pos.from("j9")).symbol());
    }
}