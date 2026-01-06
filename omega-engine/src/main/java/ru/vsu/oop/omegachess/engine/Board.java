package ru.vsu.oop.omegachess.engine;

public class Board {
    private final Piece[][] a = new Piece[12][12];

    private static int ix(int x) { return x + 1; }
    private static int iy(int y) { return y + 1; }

    public boolean isValid(Pos p) {
        int x = p.x, y = p.y;
        if (x >= 0 && x <= 9 && y >= 0 && y <= 9) return true;
        return (x == -1 && y == -1) || (x == 10 && y == -1) || (x == 10 && y == 10) || (x == -1 && y == 10);
    }

    public Piece get(Pos p) {
        if (!isValid(p)) return null;
        return a[ix(p.x)][iy(p.y)];
    }

    public void set(Pos p, Piece piece) {
        if (!isValid(p)) throw new IllegalArgumentException("Invalid square: " + p);
        a[ix(p.x)][iy(p.y)] = piece;
    }

    public void move(Pos from, Pos to) {
        Piece p = get(from);
        set(to, p);
        set(from, null);
    }

    public Board copy() {
        Board b = new Board();
        for (int x = -1; x <= 10; x++) {
            for (int y = -1; y <= 10; y++) {
                Pos p = Pos.of(x, y);
                if (!isValid(p)) continue;
                Piece piece = get(p);
                if (piece != null) b.set(p, new Piece(piece.type, piece.color));
            }
        }
        return b;
    }
}