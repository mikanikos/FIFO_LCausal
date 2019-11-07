import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

class OutputLogger {

    private static ConcurrentLinkedQueue<String> Log = new ConcurrentLinkedQueue<>();

    static void writeLog(final String message) {

        if (message != null && !message.trim().isEmpty()) {
            Log.add(message);
        }
    }

    static void writeLogToFile() {

        Collections.sort(FIFOBroadcast.getMessages(), Comparator.comparingInt(MessageSource::getMessageID));

        for (Integer pID : Da_proc.getProcesses().keySet()) {
            int seqID = 1;
            List<MessageSource> messagesFromProcess = FIFOBroadcast.getMessages().stream().filter(x -> x.getSourceID() == pID).collect(Collectors.toList());
            for (MessageSource m : messagesFromProcess) {
                if (m.getMessageID() == seqID) {
                    writeLog("d " + m.getSourceID() + " " + m.getMessageID());
                } else {
                    break;
                }
                seqID++;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("da_proc_" + Da_proc.getId() + ".out"))) {
            for (String messageOutput : Log) {
                writer.write(messageOutput + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
