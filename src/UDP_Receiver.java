import java.io.IOException;
import java.net.*;

public class UDP_Receiver extends Thread {

    private DatagramSocket socket;
    private Da_proc main_instance;
    private static boolean listen = true;
    private byte[] buffer = new byte[16];

    public void setListen(boolean listen) {
        this.listen = listen;
    }

    public UDP_Receiver(Da_proc main_instance) throws IOException {
        this.socket = new DatagramSocket(main_instance.getProcesses().get(main_instance.getId()).getPort());
        this.main_instance = main_instance;
        this.start();
    }

    public void run() {

        while (listen) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                System.out.println("Packet received");
                Da_proc.logger.info("d " + new String(packet.getData(), 0, packet.getLength()));
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
}
