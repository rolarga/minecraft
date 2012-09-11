package ch.bukkit.playground;

import ch.bukkit.playground.instant.model.BattleData;
import ch.bukkit.playground.util.DateHelper;

import java.io.IOException;
import java.util.Date;

public class Test {

    public static void main(String[] args) throws IOException {

        final BattleData battleData = new BattleData();
        double totalRounds = 12.;
        double roundQuantity = 4.;
        int levels = 3;
        int offset = 2;
        int duration = 30;
        Date tPlus1Minute = new Date(System.currentTimeMillis() + DateHelper.getMillisForMinutes(offset + 1));
        Date time = tPlus1Minute;

        System.out.println("Current time " + DateHelper.format(new Date(System.currentTimeMillis())));
        double levelDuration = ((double) duration) / ((double) levels);
        for (int i = 0; i < levels; i++) {
            int millisPerRound = (int) Math.max(DateHelper.getMillisForMinutes(levelDuration) / roundQuantity, 1);
            System.out.println("One turn takes " + DateHelper.getMinutesForMillis(millisPerRound) + " minutes or " + millisPerRound + " millis");

            System.out.println("Adding welcome message at " + DateHelper.format(time));
            for (int j = 0; j < roundQuantity; j++) {
                System.out.println("Adding spawns at " + DateHelper.format(time));
                time = new Date(time.getTime() + millisPerRound);
                battleData.setEndDate(time);
            }
        }
        System.out.println(DateHelper.format(battleData.getEndDate()));
    }
}

