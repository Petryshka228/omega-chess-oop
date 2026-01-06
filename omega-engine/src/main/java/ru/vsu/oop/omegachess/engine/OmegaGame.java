package ru.vsu.oop.omegachess.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Omega Chess engine: position, legal moves, and move execution.
 */
public class OmegaGame {
    private Board board = new Board();
    private Color sideToMove = Color.WHITE;

    private boolean wCastleK = true, wCastleQ = true;
    private boolean bCastleK = true, bCastleQ = true;

    private Pos enPassantPawn = null;
    private List<Pos> enPassantTargets = Collections.emptyList();

    public static OmegaGame newGame() {
        OmegaGame g = new OmegaGame();
        g.setup();
        return g;
    }

    public Board board() {
        return board;
    }

    public Color sideToMove() {
        return sideToMove;
    }

    public boolean tryMove(String fromTo) {
        Move m = findLegalMove(fromTo);
        if (m == null) return false;
        applyMove(m);
        return true;
    }

    public List<Move> legalMovesFrom(Pos from) {
        Piece p = board.get(from);
        if (p == null || p.color != sideToMove) return Collections.emptyList();

        List<Move> pseudo = pseudoMovesFrom(from);
        List<Move> legal = new ArrayList<>();
        for (Move m : pseudo) {
            OmegaGame copy = copy();
            copy.applyMoveNoTurnSwitch(m);
            if (!copy.isInCheck(sideToMove)) legal.add(m);
        }
        return legal;
    }

    public String toAscii() {
        StringBuilder sb = new StringBuilder();
        sb.append("w4 ").append(ch(Pos.from("w4"))).append("                ").append(ch(Pos.from("w3"))).append(" w3\n");
        for (int y = 9; y >= 0; y--) {
            sb.append(y).append("  ");
            for (int x = 0; x <= 9; x++) {
                sb.append(ch(Pos.of(x, y))).append(' ');
            }
            sb.append('\n');
        }
        sb.append("w1 ").append(ch(Pos.from("w1"))).append("                ").append(ch(Pos.from("w2"))).append(" w2\n");
        sb.append("   a b c d e f g h i j\n");
        return sb.toString();
    }

    private char ch(Pos p) {
        Piece piece = board.get(p);
        return piece == null ? '.' : piece.symbol();
    }

    private Move findLegalMove(String s) {
        String t = s.replace(" ", "").toLowerCase();
        if (t.length() != 4) return null;
        Pos from = Pos.from(t.substring(0, 2));
        Pos to = Pos.from(t.substring(2, 4));
        for (Move m : legalMovesFrom(from)) {
            if (m.to.equals(to)) return m;
        }
        return null;
    }

    private void setup() {
        // Wizards
        put("w1", PieceType.WIZARD, Color.WHITE);
        put("w2", PieceType.WIZARD, Color.WHITE);
        put("w3", PieceType.WIZARD, Color.BLACK);
        put("w4", PieceType.WIZARD, Color.BLACK);

        // Champions
        put("a0", PieceType.CHAMPION, Color.WHITE);
        put("j0", PieceType.CHAMPION, Color.WHITE);
        put("a9", PieceType.CHAMPION, Color.BLACK);
        put("j9", PieceType.CHAMPION, Color.BLACK);

        // Back ranks
        put("b0", PieceType.ROOK, Color.WHITE);
        put("c0", PieceType.KNIGHT, Color.WHITE);
        put("d0", PieceType.BISHOP, Color.WHITE);
        put("e0", PieceType.QUEEN, Color.WHITE);
        put("f0", PieceType.KING, Color.WHITE);
        put("g0", PieceType.BISHOP, Color.WHITE);
        put("h0", PieceType.KNIGHT, Color.WHITE);
        put("i0", PieceType.ROOK, Color.WHITE);

        put("b9", PieceType.ROOK, Color.BLACK);
        put("c9", PieceType.KNIGHT, Color.BLACK);
        put("d9", PieceType.BISHOP, Color.BLACK);
        put("e9", PieceType.QUEEN, Color.BLACK);
        put("f9", PieceType.KING, Color.BLACK);
        put("g9", PieceType.BISHOP, Color.BLACK);
        put("h9", PieceType.KNIGHT, Color.BLACK);
        put("i9", PieceType.ROOK, Color.BLACK);

        for (char file = 'a'; file <= 'j'; file++) {
            put("" + file + "1", PieceType.PAWN, Color.WHITE);
            put("" + file + "8", PieceType.PAWN, Color.BLACK);
        }
    }
    private void put(String sq, PieceType type, Color color) {
        board.set(Pos.from(sq), new Piece(type, color));
    }

    private OmegaGame copy() {
        OmegaGame g = new OmegaGame();
        g.board = this.board.copy();
        g.sideToMove = this.sideToMove;
        g.wCastleK = this.wCastleK;
        g.wCastleQ = this.wCastleQ;
        g.bCastleK = this.bCastleK;
        g.bCastleQ = this.bCastleQ;
        g.enPassantPawn = this.enPassantPawn;
        g.enPassantTargets = new ArrayList<>(this.enPassantTargets);
        return g;
    }

    private void applyMove(Move m) {
        applyMoveNoTurnSwitch(m);
        sideToMove = sideToMove.opposite();
    }

    private void applyMoveNoTurnSwitch(Move m) {
        Piece moving = board.get(m.from);

        enPassantPawn = null;
        enPassantTargets = Collections.emptyList();

        updateCastlingRightsBefore(m);

        if (m.kind == MoveKind.CASTLING) {
            board.move(m.from, m.to);
            board.move(m.extraFrom, m.extraTo);
        } else if (m.kind == MoveKind.EN_PASSANT) {
            board.move(m.from, m.to);
            board.set(m.extraFrom, null);
        } else if (m.kind == MoveKind.PROMOTION) {
            board.move(m.from, m.to);
            board.set(m.to, new Piece(PieceType.QUEEN, moving.color));
        } else {
            board.move(m.from, m.to);
        }

        if (moving != null && moving.type == PieceType.PAWN) {
            int dy = m.to.y - m.from.y;
            int abs = Math.abs(dy);
            int startRank = (moving.color == Color.WHITE) ? 1 : 8;
            if (m.from.y == startRank && (abs == 2 || abs == 3) && m.from.x == m.to.x) {
                List<Pos> passed = new ArrayList<>();
                int dir = moving.color.pawnDir;
                for (int step = 1; step < abs; step++) {
                    passed.add(Pos.of(m.from.x, m.from.y + dir * step));
                }
                enPassantPawn = m.to;
                enPassantTargets = passed;
            }
        }
    }

    private void updateCastlingRightsBefore(Move m) {
        Piece moving = board.get(m.from);
        Piece captured = board.get(m.to);

        if (moving != null && moving.type == PieceType.KING) {
            if (moving.color == Color.WHITE) { wCastleK = false; wCastleQ = false; }
            else { bCastleK = false; bCastleQ = false; }
        }

        if (moving != null && moving.type == PieceType.ROOK) {
            if (moving.color == Color.WHITE) {
                if (m.from.equals(Pos.from("b0"))) wCastleQ = false;
                if (m.from.equals(Pos.from("i0"))) wCastleK = false;
            } else {
                if (m.from.equals(Pos.from("b9"))) bCastleQ = false;
                if (m.from.equals(Pos.from("i9"))) bCastleK = false;
            }
        }

        if (captured != null && captured.type == PieceType.ROOK) {
            if (captured.color == Color.WHITE) {
                if (m.to.equals(Pos.from("b0"))) wCastleQ = false;
                if (m.to.equals(Pos.from("i0"))) wCastleK = false;
            } else {
                if (m.to.equals(Pos.from("b9"))) bCastleQ = false;
                if (m.to.equals(Pos.from("i9"))) bCastleK = false;
            }
        }
    }

    private boolean isInCheck(Color color) {
        Pos king = findKing(color);
        return king != null && isSquareAttacked(king, color.opposite());
    }

    private Pos findKing(Color color) {
        for (int x = -1; x <= 10; x++) {
            for (int y = -1; y <= 10; y++) {
                Pos p = Pos.of(x, y);
                if (!board.isValid(p)) continue;
                Piece piece = board.get(p);
                if (piece != null && piece.color == color && piece.type == PieceType.KING) return p;
            }
        }
        return null;
    }
    private boolean isSquareAttacked(Pos target, Color byColor) {
        for (int x = -1; x <= 10; x++) {
            for (int y = -1; y <= 10; y++) {
                Pos from = Pos.of(x, y);
                if (!board.isValid(from)) continue;
                Piece p = board.get(from);
                if (p == null || p.color != byColor) continue;
                for (Pos a : attackSquaresFrom(from)) {
                    if (a.equals(target)) return true;
                }
            }
        }
        return false;
    }

    private List<Pos> attackSquaresFrom(Pos from) {
        Piece p = board.get(from);
        if (p == null) return Collections.emptyList();

        if (p.type == PieceType.PAWN) {
            List<Pos> res = new ArrayList<>();
            int dir = p.color.pawnDir;
            addIfValid(res, Pos.of(from.x - 1, from.y + dir));
            addIfValid(res, Pos.of(from.x + 1, from.y + dir));
            return res;
        }

        if (p.type == PieceType.KING) {
            List<Pos> res = new ArrayList<>();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    addIfValid(res, Pos.of(from.x + dx, from.y + dy));
                }
            }
            return res;
        }

        if (p.type == PieceType.BISHOP || p.type == PieceType.ROOK || p.type == PieceType.QUEEN) {
            return rayAttacks(from, p.type);
        }

        List<Move> pseudo = pseudoMovesFrom(from);
        List<Pos> res = new ArrayList<>();
        for (Move m : pseudo) res.add(m.to);
        return res;
    }

    private List<Pos> rayAttacks(Pos from, PieceType type) {
        int[][] dirs;
        if (type == PieceType.BISHOP) dirs = new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}};
        else if (type == PieceType.ROOK) dirs = new int[][]{{1,0},{-1,0},{0,1},{0,-1}};
        else dirs = new int[][]{{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};

        List<Pos> res = new ArrayList<>();
        for (int[] d : dirs) {
            int x = from.x + d[0];
            int y = from.y + d[1];
            while (true) {
                Pos p = Pos.of(x, y);
                if (!board.isValid(p)) break;
                res.add(p);
                if (board.get(p) != null) break;
                x += d[0];
                y += d[1];
            }
        }
        return res;
    }

    private void addIfValid(List<Pos> out, Pos p) {
        if (board.isValid(p)) out.add(p);
    }

    private List<Move> pseudoMovesFrom(Pos from) {
        Piece p = board.get(from);
        if (p == null) return Collections.emptyList();

        switch (p.type) {
            case PAWN: return pawnMoves(from, p.color);
            case KNIGHT: return leaperMoves(from, p.color, new int[][]{
                    {1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}
            });
            case CHAMPION: return leaperMoves(from, p.color, new int[][]{
                    {1,0},{-1,0},{0,1},{0,-1},
                    {2,0},{-2,0},{0,2},{0,-2},
                    {2,2},{2,-2},{-2,2},{-2,-2}
            });
            case WIZARD: return wizardMoves(from, p.color);
            case BISHOP: return rayMoves(from, p.color, new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}});
            case ROOK: return rayMoves(from, p.color, new int[][]{{1,0},{-1,0},{0,1},{0,-1}});
            case QUEEN: return rayMoves(from, p.color, new int[][]{
                    {1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}
            });
            case KING: return kingMoves(from, p.color);
            default: return Collections.emptyList();
        }
    }
    private List<Move> wizardMoves(Pos from, Color color) {
        List<Move> res = new ArrayList<>();
        int[][] deltas = {
                {1,1},{1,-1},{-1,1},{-1,-1},
                {1,3},{3,1},{-1,3},{-3,1},{1,-3},{3,-1},{-1,-3},{-3,-1}
        };
        for (int[] d : deltas) {
            Pos to = Pos.of(from.x + d[0], from.y + d[1]);
            if (!board.isValid(to)) continue;
            if (to.parity() != from.parity()) continue; // color-bound safety
            Piece dst = board.get(to);
            if (dst == null || dst.color != color) res.add(Move.normal(from, to));
        }
        return res;
    }

    private List<Move> pawnMoves(Pos from, Color color) {
        List<Move> res = new ArrayList<>();
        int dir = color.pawnDir;

        Pos f1 = Pos.of(from.x, from.y + dir);
        if (board.isValid(f1) && board.get(f1) == null) {
            res.add(pawnMoveOrPromo(from, f1, color));

            int startRank = (color == Color.WHITE) ? 1 : 8;
            if (from.y == startRank) {
                Pos f2 = Pos.of(from.x, from.y + 2 * dir);
                if (board.isValid(f2) && board.get(f2) == null) {
                    res.add(Move.normal(from, f2));

                    Pos f3 = Pos.of(from.x, from.y + 3 * dir);
                    if (board.isValid(f3) && board.get(f3) == null) {
                        res.add(Move.normal(from, f3));
                    }
                }
            }
        }

        Pos c1 = Pos.of(from.x - 1, from.y + dir);
        Pos c2 = Pos.of(from.x + 1, from.y + dir);
        addPawnCapture(res, from, c1, color);
        addPawnCapture(res, from, c2, color);

        if (enPassantPawn != null && !enPassantTargets.isEmpty()) {
            for (Pos t : enPassantTargets) {
                if (t.equals(c1) && board.get(c1) == null) res.add(Move.enPassant(from, c1, enPassantPawn));
                if (t.equals(c2) && board.get(c2) == null) res.add(Move.enPassant(from, c2, enPassantPawn));
            }
        }

        return res;
    }

    private void addPawnCapture(List<Move> out, Pos from, Pos to, Color color) {
        if (!board.isValid(to)) return;
        Piece dst = board.get(to);
        if (dst != null && dst.color != color) out.add(pawnMoveOrPromo(from, to, color));
    }

    private Move pawnMoveOrPromo(Pos from, Pos to, Color color) {
        int lastRank = (color == Color.WHITE) ? 9 : 0;
        if (to.y == lastRank) return Move.promotion(from, to);
        return Move.normal(from, to);
    }

    private List<Move> leaperMoves(Pos from, Color color, int[][] deltas) {
        List<Move> res = new ArrayList<>();
        for (int[] d : deltas) {
            Pos to = Pos.of(from.x + d[0], from.y + d[1]);
            if (!board.isValid(to)) continue;
            Piece dst = board.get(to);
            if (dst == null || dst.color != color) res.add(Move.normal(from, to));
        }
        return res;
    }

    private List<Move> rayMoves(Pos from, Color color, int[][] dirs) {
        List<Move> res = new ArrayList<>();
        for (int[] d : dirs) {
            int x = from.x + d[0];
            int y = from.y + d[1];
            while (true) {
                Pos to = Pos.of(x, y);
                if (!board.isValid(to)) break;
                Piece dst = board.get(to);
                if (dst == null) {
                    res.add(Move.normal(from, to));
                } else {
                    if (dst.color != color) res.add(Move.normal(from, to));
                    break;
                }
                x += d[0];
                y += d[1];
            }
        }
        return res;
    }
    private List<Move> kingMoves(Pos from, Color color) {
        List<Move> res = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Pos to = Pos.of(from.x + dx, from.y + dy);
                if (!board.isValid(to)) continue;
                Piece dst = board.get(to);
                if (dst == null || dst.color != color) res.add(Move.normal(from, to));
            }
        }

        // Castling (king moves 2 squares; rook goes to square king passed through)
        if (color == Color.WHITE && from.equals(Pos.from("f0")) && !isInCheck(Color.WHITE)) {
            // kingside: f0->h0, rook i0->g0
            if (wCastleK && board.get(Pos.from("g0")) == null && board.get(Pos.from("h0")) == null) {
                if (!isSquareAttacked(Pos.from("g0"), Color.BLACK) && !isSquareAttacked(Pos.from("h0"), Color.BLACK)) {
                    if (isRook(Pos.from("i0"), Color.WHITE)) res.add(Move.castling(from, Pos.from("h0"), Pos.from("i0"), Pos.from("g0")));
                }
            }
            // queenside: f0->d0, rook b0->e0
            if (wCastleQ && board.get(Pos.from("e0")) == null && board.get(Pos.from("d0")) == null && board.get(Pos.from("c0")) == null) {
                if (!isSquareAttacked(Pos.from("e0"), Color.BLACK) && !isSquareAttacked(Pos.from("d0"), Color.BLACK)) {
                    if (isRook(Pos.from("b0"), Color.WHITE)) res.add(Move.castling(from, Pos.from("d0"), Pos.from("b0"), Pos.from("e0")));
                }
            }
        }

        if (color == Color.BLACK && from.equals(Pos.from("f9")) && !isInCheck(Color.BLACK)) {
            // kingside: f9->h9, rook i9->g9
            if (bCastleK && board.get(Pos.from("g9")) == null && board.get(Pos.from("h9")) == null) {
                if (!isSquareAttacked(Pos.from("g9"), Color.WHITE) && !isSquareAttacked(Pos.from("h9"), Color.WHITE)) {
                    if (isRook(Pos.from("i9"), Color.BLACK)) res.add(Move.castling(from, Pos.from("h9"), Pos.from("i9"), Pos.from("g9")));
                }
            }
            // queenside: f9->d9, rook b9->e9
            if (bCastleQ && board.get(Pos.from("e9")) == null && board.get(Pos.from("d9")) == null && board.get(Pos.from("c9")) == null) {
                if (!isSquareAttacked(Pos.from("e9"), Color.WHITE) && !isSquareAttacked(Pos.from("d9"), Color.WHITE)) {
                    if (isRook(Pos.from("b9"), Color.BLACK)) res.add(Move.castling(from, Pos.from("d9"), Pos.from("b9"), Pos.from("e9")));
                }
            }
        }

        return res;
    }

    private boolean isRook(Pos p, Color c) {
        Piece r = board.get(p);
        return r != null && r.color == c && r.type == PieceType.ROOK;
    }
}