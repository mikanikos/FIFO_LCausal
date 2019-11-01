import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class URBroadcast implements Runnable {

    private static ConcurrentMap<MessageSource, Boolean> forwarded = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, Boolean> ackMessages = new ConcurrentHashMap<>();
    private static ConcurrentMap<MessageSource, Boolean> delivered = new ConcurrentHashMap<>();
    private static FIFOBroadcast fifoBroadcast = new FIFOBroadcast();;

    private ProcessData process;
    private ConcurrentLinkedQueue<MessageData> processQueue;

    public ConcurrentLinkedQueue<MessageData> getProcessQueue() {
        return processQueue;
    }

    public void setProcessQueue(ConcurrentLinkedQueue<MessageData> processQueue) {
        this.processQueue = processQueue;
    }

    public URBroadcast(ProcessData process) {
        this.process = process;
        this.processQueue = new ConcurrentLinkedQueue<>();
    }


    public void broadcast() {
        for (int seq_nr = 1; seq_nr <= Da_proc.getNumMessages() && Da_proc.isRunning(); seq_nr++) {
            forwarded.put(new MessageSource(Da_proc.getId(), seq_nr), true);
            MessageData message = new MessageData(Da_proc.getId(), Da_proc.getId(), process.getId(), seq_nr, false);
            //OutputLogger.writeLog("b " + seq_nr);
            this.processQueue.add(message);
        }
    }

    public void run() {
        while(Da_proc.isRunning()) {
            MessageData m;
            while ((m = processQueue.poll()) != null)
                new PerfectLink().send(m);
        }
    }

    public static void sendToAll(int sourceID, int messageID) {
        for (ProcessData p : Da_proc.getProcesses().values()) {
            if (p.getId() != Da_proc.getId()) {
                URBroadcast urbInstance = p.getSenderInstance();
                MessageData message = new MessageData(sourceID, Da_proc.getId(), p.getId(), messageID, false);
                urbInstance.processQueue.add(message);
            }
        }
    }

    public static void deliver(MessageData message) {
        boolean variablesChanged = false;
        if (ackMessages.putIfAbsent(message.toString(), false) == null) {
            variablesChanged = true;
        }

        if (forwarded.putIfAbsent(new MessageSource(message.getSourceID(), message.getMessageID()), true) == null) {
            variablesChanged = true;
            sendToAll(message.getSourceID(), message.getMessageID());
        }
        if (variablesChanged && message.getSourceID() != Da_proc.getId()) {
            for (MessageSource ms : forwarded.keySet()) {
                int count = (int) ackMessages.keySet().stream().filter(str -> MessageData.parseMessage(str).getMessageID() == ms.getMessageID()).count();
                if (!delivered.containsKey(ms) && count > (Da_proc.getProcesses().keySet().size() / 2) && ms.getSourceID() != Da_proc.getId()) {
                    delivered.put(ms, true);
                    fifoBroadcast.deliver(ms);
                }
            }
        }
    }
}
