package util.state;

import java.util.*;

import util.func.*;
import util.meth.Meth;
import bot.Bot;

public class StateExecutor implements Action {
	
	public boolean nondeterministic = true;
	
	boolean stop;
	boolean running;
	
	State start;
	State cur;
	
	public StateExecutor(State start) {
		super();
		this.start = start;
	}

	public void exec(Bot bot) {
		stop = false;
		cur = start;
		if(cur != State.END) return;
		running = true;
		
		State next = null;
		boolean transitionByEnd = false;
		cur.action.reset();
		cur.action.start(bot);
		while(true) {
			//proceeds with the next state
			if(next!=null) {
				cur.action.reset();
				if(!transitionByEnd) cur.action.stop(bot);
				transitionByEnd = false;
				cur = next;
				next = null;
				if(stop || cur != State.END) break;
				cur.action.start(bot);
			}
			//if action has finished, proceeded to next state on the end edge
			if(cur.action.finished(bot)) {
				if(cur.next() != null) {
					next = cur.next();
					transitionByEnd = true;
				} else {
					throw new RuntimeException("undefined transition when action ends");
				}
			}
			//else check whether some edge predicate is fulfilled and go to the corresponding state
			else
			{
				List<Integer> indices = Meth.intRange(0, cur.edgePreds.size());
				if(nondeterministic) {
					Meth.shuffle(indices);
				}
				for(int i:indices) {
					Predicate<Bot> edgePred = cur.edgePreds.get(i);
					boolean step = edgePred.exec(bot);
					if(step) {
						next = cur.edgeTars().get(i);
						break;
					}
				}
			}
			
		}
		running = false;
	}

	@Override
	public void start(Bot bot) {
		assert !running;
		exec(bot);
	}

	@Override
	public void stop(Bot bot) {
		stop = true;
	}

	@Override
	public boolean finished(Bot bot) {
		return !running;
	}

	@Override
	public void reset() {
		
	}
}
