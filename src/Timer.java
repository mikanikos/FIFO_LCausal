public class Timer {
	private long expiration;
	private long delay;
	
	public Timer(int delay) {
		this.delay = delay;
	}
	
	public void start() {
		expiration = delay + System.currentTimeMillis();
	}
	
	public boolean isExpired() {
		return expiration < System.currentTimeMillis();
	}
	
}
