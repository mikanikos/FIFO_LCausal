import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Da_proc {

    // Id of the current process
    private static int id;
    // Number of processes (from membership file)
    private static int numProcesses;
    // Number of messages (from parameter)
    private static int numMessages;
    // Map for storing all processes information
    private static ConcurrentMap<Integer, ProcessData> processes;
    // Use boolean value to set packet processing status
    private static boolean running = true;
    // vector clock
    private static ConcurrentMap<Integer, AtomicInteger> vectorClock;

    public static ConcurrentMap<Integer, AtomicInteger> getVectorClock() {
        return vectorClock;
    }

    public static int getNumProcesses() { return numProcesses; }

    public static boolean isRunning() {
        return running;
    }

    public static void stopRunning() {
        running = false;
    }

    public static int getId() {
        return id;
    }

    public static int getNumMessages() {
        return numMessages;
    }

    public static ConcurrentMap<Integer, ProcessData> getProcesses() {
        return processes;
    }

    public Da_proc() {
        vectorClock = new ConcurrentHashMap<>();
        processes = new ConcurrentHashMap<>();
        // set up signal handlers
        new SignalHandlerUtility();
    }

    public static void main(String[] args) throws InterruptedException, IOException {

        Da_proc main_instance = new Da_proc();

        System.out.println("Initializing");

        // parse arguments, including membership
        id = Integer.parseInt(args[0]);
    	main_instance.parse_membership(args[1]);
        numMessages = Integer.parseInt(args[2]);

        // initialize vector clock
        for (Integer id : Da_proc.getProcesses().keySet()) {
            vectorClock.put(id, new AtomicInteger(0));
        }

        // start threads for incoming UDP packets
        int myPort = processes.get(id).getPort();
        new Thread(new Receiver(myPort)).start();

        // start thread for receiving queue
        new Thread(new URBroadcast()).start();

        // start thread for sending queue
        new Thread(new PerfectLink()).start();

        new LCausalBroadcast();

        // wait user signal to start broadcasting
        while (SignalHandlerUtility.wait_for_start) {
            Thread.sleep(100);
        }

        // start broadcast
        System.out.println("Broadcasting " + numMessages + " messages");
        for (int seq_nr = 1; seq_nr <= Da_proc.getNumMessages() && running; seq_nr++) {

            ConcurrentMap<Integer, AtomicInteger> copy = copyVectorClock(vectorClock);
            copy.put(Da_proc.getId(), new AtomicInteger(seq_nr-1));
            URBroadcast.broadcast(Da_proc.getId(), seq_nr, copy);

            // write broadcast message to the output file
            OutputLogger.writeLog("b " + seq_nr);
        }

        // Waiting to stop
        while (true) {
            Thread.sleep(1000);
        }
    }


    public static ConcurrentMap<Integer, AtomicInteger> copyVectorClock(ConcurrentMap<Integer, AtomicInteger> original) {
        ConcurrentMap<Integer, AtomicInteger> copy = new ConcurrentHashMap<>();
        for (Map.Entry<Integer, AtomicInteger> entry : original.entrySet()) {
            copy.put(entry.getKey(), new AtomicInteger(entry.getValue().get()));
        }
        return copy;
    }

    // parse input membership file
    private void parse_membership(String filename) {
        String line = "";
        BufferedReader buffer = null;

        try {
            buffer = new BufferedReader(new FileReader(filename));
            int counter = 0;
            
            while ((line = buffer.readLine()) != null) {
            	
                String[] fields = line.split(" ");
                if (fields.length != 0) {

	                if (counter == 0) {
	                    numProcesses = Integer.parseInt(fields[0]);
	                }
	                
	                if (0 < counter && counter <= numProcesses) {
	                	processes.put(Integer.parseInt(fields[0]), new ProcessData(Integer.parseInt(fields[0]), fields[1], Integer.parseInt(fields[2])));
	                }
	                
	                if (counter > numProcesses) {
	                	for (int i = 1; i < fields.length; i++) {
	                		processes.get(Integer.parseInt(fields[0])).getDependencies().add(Integer.parseInt(fields[i]));
	                	}
	                }
	                counter++;
        		}
                
            }

        } catch (IOException e) {
            System.out.println("Error when parsing the membership file");
        } finally {
            try {
                assert buffer != null;
                buffer.close();
            } catch (IOException e) {
                System.out.println("Error when closing the buffer");
            }
        }
    }
}