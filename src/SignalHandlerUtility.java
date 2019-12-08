import sun.misc.Signal;

// Signal utility: modified version of the template file provided
public class SignalHandlerUtility extends Thread {

	// Wait to start sending packets
	static boolean wait_for_start = true;

	SignalHandlerUtility() {
		SigHandlerUsr2 sigHandlerUsr2 = new SigHandlerUsr2(this);
		SigHandlerInt sigHandlerTerm = new SigHandlerInt(this);
		SigHandlerTerm sigHandlerInt = new SigHandlerTerm(this);

		Signal signalTerm = new Signal("TERM");
		Signal signalInt = new Signal("INT");
		Signal signalUsr2 = new Signal("USR2");

		Signal.handle(signalInt, sigHandlerInt);
		Signal.handle(signalTerm, sigHandlerTerm);
		Signal.handle(signalUsr2, sigHandlerUsr2);

		this.start();
	}

	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(100);
			} catch (Exception e){
				// exception
			}
		}
	}

	// Stop application, stop packet processing by stopping sending and receiving queue
	private void stopApp() {
		System.out.println("Immediately stopping network packet processing");

		// Stop listening and processing packets
		Da_proc.stopRunning();

		// Write to file the logs
		OutputLogger.writeLogToFile();

		System.exit(0);
	}

	public static class SigHandlerUsr2 implements sun.misc.SignalHandler {
		SignalHandlerUtility p;

		private SigHandlerUsr2(SignalHandlerUtility p) {
			super();
			this.p = p;
		}

		@Override
		public void handle(Signal signal) {
			System.out.format("Handling signal: %s\n", signal.toString());
			SignalHandlerUtility.wait_for_start = false;
		}
	}

	public static class SigHandlerTerm implements sun.misc.SignalHandler {
		SignalHandlerUtility p;

		private SigHandlerTerm(SignalHandlerUtility p) {
			super();
			this.p = p;
		}

		@Override
		public void handle(Signal signal) {
			System.out.format("Handling signal: %s\n", signal.toString());
			p.interrupt();
			p.stopApp();
		}
	}

	public static class SigHandlerInt implements sun.misc.SignalHandler {
		SignalHandlerUtility p;

		private SigHandlerInt(SignalHandlerUtility p) {
			super();
			this.p = p;
		}

		@Override
		public void handle(Signal signal) {
			System.out.format("Handling signal: %s\n", signal.toString());
			p.interrupt();
			p.stopApp();
		}
	}
}
