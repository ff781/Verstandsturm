package util.state;

import java.util.*;

import bot.Bot;

public class ActionUtil {
	
	private ActionUtil() {}
	
	public static Action concat(List<Action> actions) {
		return new ConcatAction(actions);
	}
	
	public static class ConcatAction implements Action {

		int i = 0;
		List<Action> actions;
		boolean stop;
		Thread thread;
		
		public ConcatAction(List<Action> actions) {
			this.actions = actions;
		}

		@Override
		public void start(Bot bot) {
			assert finished(bot);
			thread = new _Thread(bot);
			thread.setDaemon(true);
			thread.run();
		}

		@Override
		public void stop(Bot bot) {
			stop = true;
			try {
				thread.join();
			}catch(Exception e) {throw new RuntimeException(e);}
		}

		@Override
		public boolean finished(Bot bot) {
			return !thread.isAlive();
		}

		@Override
		public void reset() {
			i = 0;
		}
		
		class _Thread extends Thread {
			
			public Bot bot;

			public _Thread(Bot bot) {
				this.bot = bot;
			}

			@Override
			public void run() {
				for(Action cur : actions) {
					cur.reset();
					cur.start(bot);
					while(!cur.finished(bot));
				}
			}
			
		}
		
	}

}
