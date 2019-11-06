import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.*;

public class Da_proc {

    private static int id;
    private static int numProcesses;
    private static int numMessages;
    private static ConcurrentMap<Integer, ProcessData> processes;
    private static boolean running = true;
    private static ExecutorService threadedBroadcast;

    public static ExecutorService getThreadedBroadcast() {
        return threadedBroadcast;
    }

    static int getNumProcesses() { return numProcesses; }
    static boolean isRunning() {
        return running;
    }
    static void stopRunning() {
        running = false;
    }
    static int getId() {
        return id;
    }
    static int getNumMessages() {
        return numMessages;
    }
    static ConcurrentMap<Integer, ProcessData> getProcesses() {
        return processes;
    }

    public Da_proc() {
        processes = new ConcurrentHashMap<>();
        // set signal handlers
        new SignalHandlerUtility();

        System.out.println(ManagementFactory.getRuntimeMXBean().getName());
    }

    @SuppressWarnings("static-access")
	public static void main(String[] args) throws InterruptedException, IOException {

        Da_proc main_instance = new Da_proc();

        System.out.println("Initializing");

        // parse arguments, including membership
        id = Integer.parseInt(args[0]);
        main_instance.parse_membership(args[1]);
        numMessages = Integer.parseInt(args[2]);


        // start listening for incoming UDP packets
        int myPort = processes.get(id).getPort();
        new Thread(new Receiver(myPort)).start();
        //new Thread(new URBroadcast()).start();
        //new Thread(new PerfectLink()).start();

        threadedBroadcast = Executors.newFixedThreadPool(numProcesses-1);

        for (ProcessData p : Da_proc.getProcesses().values()) {
            if (p.getId() != Da_proc.getId()) {
                Runnable worker = new URBroadcast(p);
                threadedBroadcast.execute(worker);
            }
        }

        while(SignalHandlerUtility.wait_for_start) {
            Thread.sleep(1000);
        }

        System.out.println("Broadcasting " + main_instance.numMessages + " messages");
        for (int seq_nr = 1; seq_nr <= Da_proc.getNumMessages() && running; seq_nr++) {
            URBroadcast.broadcast(Da_proc.getId(), seq_nr);

            // handle the output of processes
            OutputLogger.writeLog("b " + seq_nr);
        }

        // start broadcast
//        System.out.println("Broadcasting " + main_instance.numMessages + " messages");
//        for (int seq_nr = 1; seq_nr <= Da_proc.getNumMessages() && running; seq_nr++) {
//            URBroadcast.broadcast(Da_proc.getId(), seq_nr);
//
//            // handle the output of processes
//            OutputLogger.writeLog("b " + seq_nr);
//        }

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
                            numProcesses = Integer.parseInt(fields[0]);
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
                assert buffer != null;
                buffer.close();
            }
            catch(IOException e)
            {
                System.out.println("Error in closing the buffer");
            }
        }
    }
}