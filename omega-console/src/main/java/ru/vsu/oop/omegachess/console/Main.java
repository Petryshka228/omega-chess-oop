package ru.vsu.oop.omegachess.console;

import ru.vsu.oop.omegachess.engine.OmegaGame;
import ru.vsu.oop.omegachess.engine.Pos;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        OmegaGame g = OmegaGame.newGame();
        Scanner sc = new Scanner(System.in);

        System.out.println(g.toAscii());
        System.out.println("Commands: print | moves e1 | e1e4 | quit");

        while (true) {
            System.out.print(g.sideToMove() + " > ");
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("quit")) break;

            if (line.equalsIgnoreCase("print")) {
                System.out.println(g.toAscii());
                continue;
            }

            if (line.toLowerCase().startsWith("moves ")) {
                String sq = line.substring(6).trim();
                try {
                    System.out.println(g.legalMovesFrom(Pos.from(sq)));
                } catch (Exception e) {
                    System.out.println("Bad square");
                }
                continue;
            }

            boolean ok = false;
            try {
                ok = g.tryMove(line);
            } catch (Exception ignored) {}

            if (!ok) System.out.println("Illegal move");
            else System.out.println(g.toAscii());
        }
    }
}