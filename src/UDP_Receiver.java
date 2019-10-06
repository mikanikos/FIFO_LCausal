import java.io.IOException;
import java.net.*;

public class UDP_Receiver extends Thread {

    public void setListening(boolean listening) {
        UDP_Receiver.listening = listening;
    }

    private DatagramSocket socket;
    private static boolean listening = true;
    private byte[] buffer = new byte[16];

    public UDP_Receiver(Da_proc main_instance) throws IOException {
        this.socket = new DatagramSocket(main_instance.getProcesses().get(main_instance.getId()).getPort());
        this.start();
    }

    public void run() {

        while (listening) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                System.out.println("Packet received");
                OutputLogger.writeLog("d " + new String(packet.getData(), 0, packet.getLength()));
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
}
