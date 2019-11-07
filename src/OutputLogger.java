import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

class OutputLogger {

    private static ConcurrentLinkedQueue<String> Log = new ConcurrentLinkedQueue<>();

    static void writeLog(final String message) {

        if (message != null && !message.trim().isEmpty()) {
            Log.add(message);
        }
    }

    static void writeLogToFile() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("da_proc_" + Da_proc.getId() + ".out"))) {
            for (String messageOutput : Log) {
                writer.write(messageOutput + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
