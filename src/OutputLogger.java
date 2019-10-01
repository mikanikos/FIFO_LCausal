import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class OutputLogger {

    private static Logger logger = Logger.getLogger("MyLogger");;
    private static FileHandler fileHandler;

    public OutputLogger (int id) throws IOException {
        String fileName = "da_proc_" + id + ".out";
        File outputFile = new File(fileName);
        outputFile.createNewFile();
        fileHandler = new FileHandler(fileName);
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
        fileHandler.setFormatter(new MyLogFormatter());

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
