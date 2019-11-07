import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

// Log and create messages for output files
public class OutputLogger {

    // Store logs
    private static ConcurrentLinkedQueue<String> logs  = new ConcurrentLinkedQueue<>();

    // Store log message
    public static void writeLog(final String message) {
        if (message != null && !message.trim().isEmpty()) {
            logs.add(message);
        }
    }

    // Write all messages stored to the log file
    public static void writeLogToFile() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("da_proc_" + Da_proc.getId() + ".out"))) {
            for (String message : logs) {
                writer.write(message + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
