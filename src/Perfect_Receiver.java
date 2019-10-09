import com.sun.xml.internal.ws.api.message.Message;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Perfect_Receiver extends Thread {

    private static List<MessageData> delivered;
    private static Lock deliveredLock;
    private String message;

    public Perfect_Receiver(String message) {
        this.message = message;
        delivered = new CopyOnWriteArrayList<>();
        deliveredLock = new ReentrantLock();
        this.start();
    }

    public void run() {
        String[] parsedMessage = message.split(" ");
        int senderID = Integer.valueOf(parsedMessage[0]);
        int seqID = Integer.valueOf(parsedMessage[1]);
        boolean isAck = Boolean.valueOf(parsedMessage[2]);

        MessageData m = new MessageData(senderID, seqID, isAck);
        List<MessageData> deliveredCopy = getAtomicDelivered();

        if (m.isAck()) {
            Perfect_Sender.getAckedLock().lock();
            Perfect_Sender.getAcked().add(m);
            Perfect_Sender.getAckedLock().unlock();
        }
        else {
            if (!delivered.contains(m)) {

                // log output

                OutputLogger.getLoggerLock().lock();
                OutputLogger.writeLog("d " + senderID + " " + seqID);
                OutputLogger.getLoggerLock().unlock();

                delivered.add(m);

                // sending ack
                MessageData messageAck = new MessageData(m.getSenderID(), m.getMessageID(), true);
                ProcessData p = Da_proc.processes.get(senderID);
                try {
                    UDP_Sender ackSender = new UDP_Sender(p,messageAck);
                    ackSender.run();
                    //new UDP_Sender(p, m);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<MessageData> getAtomicDelivered() {
        return Perfect_Sender.getMessageListAtomic(deliveredLock, delivered);
    }
}
