public class ProcessData {

    private int id;

    public int getId() {
        return id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    private String ipAddress;
    private int port;

    public ProcessData(int id, String ipAddress, int port) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }
}