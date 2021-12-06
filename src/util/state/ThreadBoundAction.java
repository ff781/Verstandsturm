package util.state;

import bot.Bot;
import util.func.*;

public class ThreadBoundAction implements Action{
	
	/*
	 * The produced threads have to complete thread halting in finite time when the stop flag is set for ThreadBoundAction to terminate in finite time
	 */
	protected Function2<Bot,boolean[],Thread> threadFactory;
	
	private Thread thread;
	private boolean[]stop = new boolean[] {false};

	protected ThreadBoundAction() {
		
	}
	
	public ThreadBoundAction(Function2<Bot,boolean[],Thread> threadFactory) {
		super();
		this.threadFactory = threadFactory;
	}

	@Override
	public void start(Bot bot) {
		assert finished(bot);
		thread = this.threadFactory.exec(bot,stop);
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void stop(Bot bot) {
		stop[0] = true;
		try {
			thread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean finished(Bot bot) {
		return thread == null || !thread.isAlive();
	}
	
	public void reset() {
		
	}

}
