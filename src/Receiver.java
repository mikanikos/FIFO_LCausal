import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

public class Receiver implements Runnable {

    private DatagramSocket socket;
    private byte[] buffer = new byte[60000];

    Receiver(int port) throws IOException {
        this.socket = new DatagramSocket(port);
    }

    @Override
    // Listening for incoming packets on the specified socket port
    public void run() {
        try {
            while (Da_proc.isRunning()) {
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

                // receive packet
                socket.receive(receivePacket);

                // get packet
                String packet = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // parse message
                MessageData message = MessageData.parseMessage(packet);

                // send the message to the receiving queue in order to process it
                PerfectLink.getReceivingQueue().add(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}