import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Perfect_Sender extends Thread {

    private ProcessData process;
    private MessageData message;
    private Timer timer;

    private static List<MessageData> acked;
    private static Lock ackedLock;

    public static Lock getAckedLock() {
        return ackedLock;
    }

    public static List<MessageData> getAcked() {
        return acked;
    }

    public Perfect_Sender(ProcessData process, MessageData message) {
        this.process = process;
        this.message = message;
        this.timer = new Timer(10000);
        acked = new CopyOnWriteArrayList<>();
        ackedLock = new ReentrantLock();
        this.start();
    }

    public void run() {
        try {
            UDP_Sender udpSender = new UDP_Sender(this.process, this.message);
            timer.start();
            List<MessageData> ackedCopy = getAtomicDelivered();
            while(!timer.isExpired() && !acked.contains(this.message)) {
                udpSender.run();
                Thread.sleep(1000);
                ackedCopy = getAtomicDelivered();
            }
            if (timer.isExpired())
                System.out.println("Timer expired");
            else
                System.out.println("Ack received");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<MessageData> getAtomicDelivered() {
        return getMessageListAtomic(ackedLock, acked);
    }

    public static List<MessageData> getMessageListAtomic(Lock lock, List<MessageData> messageList) {
        lock.lock();
        List synchronizedList = Collections.synchronizedList(messageList);
        List<MessageData> listCopy = new ArrayList<>();
        synchronized (synchronizedList) {
            Iterator i = synchronizedList.iterator(); // Must be in synchronized block
            while (i.hasNext()) {
                MessageData m = (MessageData) i.next();
                MessageData messageCopy = new MessageData(m.getSenderID(), m.getMessageID(), m.isAck());
                listCopy.add(messageCopy);
            }
        }

        //        for (MessageData m : synchronizedList) {
//            MessageData messageCopy = new MessageData(m.getSenderID(), m.getMessageID(), m.isAck());
//            listCopy.add(messageCopy);
//        }
        lock.unlock();
        return listCopy;
    }

}
