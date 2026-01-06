package ru.vsu.oop.omegachess.engine;

import java.util.Objects;

/**
 * Board position: "a0".."j9" or wizard squares "w1".."w4".
 */
public class Pos {
    public final int x;
    public final int y;

    private Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Pos of(int x, int y) {
        return new Pos(x, y);
    }

    public static Pos from(String s) {
        String t = s.trim().toLowerCase();
        if (t.length() != 2) throw new IllegalArgumentException("Bad square: " + s);

        if (t.charAt(0) == 'w') {
            int n = t.charAt(1) - '0';
            if (n == 1) return of(-1, -1);
            if (n == 2) return of(10, -1);
            if (n == 3) return of(10, 10);
            if (n == 4) return of(-1, 10);
            throw new IllegalArgumentException("Bad wizard square: " + s);
        }

        int x = t.charAt(0) - 'a';
        int y = t.charAt(1) - '0';
        if (x < 0 || x > 9 || y < 0 || y > 9) throw new IllegalArgumentException("Bad square: " + s);
        return of(x, y);
    }

    public int parity() {
        return Math.floorMod(x + y, 2);
    }

    @Override
    public String toString() {
        if (x == -1 && y == -1) return "w1";
        if (x == 10 && y == -1) return "w2";
        if (x == 10 && y == 10) return "w3";
        if (x == -1 && y == 10) return "w4";
        return "" + (char) ('a' + x) + y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pos)) return false;
        Pos p = (Pos) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}