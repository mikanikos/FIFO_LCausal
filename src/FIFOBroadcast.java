import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

// FIFO abstraction
public class FIFOBroadcast implements Runnable {

    // Messages delivered by URB
    private static ConcurrentMap<MessageSource, Boolean> messages = new ConcurrentHashMap<>();;

    // Map <Process id> <Next ID expected to be delivered according to FIFO>
    private static ConcurrentMap<Integer, AtomicInteger> senderNextID = new ConcurrentHashMap<>();

    public static ConcurrentLinkedQueue<MessageSource> fifoQueue = new ConcurrentLinkedQueue<>();

    // Deliver message for FIFO protocol
    public void run() {

        while(Da_proc.isRunning()) {
            MessageSource ms;
            // get head of the queue and process it
            while ((ms = fifoQueue.poll()) != null) {

                // Store the message
                messages.put(ms, true);

                // Initialize first next id expected if not present
                senderNextID.putIfAbsent(ms.getSourceID(), new AtomicInteger(1));

                while (true) {
                    ms = new MessageSource(ms.getSourceID(), senderNextID.get(ms.getSourceID()).get());

                    // Check if we can deliver the message (i.e. if the message with the next id expected has arrived)
                    if (messages.remove(ms) != null) {
                        // Write to log
                        OutputLogger.writeLog("d " + ms.getSourceID() + " " + ms.getMessageID());
                        // Compute next id because we could still have other messages to deliver
                        senderNextID.computeIfPresent(ms.getSourceID(), (key, value) -> new AtomicInteger(value.incrementAndGet()));
                    } else {
                        break;
                    }
                }
            }
        }
    }
}