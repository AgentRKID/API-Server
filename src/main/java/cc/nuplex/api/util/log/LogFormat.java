package cc.nuplex.api.util.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogFormat extends Handler {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE hh:mm aaa");

    @Override
    public void publish(LogRecord record) {
        System.out.printf("[%s] [%s] %s%n", DATE_FORMAT.format(new Date()), record.getLevel().getName(), record.getMessage());
    }

    @Override
    public void flush() { }

    @Override
    public void close() throws SecurityException { }

}
