import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;

public class PerfectLink implements Runnable {

    //private MessageData message;

    private static ConcurrentMap<String, Boolean> ackMessages = new ConcurrentHashMap<>();
    private static Set<MessageData> delivered = new HashSet<>();
    //public static Queue<MessageData> messages = new ConcurrentLinkedQueue<>();
    public static PriorityBlockingQueue<MessageData> messages = new PriorityBlockingQueue<>(Da_proc.getNumMessages(), Comparator.comparingInt(MessageData::getMessageID));
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

    public void run() {

        while(Da_proc.isRunning()) {
            MessageData message;
            while ((message = messages.poll()) != null) {
                if (message.isAck()) {
                    ackMessages.putIfAbsent(message.toString(), false);
                } else {

                    try {
                        sender.send(new MessageData(message.getSourceID(), Da_proc.getId(), message.getSenderID(), message.getMessageID(), true));
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                    if (!delivered.contains(message)) {
                        delivered.add(message);
                        URBroadcast.deliver(message);
                    }
                }
            }
            //new Thread(new PerfectLink(m)).start();
        }

    }

    static void send(MessageData message) {
        MessageData messageCopy = new MessageData(message.getSourceID(), message.getReceiverID(), message.getSenderID(), message.getMessageID(), true);

        //System.out.println(message.toString());
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