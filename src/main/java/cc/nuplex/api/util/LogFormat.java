package cc.nuplex.api.util;

import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogFormat extends Handler {

    @Override
    public void publish(LogRecord record) {
        System.out.printf("[%s] [%s] %s%n", new Date(), record.getLevel().getName(), record.getMessage());
    }

    @Override
    public void flush() { }

    @Override
    public void close() throws SecurityException { }

}
