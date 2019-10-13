import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PerfectLink implements Runnable {

    private final Timer timer = new Timer(10000);
    private Sender sender;
    private MessageData message;

    private static ConcurrentMap<String, Boolean> ackMessages = new ConcurrentHashMap<>();
    private static ConcurrentMap<MessageData, Boolean> delivered = new ConcurrentHashMap<>();


    public PerfectLink(MessageData message) throws IOException {
        this.message = message;
        this.sender = new Sender();
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

    public void receive() {

        if (message.isAck()) {
            ackMessages.putIfAbsent(message.toString(), false);
        } else {
            if (delivered.putIfAbsent(message, true) == null) {
                //OutputLogger.writeLog("d " + message.getSourceID() + " " + message.getMessageID());
                new URBroadcast().deliver(message);
            }
            MessageData ackMessage = new MessageData(message.getSourceID(), Da_proc.getId(), message.getSenderID(), message.getMessageID(), true);
            try {
                sender.send(ackMessage);
                sender.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
