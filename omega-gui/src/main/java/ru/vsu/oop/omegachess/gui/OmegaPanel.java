package ru.vsu.oop.omegachess.gui;

import ru.vsu.oop.omegachess.engine.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OmegaPanel extends JPanel {
    private final Supplier<OmegaGame> game;
    private final Consumer<OmegaGame> setGame;
    private final Consumer<String> setStatus;

    private Pos selected = null;
    private List<Move> selectedMoves = List.of();

    private static final int CELL = 55;
    private static final int PAD = 20;

    public OmegaPanel(Supplier<OmegaGame> game, Consumer<OmegaGame> setGame, Consumer<String> setStatus) {
        this.game = game;
        this.setGame = setGame;
        this.setStatus = setStatus;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick(e.getX(), e.getY());
            }
        });
    }

    public void resetSelection() {
        selected = null;
        selectedMoves = List.of();
        repaint();
    }

    private void onClick(int mx, int my) {
        OmegaGame g = game.get();
        Pos p = pixelToPos(mx, my);
        if (p == null || !g.board().isValid(p)) return;

        Piece piece = g.board().get(p);

        if (selected == null) {
            if (piece == null) return;
            if (piece.color != g.sideToMove()) return;
            selected = p;
            selectedMoves = g.legalMovesFrom(p);
            setStatus.accept("Selected " + p + " " + piece.symbol());
            repaint();
            return;
        }

        // try move
        boolean ok = g.tryMove(selected.toString() + p.toString());
        if (!ok) {
            setStatus.accept("Illegal move: " + selected + " -> " + p);
        } else {
            setStatus.accept("Moved. Side: " + g.sideToMove());
        }
        selected = null;
        selectedMoves = List.of();
        repaint();
    }

    private Pos pixelToPos(int mx, int my) {
        int col = (mx - PAD) / CELL;
        int row = (my - PAD) / CELL;
        if (col < 0 || col > 11 || row < 0 || row > 11) return null;

        int x = col - 1;       // -1..10
        int y = 10 - row;      // 10..-1
        return Pos.of(x, y);
    }

    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        OmegaGame g = game.get();

        for (int row = 0; row <= 11; row++) {
            for (int col = 0; col <= 11; col++) {
                int x = col - 1;
                int y = 10 - row;
                Pos p = Pos.of(x, y);

                int px = PAD + col * CELL;
                int py = PAD + row * CELL;

                if (!g.board().isValid(p)) {
                    gr.setColor(new Color(230, 230, 230));
                    gr.fillRect(px, py, CELL, CELL);
                    continue;
                }

                boolean light = (Math.floorMod(x + y, 2) == 0);
                gr.setColor(light ? new Color(245, 245, 245) : new Color(180, 180, 180));
                gr.fillRect(px, py, CELL, CELL);

                if (isHighlighted(p)) {
                    gr.setColor(new Color(255, 220, 120));
                    gr.fillRect(px, py, CELL, CELL);
                }

                gr.setColor(Color.BLACK);
                gr.drawRect(px, py, CELL, CELL);

                Piece piece = g.board().get(p);
                if (piece != null) {
                    gr.setFont(new Font("SansSerif", Font.BOLD, 22));
                    String s = String.valueOf(piece.symbol());
                    int tx = px + CELL / 2 - 6;
                    int ty = py + CELL / 2 + 8;
                    gr.drawString(s, tx, ty);
                }
            }
        }
    }
    private boolean isHighlighted(Pos p) {
        if (selected == null) return false;
        if (p.equals(selected)) return true;
        for (Move m : selectedMoves) {
            if (m.to.equals(p)) return true;
        }
        return false;
    }
}