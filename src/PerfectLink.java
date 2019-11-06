import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PerfectLink implements Runnable {

    private MessageData message;

    private static ConcurrentMap<String, Boolean> ackMessages = new ConcurrentHashMap<>();
    private static ConcurrentMap<MessageData, Boolean> delivered = new ConcurrentHashMap<>();
    private static Sender sender;

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

    public PerfectLink(MessageData message) {
        this.message = message;
    }

    public static long count = 0;
    public static long time = 0;

    public void run() {

        long start = System.nanoTime();

        if (message.isAck()) {
            ackMessages.putIfAbsent(message.toString(), false);
        } else {

            MessageData ackMessage = new MessageData(message.getSourceID(), Da_proc.getId(), message.getSenderID(), message.getMessageID(), true);
            try {
                sender.send(ackMessage);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            if (delivered.putIfAbsent(message, true) == null) {
                URBroadcast.deliver(message);
            }
        }

        long end = System.nanoTime();
        time += (end - start);
        count++;
    }

    static void send(MessageData message) {
        MessageData messageCopy = new MessageData(message.getSourceID(), message.getReceiverID(), message.getSenderID(), message.getMessageID(), true);

        if (!ackMessages.containsKey(messageCopy.toString())) {
            try {
                sender.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
            URBroadcast.processQueue.add(message);
        }
    }
}