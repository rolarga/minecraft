package ch.bukkit.playground.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    public static String format(Date date) {
        if(date == null) {
            return "<unknown>";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(date);
    }

    public static long getMillisForMinutes(int minutes) {
        return minutes * 60000;
    }

    public static BigDecimal getMinutesForMillis(int millis) {
        return new BigDecimal(millis / 60000.).setScale(1, RoundingMode.HALF_UP);
    }
}
