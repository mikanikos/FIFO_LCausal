import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
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

    public Map<Integer, ProcessData> getProcesses() {
        return processes;
    }

    private int id;
    private int numProcesses;
    private int numMessages;
    public static Map<Integer, ProcessData> processes;

    public static UDP_Receiver receiver;
    private static boolean running = true;

    public Da_proc() {
        this.processes = new HashMap<Integer, ProcessData>();
        System.out.println(ManagementFactory.getRuntimeMXBean().getName());
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
        new OutputLogger(main_instance.id);

        // start listening for incoming UDP packets
        int myPort = main_instance.getProcesses().get(main_instance.getId()).getPort();
        receiver = new UDP_Receiver(myPort);
        while(ProcessModel.wait_for_start) {
            Thread.sleep(1000);
        }

        System.out.println("Broadcasting " + main_instance.numMessages + " messages");

        // not sending to myself
        main_instance.processes.remove(main_instance.id);

        // start broadcast
        UDP_Sender sender;
        int seq_nr;
        for (seq_nr = 1; seq_nr <= main_instance.numMessages && running; seq_nr++) {
            for (ProcessData p : main_instance.processes.values()) {
                MessageData m = new MessageData(main_instance.id, p.getId(), seq_nr, false);
                new Perfect_Sender(p, m);
            }
            // handle the output of processes
            OutputLogger.writeLog("b " + seq_nr);
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
                        processes.put(Integer.parseInt(fields[0]), new ProcessData(Integer.parseInt(fields[0]), fields[1], Integer.parseInt(fields[2])));
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
}
