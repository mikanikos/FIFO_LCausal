import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class URBroadcast {

    private static ConcurrentMap<MessageSource, Boolean> forwarded = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, Boolean> ackMessages = new ConcurrentHashMap<>();
    private static ConcurrentMap<MessageSource, Boolean> delivered = new ConcurrentHashMap<>();
    private static FIFOBroadcast fifoBroadcast = new FIFOBroadcast();;

    public void broadcast(int sourceID, int messageID) {

        forwarded.put(new MessageSource(Da_proc.getId(), messageID), true);

        for (ProcessData p : Da_proc.getProcesses().values()) {
            if (p.getId() != Da_proc.getId()) {

                MessageData message = new MessageData(sourceID, Da_proc.getId(), p.getId(), messageID, false);

                try {
                    new Thread(new PerfectLink(message)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deliver(MessageData message) {
        boolean variablesChanged = false;
        if (ackMessages.putIfAbsent(message.toString(), false) == null) {
            variablesChanged = true;
        }

        if (forwarded.putIfAbsent(new MessageSource(message.getSourceID(), message.getMessageID()), true) == null) {
            variablesChanged = true;
            broadcast(message.getSourceID(), message.getMessageID());
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
