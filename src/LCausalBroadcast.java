import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LCausalBroadcast implements Runnable {

    // Messages delivered by URB
    public static Map<Integer, SortedSet<MessageData>> messagesPerProcess = new HashMap<>();

    public static ConcurrentLinkedQueue<MessageData> causalQueue = new ConcurrentLinkedQueue<>();

    public LCausalBroadcast() {
        for (int i = 1; i <= Da_proc.getNumProcesses(); i++) {
            messagesPerProcess.putIfAbsent(i, new TreeSet<>(Comparator.comparingInt(MessageData::getMessageID)));
        }
    }

    // Deliver message for LCausal protocol
    public void run() {

        while(Da_proc.isRunning()) {
            MessageData md;
            // Get head of the queue and process it
            while ((md = causalQueue.poll()) != null) {

                // Store the message
                messagesPerProcess.get(md.getSourceID()).add(md);

                boolean keepRunning = true;
                while (keepRunning) {
                    keepRunning = false;
                    for (int i = 1; i <= Da_proc.getNumProcesses(); i++) {
                        if (Da_proc.getProcesses().get(Da_proc.getId()).getDependencies().contains(i)) {
                            Iterator<MessageData> messageDataIterator = messagesPerProcess.get(i).iterator();
                            while (messageDataIterator.hasNext()) {
                                MessageData m = messageDataIterator.next();
                                if (isVectorClockLessOrEqual(m.getVectorClock(), Da_proc.getVectorClockSend())) {
                                    messageDataIterator.remove();
                                    Da_proc.getVectorClockSend().get(m.getSourceID()).incrementAndGet();
                                    OutputLogger.writeLog("d " + m.getSourceID() + " " + m.getMessageID());
                                    keepRunning = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isVectorClockLessOrEqual(ConcurrentMap<Integer, AtomicInteger> target, ConcurrentMap<Integer, AtomicInteger> reference) {
        for (int i = 1; i <= Da_proc.getNumProcesses(); i++) {
            if (target.get(i).get() > reference.get(i).get()) {
                return false;
            }
        }
        return true;
    }

}
