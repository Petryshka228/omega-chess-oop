package ru.vsu.oop.omegachess.gui;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OmegaFrame().setVisible(true));
    }
}