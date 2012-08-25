package ch.bukkit.playground.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

    public static String format(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(date);
    }
}
