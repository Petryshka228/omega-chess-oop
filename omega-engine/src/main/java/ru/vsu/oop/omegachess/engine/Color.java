package ru.vsu.oop.omegachess.engine;

public enum Color {
    WHITE(1), BLACK(-1);

    public final int pawnDir;

    Color(int pawnDir) {
        this.pawnDir = pawnDir;
    }

    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}