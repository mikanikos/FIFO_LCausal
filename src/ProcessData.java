import java.net.SocketException;

class ProcessData {

    private int id;
    private String ipAddress;
    private int port;

    int getId() {
        return id;
    }

    String getIpAddress() {
        return ipAddress;
    }
    
    int getPort() {
        return this.port;
    }

    ProcessData(int id, String ipAddress, int port) throws SocketException {
        this.id = id;
        this.ipAddress = ipAddress;
        this.port = port;
    }

}