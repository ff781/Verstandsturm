package bot.nav.line;

import util.func.Function;

import bot.Bot;

public class StateExecutor {
	
	State cur;
	
	public StateExecutor(State start) {
		super();
		this.cur = start;
	}

	public void exec(Bot bot) {
		if(cur != State.END) return;
		State next = null;
		cur.action.start(bot);
		while(cur != State.END) {
			if(next!=null) {
				cur = next;
				if(cur != State.END) return;
				cur.action.start(bot);
			}
			if(cur.action.finished(bot)) {
				next = cur.next;
			}
			else
			for(Function<Bot,State>edge:cur.edges) {
				next = edge.exec(bot);
				if(next!=null) {
					cur.action.stop(bot);
					break;
				}
			}
			
			
		}
	}
}
