import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class PerfectLink implements Runnable {

    private static ConcurrentMap<String, Boolean> ackMessages = new ConcurrentHashMap<>();
    private static Set<MessageData> delivered = new HashSet<>();
    public static Queue<MessageData> messages = new ConcurrentLinkedQueue<>();
    //public static PriorityBlockingQueue<MessageData> messages = new PriorityBlockingQueue<>(Da_proc.getNumMessages(), Comparator.comparingInt(MessageData::getMessageID));
    private static Sender sender;

    // USING IT DESPITE POSSIBLE EXCEPTION?????????
    public static void closeSendingSocket() {
        sender.getSocket().close();
    }

    static {
        try {
            sender = new Sender();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        while (Da_proc.isRunning()) {
            MessageData message;
            while ((message = messages.poll()) != null) {
                if (message.isAck()) {
                    MessageData finalMessage = message;
                    PerfectLink.messages.removeIf(messageData -> {
                        if (finalMessage.getSourceID() == messageData.getSourceID() && finalMessage.getSenderID() == messageData.getReceiverID() && messageData.getSenderID() == Da_proc.getId() && finalMessage.getReceiverID() == Da_proc.getId() && messageData.getMessageID() == finalMessage.getMessageID())
                            return true;
                        return false;
                    });
                } else {


                    MessageData ackMessage = new MessageData(message.getSourceID(), Da_proc.getId(), message.getSenderID(), message.getMessageID(), true);
                    try {
                        sender.send(ackMessage);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                    // AFTER OR BEFORE SENDING AN ACK??????
                    if (!delivered.contains(message)) {
                        delivered.add(message);
                        URBroadcast.deliver(message);
                    }
                }
            }
        }
    }

    static void send(MessageData message) {

        try {
            sender.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}