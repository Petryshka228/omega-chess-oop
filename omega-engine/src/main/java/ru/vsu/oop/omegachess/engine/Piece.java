package ru.vsu.oop.omegachess.engine;

public class Piece {
    public final PieceType type;
    public final Color color;

    public Piece(PieceType type, Color color) {
        this.type = type;
        this.color = color;
    }

    public char symbol() {
        char c;
        switch (type) {
            case KING: c = 'K'; break;
            case QUEEN: c = 'Q'; break;
            case ROOK: c = 'R'; break;
            case BISHOP: c = 'B'; break;
            case KNIGHT: c = 'N'; break;
            case CHAMPION: c = 'C'; break;
            case WIZARD: c = 'W'; break;
            default: c = 'P';
        }
        return color == Color.WHITE ? c : Character.toLowerCase(c);
    }
}