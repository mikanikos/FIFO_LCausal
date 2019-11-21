import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

// Uniform Reliable Broadcast abstraction
public class URBroadcast implements Runnable {

    // ackMessages for URB
    private static ConcurrentMap<MessageSource, AtomicInteger> ackMessages = new ConcurrentHashMap<>();

    // delivered messages by URB
    private static ConcurrentMap<MessageSource, Boolean> delivered = new ConcurrentHashMap<>();

    // sending queue for outgoing messages
    private static ConcurrentLinkedQueue<MessageData> sendingQueue = new ConcurrentLinkedQueue<>();

    public static ConcurrentLinkedQueue<MessageData> getSendingQueue() {
        return sendingQueue;
    }

    // broadcast a given message to all the processes
    public static void broadcast(int sourceID, int messageID, ConcurrentMap<Integer, AtomicInteger> vectorClock) {
        // Message is already acknowledged for me, so put it now
        ackMessages.putIfAbsent(new MessageSource(sourceID, messageID), new AtomicInteger(1));
        for (ProcessData p : Da_proc.getProcesses().values()) {
            // not sending message to myself
            if (p.getId() != Da_proc.getId()) {
                // create message to be sent with all the meta information
                MessageData message = new MessageData(sourceID, Da_proc.getId(), p.getId(), messageID, false, vectorClock);
                // add message to the sending queue
                sendingQueue.add(message);
            }
        }
    }

    public static void deliver(MessageData message) {
        MessageSource ms = new MessageSource(message.getSourceID(), message.getMessageID());

        // increase the number of acknowledgements received for this message id from the source
        // using 2 initially because the message is already acknowledged by me since the current process is handling the message
        if (ackMessages.putIfAbsent(ms, new AtomicInteger(2)) != null) {
            ackMessages.computeIfPresent(ms, (key, value) -> new AtomicInteger(value.incrementAndGet()));
        } else {
            // if this is the first time I get this message, I relay it to all the other processes according to the protocol
            broadcast(message.getSourceID(), message.getMessageID(), message.getVectorClock());
        }

        // Check if I received a majority of acknowledgements: if yes, URB delivers it
        if (ackMessages.getOrDefault(ms, new AtomicInteger(0)).get() > (Da_proc.getNumProcesses() / 2)) {
            if (delivered.putIfAbsent(ms, true) == null) {
                if (Da_proc.getProcesses().get(Da_proc.getId()).getDependencies().contains(ms.getSourceID()) &&  Da_proc.getId() != ms.getSourceID())
                    LCausalBroadcast.causalQueue.add(message);
                else
                    FIFOBroadcast.fifoQueue.add(ms);
            }
        }
    }

    @Override
    // process packet sending from queue on a different thread
    public void run() {
        // sending packets from the queue until the termination signal
        while(Da_proc.isRunning()) {
            MessageData m;
            // get head of the queue and send it
            while ((m = sendingQueue.poll()) != null)
                PerfectLink.send(m);
        }
    }
}