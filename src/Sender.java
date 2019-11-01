import java.io.IOException;
import java.net.*;

public class Sender {

    private DatagramSocket socket;
    private DatagramPacket packet;

    Sender(MessageData message) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        byte[] buffer = message.toString().getBytes();
        ProcessData process = Da_proc.getProcesses().get(message.getReceiverID());
        this.packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(process.getIpAddress()), process.getPort());
    }

    public void send() throws UnknownHostException {

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
