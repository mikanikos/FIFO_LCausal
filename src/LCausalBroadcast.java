import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LCausalBroadcast {
    
    // Initiating the vector clock
    private static int[] vectorClock = new int[Da_proc.getNumProcesses() + 1];
    
    // Initiating a mask for managing the dependency list
    private static boolean[] mask = new boolean[Da_proc.getNumProcesses() + 1];
    
    // Initializing the pending queue for each processId
    private static ConcurrentLinkedQueue<int[]>[] pending = new ConcurrentLinkedQueue[Da_proc.getNumProcesses() + 1];

    // Deliver message for LCausal protocol
    public static void deliver(MessageSource ms) {

        while(true) {
        	// Initialization
        	for (int i = 0; i < Da_proc.getNumProcesses(); i++) {
        		vectorClock[i] = 0;
        	    pending[i] = new ConcurrentLinkedQueue<int[]>();
        	    mask[i] = true;
        	}
        	
        	// Assign specific VC to processes
            if(Da_proc.getId() != ms.getSourceID()){
                pending[ms.getSourceID()].add(vectorClock);
                deliverOrPending();
            }
        }
    }
    
    public static void deliverOrPending() {
    	
    	// Create a copy for the last vector clock
    	int vectorClockLength = vectorClock.length;
    	int[] copyVectorClock = new int[vectorClockLength];
    	
        for(int i= 0; i < vectorClockLength; i++){
            if (mask[i]) {
            	copyVectorClock[i] = vectorClock[i];
            }
        }
    	
        // Proceed with delivering messages
    	for (int processId = 0; processId < Da_proc.getNumProcesses(); processId++) {
    		
    		Iterator<int[]> dependency = pending[processId].iterator();
    		
    		while (dependency.hasNext()) {
                int[] vectorClock = dependency.next();
                boolean pendingState = false;
                
                // Check if the current vector clock is less than the previous one
                for (int pId= 1; pId <= Da_proc.getNumProcesses(); pId++){
                    if(copyVectorClock[pId] < vectorClock[pId]) {
                    	pendingState = true;
                    }
                }
                
                // Deliver following the dependencies
                if (!pendingState) {
                	dependency.remove();
                	dependency = pending[processId].iterator();

                	OutputLogger.writeLog("d " + processId + " " + vectorClock[processId]);
                    vectorClock[processId] = vectorClock[processId] + 1;
                }
    		
    		}
    	}
    }
	
}
