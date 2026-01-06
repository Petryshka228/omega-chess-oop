package ru.vsu.oop.omegachess.gui;

import ru.vsu.oop.omegachess.engine.OmegaGame;

import javax.swing.*;
import java.awt.*;

public class OmegaFrame extends JFrame {
    private final JLabel status = new JLabel(" ");
    private OmegaGame game = OmegaGame.newGame();

    public OmegaFrame() {
        super("Omega Chess");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(760, 820);
        setLocationRelativeTo(null);

        OmegaPanel panel = new OmegaPanel(() -> game, this::setGame, this::setStatus);

        JButton restart = new JButton("Restart");
        restart.addActionListener(e -> {
            setGame(OmegaGame.newGame());
            panel.resetSelection();
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(restart);

        add(top, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        setStatus("Click piece, then destination.");
    }

    private void setGame(OmegaGame g) {
        this.game = g;
        repaint();
    }

    private void setStatus(String s) {
        status.setText(s);
    }
}