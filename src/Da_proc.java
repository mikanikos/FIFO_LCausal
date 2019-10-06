import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Da_proc {

    public int getId() {
        return id;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        Da_proc.running = running;
    }

    public Map<Integer, Process> getProcesses() {
        return processes;
    }

    private int id;
    private int numProcesses;
    private int numMessages;
    private Map<Integer, Process> processes;
    public static UDP_Receiver receiver;
    private static boolean running = true;

    public Da_proc() {
        this.processes = new HashMap<>();
        // set signal handlers
        new ProcessModel();
    }

    @SuppressWarnings("static-access")
	public static void main(String[] args) throws InterruptedException, IOException {

        Da_proc main_instance = new Da_proc();

        System.out.println("Initializing");

        // parse arguments, including membership
        main_instance.id = Integer.parseInt(args[0]);
        main_instance.parse_membership(args[1]);
        
        // using java logging to output log file
        OutputLogger outputLogger = new OutputLogger(main_instance.id);

        // start listening for incoming UDP packets
        receiver = new UDP_Receiver(main_instance);
        while(ProcessModel.wait_for_start) {
            Thread.sleep(1000);
        }

        System.out.println("Broadcasting messages");

        // not sending to myself
        main_instance.processes.remove(main_instance.id);

        // start broadcast
        UDP_Sender sender;
        int seq_nr;
        for (seq_nr = 1; seq_nr <= main_instance.numMessages && running; seq_nr++) {
            for (Process  p : main_instance.processes.values()) {            	
                sender = new UDP_Sender(p.ipAddress, p.port);
                // Keep on sending the message until the acknowledgement is received
                try {
					while(!sender.acknowledgement()) {
						sender.send(main_instance.id + " " + seq_nr);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
            // handle the output of processes
            outputLogger.writeLog("b " + seq_nr);
        }

        System.out.println("Done");

        while(true) {
            Thread.sleep(1000);
        }
    }

    private void parse_membership(String filename) {
        String line = "";
        BufferedReader buffer = null;

        try {
            buffer = new BufferedReader(new FileReader(filename));

            while ((line = buffer.readLine()) != null) {

                String[] fields = line.split(" ");

                if (fields.length != 0) {

                    if (fields.length == 1) {
                        if (numProcesses != 0) {
                            numMessages = Integer.parseInt(fields[0]);
                        }
                        else {
                            numProcesses = Integer.parseInt(fields[0]);
                        }
                    }
                    else {
                        processes.put(Integer.parseInt(fields[0]), new Process(fields[1], Integer.parseInt(fields[2])));
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                buffer.close();
            }
            catch(IOException e)
            {
                System.out.println("Error in closing the buffer");
            }
        }
    }

    class Process {
        public Process(String ipAddress, int port) {
            this.ipAddress = ipAddress;
            this.port = port;
        }

        public int getPort() {
            return this.port;
        }

        private String ipAddress;
        private int port;
    }
}
