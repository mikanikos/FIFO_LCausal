import java.io.IOException;
import java.net.*;

public class UDP_Receiver extends Thread {

    public void setListening(boolean listening) {
        UDP_Receiver.listening = listening;
    }

    private DatagramSocket socket;
    private static boolean listening = true;
    private byte[] buffer = new byte[16];

    public UDP_Receiver(int port) throws IOException {
        this.socket = new DatagramSocket(port);
        this.start();
    }

    public void run() {
        try {
            while (listening) {
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);
                System.out.println("Packet received");
                String packet = new String(receivePacket.getData(), 0, receivePacket.getLength());

                new Perfect_Receiver(packet);

                //perfect_receiver.deliver(packet);

                // Acknowledgement packet back to sender
//                InetAddress IPAddress = receivePacket.getAddress();
//                DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, IPAddress, receivePacket.getPort());
//                socket.send(sendPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
