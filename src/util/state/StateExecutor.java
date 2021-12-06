package util.state;

import java.util.*;

import util.func.*;
import util.meth.Meth;
import util.thrd.Clock;
import bot.Bot;
import bot.men.Screen;
import lejos.hardware.Button;

import static bot.nav.ParcourConstants.*;

public class StateExecutor extends ThreadBoundAction implements Action {
	
	public boolean nondeterministic = true;
	
	boolean stop;
	boolean running;
	
	State start;
	State cur;
	
	public StateExecutor(State start) {
		super();
		this.threadFactory = new ThreadFactory();
		this.start = start;
	}
	
	public void exec(Bot bot) {
		this.exec(bot, new boolean[] {false,});
	}

	public void exec(final Bot bot, final boolean[]stop) {
		State next;
		Clock renderClock = new Clock(200) {

			@Override
			public void exec() {
				Screen.clear();
				Screen.prints(((cur==null)?null:cur.getClass().getSimpleName()) + "");
				Screen.prints(rgbInfo(bot));
				Screen.prints(colorBinaryClassify(bot.sensors.getRGB(), LINE_WHITE, LINE_BROWN) + "");
				Screen.prints(Meth.length(Meth.sub(bot.sensors.getRGB(), LINE_WHITE))+"");
				Screen.prints(Meth.length(Meth.sub(bot.sensors.getRGB(), LINE_BROWN))+"");
				Screen.prints(colorIDToString(bot.sensors.getColorID()));
			}};
			renderClock.start();
		try {
			cur = start;
			if(cur == State.END) return;
			running = true;
			
			
			next = null;
			boolean transitionByEnd = false;
			cur.action.reset();
			cur.action.start(bot);
			
			
			while(!stop[0] && Button.ESCAPE.isUp()) {
				
				//proceeds with the next state
				if(next!=null) {
					if(!transitionByEnd) cur.action.stop(bot);
					transitionByEnd = false;
					cur = next;
					next = null;
					if(cur == State.END) break;
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
			Screen.sleep(100);
			renderClock.stopp();
		} catch (Exception e) {
			renderClock.stopp();
			Screen.clear();
			Screen.prints(e.getStackTrace()+"");
			Button.waitForAnyEvent();
		}
		running = false;
	}
	
	public class ThreadFactory implements Function2<Bot,boolean[],Thread>{

		@Override
		public Thread exec(final Bot s, final boolean[] t) {
			Thread th = new Thread() {
				public void run() {
					StateExecutor.this.exec(s, t);
				}
			};
			return th;
		}
		
	}
}
