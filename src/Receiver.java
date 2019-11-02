import java.io.IOException;
import java.net.*;

public class Receiver implements Runnable {

    private DatagramSocket socket;
    private byte[] buffer = new byte[16];

    Receiver(int port) throws IOException {
        this.socket = new DatagramSocket(port);
    }

    public void run() {
        try {
            while (Da_proc.isRunning()) {
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);
                //System.out.println("Packet received");
                String packet = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // parse message
                MessageData message = MessageData.parseMessage(packet);
//                pl = new PerfectLink();
                new Thread(new PerfectLink(message)).start();
                //PerfectLink.receive(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
