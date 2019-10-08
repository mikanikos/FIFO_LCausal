import java.io.IOException;
import java.net.*;

public class UDP_Sender {
	
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    Timer timer = new Timer(10000);

    public UDP_Sender(String ipAddress, int port) throws IOException {
        this.address = InetAddress.getByName(ipAddress);
        this.socket = new DatagramSocket();
        this.port = port;
    }

    public void send(String payload) {
        System.out.println("Sending packet");
        byte[] buffer = payload.getBytes();
        
        DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, address, port);
        while(!timer.isExpired()) {
	        try {
	    		socket.send(sendPacket);
	        }
	        catch(IOException e) {
	            e.printStackTrace();
	        }
	        finally {
	            socket.close();
	        }
        }
    }
    
    public boolean acknowledgement() throws Exception {
    	boolean acknowledgement = false;
    	byte[] buffer = new byte[16];
		DatagramPacket recievePacket = new DatagramPacket(buffer, buffer.length);
		while(!timer.isExpired()) {
			try {
				socket.receive(recievePacket);
				acknowledgement = true;
			} 
			catch(IOException e) {
				acknowledgement = false;
            }
		}
		return acknowledgement;
	}
}
