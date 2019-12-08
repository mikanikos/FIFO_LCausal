import java.io.IOException;
import java.net.*;

public class Sender {

    private DatagramSocket socket;

    // Initialize socket
    public Sender() throws IOException {
        this.socket = new DatagramSocket();
    }

    // Send UDP packet
    public void send(MessageData message) throws IOException {

        byte[] buffer = message.toString().getBytes();
        ProcessData process = Da_proc.getProcesses().get(message.getReceiverID());
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(process.getIpAddress()), process.getPort());

    	try {
    	    socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
