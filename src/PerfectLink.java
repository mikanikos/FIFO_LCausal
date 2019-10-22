import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PerfectLink implements Runnable {

    private final Timer timer = new Timer(10000);
    private static Sender sender;

    static {
        try {
            sender = new Sender();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private MessageData message;

    private static ConcurrentMap<String, Boolean> ackMessages = new ConcurrentHashMap<>();
    private static ConcurrentMap<MessageData, Boolean> delivered = new ConcurrentHashMap<>();
    private static URBroadcast urb = new URBroadcast();


    public PerfectLink(MessageData message) {
        this.message = message;
    }

    // threaded send
    public void run() {
        MessageData messageCopy = new MessageData(message.getSourceID(), message.getReceiverID(), message.getSenderID(), message.getMessageID(), true);

        timer.start();
        while (!ackMessages.containsKey(messageCopy.toString()) && !timer.isExpired()){
            try {
                sender.send(message);
                Thread.sleep(1);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (timer.isExpired()) {
            System.out.println("Timer expired " + message.toString());
        }
    }

    void receive() {

        if (message.isAck()) {
            ackMessages.putIfAbsent(message.toString(), false);
        } else {
            if (delivered.putIfAbsent(message, true) == null) {
                //OutputLogger.writeLog("d " + message.getSourceID() + " " + message.getMessageID());
                urb.deliver(message);
            }
            MessageData ackMessage = new MessageData(message.getSourceID(), Da_proc.getId(), message.getSenderID(), message.getMessageID(), true);
            try {
                sender.send(ackMessage);
                //sender.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
