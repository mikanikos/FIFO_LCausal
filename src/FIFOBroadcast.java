import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FIFOBroadcast {

    private static ConcurrentMap<MessageSource, Boolean> messages = new ConcurrentHashMap<>();;
    private static ConcurrentMap<Integer, AtomicInteger> senderNextID = new ConcurrentHashMap<>();

//    public FIFOBroadcast() {
//        messages =
//        senderNextID = new ConcurrentHashMap<>();
//    }

    static void deliver(MessageSource ms) {

        messages.put(ms, true);
        senderNextID.putIfAbsent(ms.getSourceID(), new AtomicInteger(1));

//        boolean delivered;
//        do {
//            delivered = false;
//            for (MessageSource m : messages.keySet()) {
//                AtomicInteger nextID = senderNextID.get(m.getSourceID());
//                if (m.getMessageID() == nextID.get() && m.getSourceID() == m.getSourceID()) {
//                    //System.out.println("Delivered");
//                    delivered = true;
//                    OutputLogger.writeLog("d " + m.getSourceID() + " " + m.getMessageID());
//                    senderNextID.computeIfPresent(m.getSourceID(), (key, value) -> new AtomicInteger(value.incrementAndGet()));
//                    messages.remove(m);
//                }
//            }
//        } while (delivered);

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
