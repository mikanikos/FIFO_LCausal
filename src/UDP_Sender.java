import java.io.IOException;
import java.net.*;

public class UDP_Sender extends Thread {

    private DatagramSocket socket;
    private DatagramPacket packet;

    public UDP_Sender(ProcessData process, MessageData message) throws IOException {
        this.socket = new DatagramSocket();
        byte[] buffer = message.toString().getBytes();
		this.packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(process.getIpAddress()), process.getPort());
    }

    public void run() {
    	try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
