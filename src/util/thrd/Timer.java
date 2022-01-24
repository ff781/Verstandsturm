package util.thrd;

public class Timer extends Thread {
	
	long millis;
	
	public Timer(long millis) {
		this.millis = millis;
		this.setDaemon(true);
	}
	
	public void run() {
		try {
			Thread.sleep(millis);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
