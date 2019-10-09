import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.*;

public class OutputLogger {

    private static Logger logger = Logger.getLogger("MyLogger");;
    private static FileHandler fileHandler;
    private static Lock loggerLock;

    public static Lock getLoggerLock() {
        return loggerLock;
    }

    public OutputLogger(int id) throws IOException {
        String fileName = "da_proc_" + id + ".out";
        File outputFile = new File(fileName);
        outputFile.createNewFile();
        fileHandler = new FileHandler(fileName);
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
        fileHandler.setFormatter(new MyLogFormatter());
        loggerLock = new ReentrantLock();
    }

    public static void writeLog(String data) {
        logger.info(data);
    }

    private static class MyLogFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            StringBuffer sb = new StringBuffer();
            sb.append(record.getMessage()+"\n");
            return sb.toString();
        }

    }
}
