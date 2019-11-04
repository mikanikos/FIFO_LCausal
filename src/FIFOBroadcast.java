import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FIFOBroadcast {

    private static ConcurrentMap<MessageSource, Boolean> messages = new ConcurrentHashMap<>();;
    private static ConcurrentMap<Integer, AtomicInteger> senderNextID = new ConcurrentHashMap<>();

    static void deliver(MessageSource ms) {

        messages.put(ms, true);
        senderNextID.putIfAbsent(ms.getSourceID(), new AtomicInteger(1));

        while(true) {
            ms = new MessageSource(ms.getSourceID(), senderNextID.get(ms.getSourceID()).get());
            if (messages.remove(ms) != null) {
                OutputLogger.writeLog("d " + ms.getSourceID() + " " + ms.getMessageID());
                senderNextID.computeIfPresent(ms.getSourceID(), (key, value) -> new AtomicInteger(value.incrementAndGet()));
            } else {
                break;
            }
        }
    }
    
}
