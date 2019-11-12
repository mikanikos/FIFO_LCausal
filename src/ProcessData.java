import java.util.ArrayList;
import java.util.List;

// Process data
public class ProcessData {

    // process id
    private int id;
    // ip address where it received
    private String ipAddress;
    // port where it received
    private int port;
    // dependencies
    private List<Integer> dependencies;

    public int getId() {
        return id;
    }
    
    public List<Integer> getDependencies() {
        return dependencies;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return this.port;
    }

    public ProcessData(int id, String ipAddress, int port) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.port = port;
        this.dependencies = new ArrayList<Integer>();
    }

}