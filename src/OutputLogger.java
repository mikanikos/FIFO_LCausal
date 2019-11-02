import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.*;

class OutputLogger {

    //private static Logger logger = Logger.getLogger("MyLogger");;

//    OutputLogger(int id) throws IOException {
//        String fileName = "da_proc_" + id + ".out";
//        File outputFile = new File(fileName);
//        outputFile.createNewFile();
//        FileHandler fileHandler = new FileHandler(fileName);
//        logger.addHandler(fileHandler);
//        logger.setUseParentHandlers(false);
//        fileHandler.setFormatter(new MyLogFormatter());
//    }
//
//    static void writeLog(String data) {
//        logger.info(data);
//    }
//
//    private static class MyLogFormatter extends Formatter {
//        @Override
//        public String format(LogRecord record) {
//            return record.getMessage() + "\n";
//        }
//    }

    private static ConcurrentLinkedQueue<String> Log  = new ConcurrentLinkedQueue<>();

    static void writeLog(final String message) {

        if (message != null && !message.trim().isEmpty()) {
            Log.add(message);
        }
    }

    static void writeLogToFile() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("da_proc_" + Da_proc.getId() + ".out"))) {
            for (String message : Log) {
                writer.write(message + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
