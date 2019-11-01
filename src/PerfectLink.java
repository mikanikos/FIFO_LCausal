import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PerfectLink {

    private final Timer timer = new Timer(1000);
//    private static Sender sender;
//
//    static {
//        try {
//            sender = new Sender();
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
//    }

    private MessageData message;

    private static ConcurrentMap<String, Boolean> ackMessages = new ConcurrentHashMap<>();
    private static ConcurrentMap<MessageData, Boolean> delivered = new ConcurrentHashMap<>();
    //private static URBroadcast urb = new URBroadcast();

//
//    public PerfectLink(MessageData message) {
//        this.message = message;
//    }

    // threaded send
    public void send(MessageData message) throws SocketException, UnknownHostException {
        MessageData messageCopy = new MessageData(message.getSourceID(), message.getReceiverID(), message.getSenderID(), message.getMessageID(), true);
        Sender sender = new Sender(message);

        timer.start();
        //long start = System.currentTimeMillis();
        while (!ackMessages.containsKey(messageCopy.toString()) && !timer.isExpired()){
            try {
                sender.send();
                Thread.sleep(0, 10000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        //long end = System.currentTimeMillis();
        //System.out.println(end-start);

        if (timer.isExpired()) {
            //System.out.println("Timer expired " + message.toString());
            Da_proc.getProcesses().get(message.getReceiverID()).getSenderInstance().getProcessQueue().add(message);
        }
    }

    static void receive(MessageData message) {

        if (message.isAck()) {
            ackMessages.putIfAbsent(message.toString(), false);
        } else {
            if (delivered.putIfAbsent(message, true) == null) {
                //OutputLogger.writeLog("d " + message.getSourceID() + " " + message.getMessageID());
                URBroadcast.deliver(message);
            }
            MessageData ackMessage = new MessageData(message.getSourceID(), Da_proc.getId(), message.getSenderID(), message.getMessageID(), true);
            try {
                Sender sender = new Sender(ackMessage);
                sender.send();
                //sender.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
