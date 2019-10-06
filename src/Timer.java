public class Timer {
	long expiers;
	long delay;
	
	public Timer(int delay) {
		this.delay = delay;
	}
	
	public void start() {
		expiers = delay + System.currentTimeMillis();
	}
	
	public boolean isExpired() {
		return expiers < System.currentTimeMillis();
	}
}
