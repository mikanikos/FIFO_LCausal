import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class URBroadcast implements Runnable {

    private static Map<MessageSource, Integer> ackMessages = new HashMap<>();
    private static Set<MessageSource> delivered = new HashSet<>();
    public static ConcurrentLinkedQueue<MessageData> processQueue = new ConcurrentLinkedQueue<>();

    public static void broadcast(int sourceID, int messageID) {

        ackMessages.putIfAbsent(new MessageSource(sourceID, messageID), 1);
        for (ProcessData p : Da_proc.getProcesses().values()) {
            if (p.getId() != Da_proc.getId()) {
                MessageData message = new MessageData(sourceID, Da_proc.getId(), p.getId(), messageID, false);
                processQueue.add(message);
            }
        }
    }

    public static void deliver(MessageData message) {
        MessageSource ms = new MessageSource(message.getSourceID(), message.getMessageID());

        if (ackMessages.putIfAbsent(ms, 2) != null) {
            ackMessages.computeIfPresent(ms, (key, value) -> value+1);
        } else {
            broadcast(message.getSourceID(), message.getMessageID());
        }

        if (ackMessages.get(ms) > (Da_proc.getNumProcesses() / 2)) {
            if (!delivered.contains(ms)) {
                //OutputLogger.writeLog("d " + ms.getSourceID() + " " + ms.getMessageID());
                delivered.add(ms);
                FIFOBroadcast.deliver(ms);
            }
        }
    }

    @Override
    public void run() {
        while(Da_proc.isRunning()) {
            MessageData m;
            while ((m = processQueue.poll()) != null)
                PerfectLink.send(m);
                //new Thread(new PerfectLink(m)).start();
        }
    }
}