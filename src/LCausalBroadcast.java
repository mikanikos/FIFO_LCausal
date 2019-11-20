import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LCausalBroadcast {

    // messages delivered by URB
    private static Map<Integer, SortedSet<MessageData>> messagesPerProcess = new HashMap<>();

    public LCausalBroadcast() {
        for (int i = 1; i <= Da_proc.getNumProcesses(); i++) {
            messagesPerProcess.putIfAbsent(i, new TreeSet<>(new MessageDataComparator()));
        }
    }

    // Deliver message for LCausal protocol
    public static void deliver(MessageData md) {

        // Store the message
        messagesPerProcess.get(md.getSourceID()).add(md);

        boolean keepRunning = true;
        while (keepRunning) {
            keepRunning = false;
            for (int i = 1; i <= Da_proc.getNumProcesses(); i++) {
                Iterator<MessageData> messageDataIterator = messagesPerProcess.get(i).iterator();
                while (messageDataIterator.hasNext()) {
                    MessageData m = messageDataIterator.next();
                    if (isVectorClockLessOrEqual(m.getVectorClock(), Da_proc.getVectorClock())) {
                        messageDataIterator.remove();
                        Da_proc.getVectorClock().get(m.getSourceID()).incrementAndGet();
                        OutputLogger.writeLog("d " + m.getSourceID() + " " + m.getMessageID());
                        keepRunning = true;
                        System.out.println("Delivered : " + "d " + m.getSourceID() + " " + m.getMessageID());
                        System.out.println(messagesPerProcess.get(i).size());
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
