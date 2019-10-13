import java.io.IOException;
import java.net.*;

public class Sender {

    private DatagramSocket socket;

    Sender() throws IOException {
        this.socket = new DatagramSocket();
    }

    public void send(MessageData message) throws UnknownHostException {
        byte[] buffer = message.toString().getBytes();
        ProcessData process = Da_proc.getProcesses().get(message.getReceiverID());
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(process.getIpAddress()), process.getPort());
    	try {
            socket.send(packet);
            //System.out.println("Packet sent");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        socket.close();
    }
}
