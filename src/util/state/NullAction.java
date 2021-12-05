package util.state;

import bot.Bot;

public class NullAction implements Action {

	NullActionThread thread;
	
	public NullAction() {
		
	}
	
	@Override
	public void start(Bot bot) {
		this.thread.run();
	}

	@Override
	public void stop(Bot bot) {
		this.thread.stop = true;
	}

	@Override
	public boolean finished(Bot bot) {
		return !this.thread.isAlive();
	}

	@Override
	public void reset() {
		this.thread = new NullActionThread();
	}
	
	private static class NullActionThread extends Thread {
		boolean stop = false;
		public void run() {
			while(!stop);
		}
	}

}
