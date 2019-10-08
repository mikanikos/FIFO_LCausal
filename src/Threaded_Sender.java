public class Threaded_Sender extends Thread {

    private String message;
    private String ipAddress;
    private int port;

    public Threaded_Sender(String message, String ipAddress, int port) {
        this.message = message;
        this.ipAddress = ipAddress;
        this.port = port;
        this.run();
    }

    public void run() {

    }
}
