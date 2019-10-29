import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FIFOBroadcast {

    ConcurrentMap<MessageSource, Boolean> messages;
    ConcurrentMap<Integer,Integer> senderNextID;

    public FIFOBroadcast() {
        messages = new ConcurrentHashMap<>();
        senderNextID = new ConcurrentHashMap<>();
    }

    public void deliver(MessageSource ms) {

        messages.put(ms, true);

        if (!senderNextID.containsKey(ms.getSourceID()))
            senderNextID.put(ms.getSourceID(), 1);

        boolean delivered;
        do {
            delivered = false;
            for (MessageSource m : messages.keySet()) {
                int nextID = senderNextID.get(m.getSourceID());
                if (m.getMessageID() == nextID && m.getSourceID() == m.getSourceID()) {
                    //System.out.println("Delivered");
                    delivered = true;
                    OutputLogger.writeLog("d " + m.getSourceID() + " " + m.getMessageID());
                    senderNextID.put(m.getSourceID(), nextID + 1);
                    messages.remove(m);
                }
            }
        } while (delivered);
    }
}
