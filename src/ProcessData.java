import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

class ProcessData {

    private int id;
    private String ipAddress;
    private int port;
    private Sender sender;
    private ConcurrentLinkedQueue<MessageData> processQueue;

    ConcurrentLinkedQueue<MessageData> getProcessQueue() {
        return processQueue;
    }
    int getId() {
        return id;
    }
    String getIpAddress() {
        return ipAddress;
    }
    int getPort() {
        return this.port;
    }
    Sender getSenderInstance() { return this.sender; }


    ProcessData(int id, String ipAddress, int port) throws SocketException {
        this.id = id;
        this.ipAddress = ipAddress;
        this.port = port;
        this.sender = new Sender();
        this.processQueue = new ConcurrentLinkedQueue<>();
    }
}