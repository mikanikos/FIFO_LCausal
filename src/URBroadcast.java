import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class URBroadcast implements Runnable {

    private static ConcurrentMap<MessageSource, AtomicInteger> ackMessages = new ConcurrentHashMap<>();
    private static ConcurrentMap<MessageSource, Boolean> delivered = new ConcurrentHashMap<>();
    //public ConcurrentLinkedQueue<MessageData> processQueue = new ConcurrentLinkedQueue<>();

    private ProcessData process;
    private ConcurrentLinkedQueue<MessageData> processQueue;

    public ConcurrentLinkedQueue<MessageData> getProcessQueue() {
        return processQueue;
    }

    public URBroadcast(ProcessData process) {
        this.process = process;
        this.processQueue = new ConcurrentLinkedQueue<>();
    }

//    static void broadcast(int sourceID, int messageID) {
//
//        for (ProcessData p : Da_proc.getProcesses().values()) {
//            if (p.getId() != Da_proc.getId()) {
//
//                MessageData message = new MessageData(sourceID, Da_proc.getId(), p.getId(), messageID, false);
//
//                processQueue.add(message);
//            }
//        }
//    }

    public static void broadcast(int sourceID, int messageID) {
        for (ProcessData p : Da_proc.getProcesses().values()) {
            if (p.getId() != Da_proc.getId()) {
                URBroadcast urbInstance = p.getSenderInstance();
                MessageData message = new MessageData(sourceID, Da_proc.getId(), p.getId(), messageID, false);
                urbInstance.processQueue.add(message);
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

        if (ackMessages.get(ms).get() > (Da_proc.getProcesses().keySet().size() / 2)) {
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
                //new Thread(new PerfectLink(m)).start();
        }
    }
}
