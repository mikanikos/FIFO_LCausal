import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LCausalBroadcast {
	
	// messages delivered by URB
    private static ConcurrentMap<MessageSource, Boolean> messages = new ConcurrentHashMap<>();;

    // map <Process id> <Next ID expected to be delivered according to FIFO>
    private static ConcurrentMap<Integer, AtomicInteger> senderNextID = new ConcurrentHashMap<>();

    // Deliver message for LCausal protocol
    public static void deliver(MessageSource ms) {

        // Store the message
        messages.put(ms, true);

        // Initialize first next id expected if not present
        senderNextID.putIfAbsent(ms.getSourceID(), new AtomicInteger(1));

        while(true) {
            ms = new MessageSource(ms.getSourceID(), senderNextID.get(ms.getSourceID()).get());
            
            if (Da_proc.getProcesses().get(ms.getSourceID()) == null) {
            	FIFOBroadcast.deliver(ms);
            }
            else {
            	System.out.println("LCausal broadcast.");
            }
        }
    }
	
}
