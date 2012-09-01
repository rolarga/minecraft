package ch.bukkit.playground;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Test {

    public static void main(String[] args) throws IOException {
//        InstantHandler instantHandler = new InstantHandler();
//        instantHandler.start();
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//        String s;
//        while ((s = in.readLine()) != null) {
//            if ("exit".equals(s)) System.exit(0);
//
//            if (s.startsWith("create")) {
//                s = s.replace("create ", "");
//                instantHandler.handleOpCommands(s, "stat", null, null);
//            }
//        }

        List<Integer> levels = new LinkedList<Integer>();
        levels.add(11);
        levels.add(2);
        levels.add(9);
        levels.add(4);
        levels.add(23);
        levels.add(6);

        sort(levels, false);
        sort(levels, true);

    }

    private static void sort(List<Integer> levels, final boolean desc) {
        List<Integer> playersSorted = new LinkedList<Integer>(levels);
        Collections.sort(playersSorted, new Comparator<Integer>() {
            @Override
            public int compare(Integer player, Integer player1) {
                if (!desc) {
                    return player - player1;
                }
                return player1 - player;
            }
        });
        System.out.println(playersSorted);
    }
}

