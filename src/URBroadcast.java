import java.util.*;
import java.util.concurrent.*;

public class URBroadcast implements Runnable {

    private static Map<MessageSource, Integer> ackMessages = new HashMap<>();
    private static Set<MessageSource> delivered = new HashSet<>();
    //public static DelayQueue<MessageData> processQueue = new DelayQueue<>();
    public static ConcurrentLinkedQueue<MessageData> processQueue = new ConcurrentLinkedQueue<>();
    //public static PriorityBlockingQueue<MessageData> processQueue = new PriorityBlockingQueue<>(Da_proc.getNumMessages() * Da_proc.getNumProcesses(), Comparator.comparingInt(MessageData::getMessageID));


    public static void broadcast(int sourceID, int messageID) {

        ackMessages.putIfAbsent(new MessageSource(sourceID, messageID), 1);
        for (ProcessData p : Da_proc.getProcesses().values()) {
            if (p.getId() != Da_proc.getId()) {
                MessageData message = new MessageData(sourceID, Da_proc.getId(), p.getId(), messageID, false);
                processQueue.offer(message);
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

        if (ackMessages.getOrDefault(ms, 0) > (Da_proc.getNumProcesses() / 2)) {
            if (!delivered.contains(ms)) {
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