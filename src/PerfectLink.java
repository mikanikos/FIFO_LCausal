import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PerfectLink implements Runnable {

    private MessageData message;
    //private boolean isSending;

    private static ConcurrentMap<String, Boolean> ackMessages = new ConcurrentHashMap<>();
    private static ConcurrentMap<MessageData, Boolean> delivered = new ConcurrentHashMap<>();
    private static ConcurrentMap<Integer, Integer> lastTimeElapsed = new ConcurrentHashMap<>();
    private static Sender sender;

    static {
        try {
            sender = new Sender();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public PerfectLink(MessageData message) {
        this.message = message;
        //this.isSending = isSending;
    }

    public void run() {
//        if (isSending) {
//            send();
//        } else {
            try {
                receive();
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        //}
    }

    static void send(MessageData message) {
        MessageData messageCopy = new MessageData(message.getSourceID(), message.getReceiverID(), message.getSenderID(), message.getMessageID(), true);
//        Sender sender = null;
//        try {
//            sender = new Sender(message);
//        } catch (SocketException | UnknownHostException e) {
//            e.printStackTrace();
//        }

        //lastTimeElapsed.putIfAbsent(message.getReceiverID(), 1);

        //Timer timer = new Timer(lastTimeElapsed.get(message.getReceiverID()));
        //int count = 0;
        //timer.start();
        //long start = System.currentTimeMillis();

        if (!ackMessages.containsKey(messageCopy.toString())) {
            try {
                sender.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
            URBroadcast.processQueue.add(message);
        }

//        while (!ackMessages.containsKey(messageCopy.toString()) && !timer.isExpired()) {
//            try {
//                sender.send();
//                count++;
//                Thread.sleep(0, 10000);
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        System.out.println("Sent times: " + count);
//
//        if (timer.isExpired()) {
//            lastTimeElapsed.computeIfPresent(message.getReceiverID(), (key, value) -> value + 10);
//            System.out.println("Timer expired " + message.toString());
//
//            //Da_proc.getProcesses().get(message.getReceiverID()).getSenderInstance().getProcessQueue().add(message);
//        } else {
//            long end = System.currentTimeMillis();
//            System.out.println("Got it in time in " + lastTimeElapsed.get(message.getReceiverID()));
//            lastTimeElapsed.computeIfPresent(message.getReceiverID(), (key, value) -> Math.toIntExact(end-start));
//        }
//        System.out.println("Timer for " + lastTimeElapsed.get(message.getReceiverID()));
    }

    void receive() throws SocketException, UnknownHostException {

        if (message.isAck()) {
            ackMessages.putIfAbsent(message.toString(), false);
        } else {

            MessageData ackMessage = new MessageData(message.getSourceID(), Da_proc.getId(), message.getSenderID(), message.getMessageID(), true);
            //Sender sender = new Sender(ackMessage);
            sender.send(ackMessage);
            //sender.close();

            if (delivered.putIfAbsent(message, true) == null && message.getSourceID() != Da_proc.getId()) {
                URBroadcast.deliver(message);
            }
        }
    }

}
