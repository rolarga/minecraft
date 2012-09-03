package ch.bukkit.playground.util;

import org.bukkit.entity.Player;

import java.util.*;

public class PlayerUtil {

    public static Map<Integer, List<Player>> getEqualDistributedGroupByLevel(int groupQuantity, Collection<Player> players) {
        Map<Integer, List<Player>> groups = new HashMap<Integer, List<Player>>();
        Map<Integer, Boolean> groupSortingState = new HashMap<Integer, Boolean>();
        List<Player> playersToSort = new LinkedList<Player>(players);
        int totalPlayers = players.size();

        for (int i = 0; i < totalPlayers; i++) {
            int currentGroup = i % groupQuantity;
            // how should it be sorted this turn
            Boolean desc = groupSortingState.get(currentGroup);
            if (desc == null) {
                desc = false;
                groupSortingState.put(currentGroup, desc);
            }

            List<Player> sortedPlayers = getPlayersSortedByLevel(desc, playersToSort);

            List<Player> thisGroup = groups.get(currentGroup);
            if (thisGroup == null) {
                thisGroup = new LinkedList<Player>();
                groups.put(currentGroup, thisGroup);
            }
            Player playerToAdd = sortedPlayers.remove(0);
            thisGroup.add(playerToAdd);
            playersToSort = sortedPlayers;

            groupSortingState.put(currentGroup, !desc);
        }

        return groups;
    }

    public static List<Player> getPlayersSortedByLevel(final boolean desc, List<Player> players) {
        List<Player> playersSorted = new LinkedList<Player>(players);
        Collections.sort(playersSorted, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                if (!desc) {
                    return p1.getLevel() - p2.getLevel();
                }
                return p2.getLevel() - p1.getLevel();
            }
        });
        return playersSorted;
    }
}