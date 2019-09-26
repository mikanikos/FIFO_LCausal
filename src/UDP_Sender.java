import java.io.IOException;
import java.net.*;

public class UDP_Sender {

    private DatagramSocket socket;
    private InetAddress address;
    private int port;

    public UDP_Sender(String ipAddress, int port) throws IOException {
        this.address = InetAddress.getByName(ipAddress);
        this.socket = new DatagramSocket();
        this.port = port;
    }

    public void send(String payload) {
        System.out.println("Sending packet");
        byte[] buffer = payload.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);

        try {
            socket.send(packet);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            socket.close();
        }
    }
}
