import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FIFOBroadcast {

    public static Set<MessageSource> messages = new HashSet<>();;
    private static Map<Integer, Integer> senderNextID = new ConcurrentHashMap<>();

    static void deliver(MessageSource ms) {

        messages.add(ms);
        senderNextID.putIfAbsent(ms.getSourceID(), 1);

        while(true) {
            ms = new MessageSource(ms.getSourceID(), senderNextID.get(ms.getSourceID()));
            if (messages.remove(ms)) {
                OutputLogger.writeLog("d " + ms.getSourceID() + " " + ms.getMessageID());
                senderNextID.computeIfPresent(ms.getSourceID(), (key, value) -> value+1);
            } else {
                break;
            }
        }
    }
}
