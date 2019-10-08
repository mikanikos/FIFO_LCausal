import java.io.IOException;
import java.net.*;

public class UDP_Sender {

    private DatagramSocket socket;
    private DatagramPacket packet;


    public UDP_Sender(String ipAddress, int port, String message) throws IOException {
        InetAddress address = InetAddress.getByName(ipAddress);
        this.socket = new DatagramSocket();
		byte[] buffer = message.getBytes();
		this.packet = new DatagramPacket(buffer, buffer.length, address, port);

	}

    public void send() {
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
