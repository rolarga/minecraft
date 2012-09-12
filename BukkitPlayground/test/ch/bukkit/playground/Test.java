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
        String name = "test";

        long currentMillis = System.currentTimeMillis();
        Date tFirstMessage = new Date(currentMillis + (DateHelper.getMillisForMinutes(offset) / 2));
        System.out.println("Added first message: " + DateHelper.format(tFirstMessage));
        Date tSecondMessage = new Date(currentMillis + (DateHelper.getMillisForMinutes(offset * 2) / 3));
        System.out.println("Added second message: " + DateHelper.format(tSecondMessage));
        Date t = new Date(currentMillis + DateHelper.getMillisForMinutes(offset));
        System.out.println("t: " + DateHelper.format(t));
        Date tPlus1Minute = new Date(currentMillis + DateHelper.getMillisForMinutes(offset + 1));
        System.out.println("t+1: " + DateHelper.format(tPlus1Minute));


        long millis = Math.abs(tFirstMessage.getTime() - currentMillis);
        System.out.println(millis);
        System.out.println("first: " + DateHelper.getMinutesForMillis(millis));

        millis = Math.abs(currentMillis - tSecondMessage.getTime());
        System.out.println(millis);
        System.out.println("second: " + DateHelper.getMinutesForMillis(millis));


        millis = t.getTime() - tFirstMessage.getTime();
        System.out.println(millis);
        System.out.println("first: " + DateHelper.getMinutesForMillis(millis));

        millis = t.getTime() - tSecondMessage.getTime();
        System.out.println(millis);
        System.out.println("second: " + DateHelper.getMinutesForMillis(millis));

    }
}

