import java.io.File;
import java.io.IOException;
import java.util.logging.*;

class OutputLogger {

    private static Logger logger = Logger.getLogger("MyLogger");;

    OutputLogger(int id) throws IOException {
        String fileName = "da_proc_" + id + ".out";
        File outputFile = new File(fileName);
        outputFile.createNewFile();
        FileHandler fileHandler = new FileHandler(fileName);
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
        fileHandler.setFormatter(new MyLogFormatter());
    }

    static void writeLog(String data) {
        logger.info(data);
    }

    private static class MyLogFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return record.getMessage() + "\n";
        }
    }
}
