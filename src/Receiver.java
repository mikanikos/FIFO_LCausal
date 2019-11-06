import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Receiver implements Runnable {

    private DatagramSocket socket;
    private byte[] buffer = new byte[16];

    Receiver(int port) throws IOException {
        this.socket = new DatagramSocket(port);
    }

    public void run() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            while (Da_proc.isRunning()) {
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);
                String packet = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // parse message
                MessageData message = MessageData.parseMessage(packet);
                Runnable worker = new PerfectLink(message);
                executor.execute(worker);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();

            System.out.println("Killing all threads");
            executor.shutdown();

            PerfectLink.closeSendingSocket();
        }
    }
}