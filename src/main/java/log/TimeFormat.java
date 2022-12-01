package log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormat {

    public static String getCurrentTime() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");
        String date = format.format(new Date());
        return "[" + date + "] ";
    }
}
