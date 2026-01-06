package ru.vsu.oop.omegachess.engine;

public class Move {
    public final Pos from;
    public final Pos to;
    public final MoveKind kind;

    public final Pos extraFrom; // rookFrom for castling, capturedPawnPos for en passant
    public final Pos extraTo;   // rookTo for castling

    private Move(Pos from, Pos to, MoveKind kind, Pos extraFrom, Pos extraTo) {
        this.from = from;
        this.to = to;
        this.kind = kind;
        this.extraFrom = extraFrom;
        this.extraTo = extraTo;
    }

    public static Move normal(Pos from, Pos to) {
        return new Move(from, to, MoveKind.NORMAL, null, null);
    }

    public static Move promotion(Pos from, Pos to) {
        return new Move(from, to, MoveKind.PROMOTION, null, null);
    }

    public static Move enPassant(Pos from, Pos to, Pos capturedPawnPos) {
        return new Move(from, to, MoveKind.EN_PASSANT, capturedPawnPos, null);
    }

    public static Move castling(Pos kingFrom, Pos kingTo, Pos rookFrom, Pos rookTo) {
        return new Move(kingFrom, kingTo, MoveKind.CASTLING, rookFrom, rookTo);
    }

    @Override
    public String toString() {
        return from + "->" + to + " (" + kind + ")";
    }
}