import java.io.IOException;
import java.net.DatagramPacket;

public class Perfect_Sender {

    private int port;
    private String ipAddress;
    private String message;
    Timer timer = new Timer(10000);

    public Perfect_Sender(int port, String ipAddress, String message) {
        this.port = port;
        this.ipAddress = ipAddress;
        this.message = message;
    }

    public void send(String payload) throws IOException {
        UDP_Sender udpSender = new UDP_Sender(ipAddress, port, message);
        while(!timer.isExpired()) {
            udpSender.send();
        }

    }

//    public boolean acknowledgement() throws Exception {
//        boolean acknowledgement = false;
//
//
//        return acknowledgement;
//    }
}
