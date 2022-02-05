package util.state;

import java.util.*;

import bot.Bot;

public class ActionUtil {
	
	private ActionUtil() {}
	
	public static Action multiply(int n, Action action) {
		List<Action> actions = new ArrayList<>(n);
		for(int i=0;i<n;i++) {
			actions.add(action);
		}
		return new ConcatAction(actions);
	}
	
	public static Action concat(List<Action> actions) {
		return new ConcatAction(actions);
	}
	
	public static class ConcatAction implements Action {

		int i = 0;
		List<Action> actions;
		boolean stop;
		public Thread thread;
		
		public ConcatAction(List<Action> actions) {
			this.actions = actions;
		}

		@Override
		public void start(Bot bot) {
			assert finished(bot);
			stop = false;
			thread = new _Thread(bot);
			thread.setDaemon(true);
			thread.start();
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
			return thread == null || !thread.isAlive();
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
					while(!cur.finished(bot)) {
						if(stop) {
							cur.stop(bot);
							break;
						}
					}
					if(stop) break;
					cur.stop(bot);
				}
			}
			
		}
		
	}

}
