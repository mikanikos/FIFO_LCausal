class ProcessData {

    private int id;

    int getId() {
        return id;
    }

    String getIpAddress() {
        return ipAddress;
    }

    private String ipAddress;
    private int port;

    ProcessData(int id, String ipAddress, int port) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    int getPort() {
        return this.port;
    }
}