import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Perfect_Sender extends Thread {

    private ProcessData process;
    private MessageData message;
    private Timer timer;

    private static ConcurrentMap<MessageData, Boolean> acked = new ConcurrentHashMap<>();

    public static ConcurrentMap<MessageData, Boolean> getAcked() {
        return acked;
    }

    public Perfect_Sender(ProcessData process, MessageData message) {
        this.process = process;
        this.message = message;
        this.timer = new Timer(10000);
        this.start();
    }

    public void run() {
        try {
            UDP_Sender udpSender = new UDP_Sender(this.process, this.message);
            timer.start();
            while(!acked.containsKey(message) && !timer.isExpired()) {
                udpSender.run();
                Thread.sleep(1000);
            }
            if (timer.isExpired())
                System.out.println("Timer expired for " + message.toString());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
