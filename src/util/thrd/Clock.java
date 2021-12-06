package util.thrd;

public abstract class Clock extends Thread {
	
	public boolean stop;
	public long stepMillis;
	
	public Clock(long stepMillis) {
		this.stepMillis = stepMillis;
		this.setDaemon(true);
	}
	
	public void run() {
		stop = false;
		while(!stop) {
			exec();
			try {
				Thread.sleep(stepMillis);
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	native void teaset();
	
	public void stopp() {
		if(!this.isAlive()) return;
		this.stop = true;
		try {
			this.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	abstract public void exec();

}
