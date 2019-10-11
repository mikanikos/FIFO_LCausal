import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Perfect_Receiver extends Thread {

    private static ConcurrentMap<MessageData, Boolean> delivered = new ConcurrentHashMap<>();
    private String message;

    public Perfect_Receiver(String message) {
        this.message = message;
        this.start();
    }

    public void run() {
        String[] parsedMessage = message.split(" ");
        int senderID = Integer.valueOf(parsedMessage[0]);
        int receiverID = Integer.valueOf(parsedMessage[1]);
        int seqID = Integer.valueOf(parsedMessage[2]);
        boolean isAck = Boolean.valueOf(parsedMessage[3]);

        MessageData m = new MessageData(senderID, receiverID, seqID, isAck);

        if (m.isAck()) {
            Perfect_Sender.getAcked().putIfAbsent(m, false);
        }
        else {
            if (delivered.putIfAbsent(m, true) == null) {
                OutputLogger.writeLog("d " + senderID + " " + seqID);
            }
            // sending ack even if already deliver it because sender could not have received it
            MessageData messageAck = new MessageData(m.getSenderID(), m.getReceiverID(), m.getMessageID(), true);
            ProcessData p = Da_proc.processes.get(senderID);
            try {
                UDP_Sender ackSender = new UDP_Sender(p, messageAck);
                ackSender.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
