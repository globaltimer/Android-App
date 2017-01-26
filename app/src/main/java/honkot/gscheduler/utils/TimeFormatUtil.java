package honkot.gscheduler.utils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by hiroki on 2016-11-25.
 */
public class TimeFormatUtil {

    public static String getGMT(TimeZone tz) {
        final int HOURS_1 = 60 * 60000;

        long date = Calendar.getInstance().getTimeInMillis();
        final int offset = tz.getOffset(date);
        final int p = Math.abs(offset);
        final StringBuilder name = new StringBuilder();
        name.append("GMT");

        if (offset < 0) {
            name.append('-');
        } else {
            name.append('+');
        }

        name.append(p / (HOURS_1));
        name.append(':');

        int min = p / 60000;
        min %= 60;

        if (min < 10) {
            name.append('0');
        }
        name.append(min);

        return name.toString();
    }
}
