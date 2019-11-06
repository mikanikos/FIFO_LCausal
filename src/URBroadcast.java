import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class URBroadcast implements Runnable {

    private static ConcurrentMap<MessageSource, AtomicInteger> ackMessages = new ConcurrentHashMap<>();
    private static ConcurrentMap<MessageSource, Boolean> delivered = new ConcurrentHashMap<>();
    public static ConcurrentLinkedQueue<MessageData> processQueue = new ConcurrentLinkedQueue<>();

    public static void broadcast(int sourceID, int messageID) {

        ackMessages.putIfAbsent(new MessageSource(sourceID, messageID), new AtomicInteger(1));
        for (ProcessData p : Da_proc.getProcesses().values()) {
            if (p.getId() != Da_proc.getId()) {
                MessageData message = new MessageData(sourceID, Da_proc.getId(), p.getId(), messageID, false);
                processQueue.add(message);
            }
        }
    }

    public static void deliver(MessageData message) {
        MessageSource ms = new MessageSource(message.getSourceID(), message.getMessageID());

        if (ackMessages.putIfAbsent(ms, new AtomicInteger(2)) != null) {
            ackMessages.computeIfPresent(ms, (key, value) -> new AtomicInteger(value.incrementAndGet()));
        } else {
            broadcast(message.getSourceID(), message.getMessageID());
        }

        if (ackMessages.get(ms).get() > (Da_proc.getNumProcesses() / 2)) {
            if (delivered.putIfAbsent(ms, true) == null)
                //OutputLogger.writeLog("d " + ms.getSourceID() + " " + ms.getMessageID());
                FIFOBroadcast.deliver(ms);
        }
    }

    @Override
    public void run() {
        while(Da_proc.isRunning()) {
            MessageData m;
            while ((m = processQueue.poll()) != null)
                PerfectLink.send(m);
        }
    }
}