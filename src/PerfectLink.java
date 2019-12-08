import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

// Perfect Link abstraction
public class PerfectLink implements Runnable {

    // ackMessages for PL
    private static ConcurrentMap<String, Boolean> ackMessages = new ConcurrentHashMap<>();

    // delivered messages by URB
    private static ConcurrentMap<MessageData, Boolean> delivered = new ConcurrentHashMap<>();

    // receiving queue for incoming messages
    public static ConcurrentLinkedQueue<MessageData> receivingQueue = new ConcurrentLinkedQueue<>();

    // Sender instance to send messages and acknowledgement
    private static Sender sender;

    static {
        try {
            sender = new Sender();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ConcurrentLinkedQueue<MessageData> getReceivingQueue() {
        return receivingQueue;
    }

    @Override
    // Process incoming packets on a different thread until termination signal
    public void run() {
    	
    	while(Da_proc.isRunning()) {
            MessageData message;
            // Get head of the queue and process it
            while ((message = receivingQueue.poll()) != null) {
                // If the message is an ack, I store it so that I can notify the sender part and don't need to send it again
                if (message.isAck()) {
                    ackMessages.putIfAbsent(message.toString(), false);
                } else {
                    // If the message is not an ack, we first need to send an ack to who sent the message
                    MessageData ackMessage = new MessageData(message.getSourceID(), Da_proc.getId(), message.getSenderID(), message.getMessageID(), true, Da_proc.getVectorClockSend());
                    try {
                        sender.send(ackMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Then process the message and send it to URB
                    if (delivered.putIfAbsent(message, true) == null) {
                        URBroadcast.deliver(message);
                    }
                }
            }

        }
    }

    // Send the message until the message is not acknowledged, if there's no ack just put it at the tail of the queue so that we can continue sending other messages
    public static void send(MessageData message) {
        MessageData ackMessage = new MessageData(message.getSourceID(), message.getReceiverID(), message.getSenderID(), message.getMessageID(), true, Da_proc.getVectorClockSend());

        // Check if an ack has already arrived for this message, if not send the packet
        if (!ackMessages.containsKey(ackMessage.toString())) {
            try {
                sender.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Since the message was not already acknowledged, we put it at the end of the queue
            URBroadcast.getSendingQueue().add(message);
        }
    }
}