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
		final String[]renderBuffer = new String[10];
		Clock renderClock = new Clock(200) {

			@Override
			public void exec() {
				Screen.clear();
				Screen.prints(((cur==null)?null:cur.getClass().getSimpleName()) + "");
				//Screen.prints(rgbInfo(bot));
				String color = "unknown";
				switch(colorClassify(bot.sensors.getRGB(), ALL_COLORS))
				{
				case LINE_WHITE_I: color = "WHITE";break;
				case LINE_BROWN_I: color = "BROWN";break;
				case LINE_BLUE_I: color = "BLUE";break;
				case LINE_FAKE_BLUE_I: color = "FAKE_BLUE";break;
				}
				Screen.prints(color + "");
				for(String s : renderBuffer) {
					if(s!=null) {
						Screen.prints(s);
					}
				}
			}};
			renderClock.start();
		try {
			cur = start;
			if(cur == State.END) return;
			running = true;
			
			
			next = null;
			boolean transitionByEnd = false;
			Action finalizingAction = null;
			cur.action.reset();
			cur.action.start(bot);
			
			
			while(!stop[0] && !Screen.wasPressed(Button.ID_ESCAPE)) {
				
				//proceeds with the next state
				if(next!=null) {
					if(!transitionByEnd) {
						cur.action.stop(bot);
					}
					if(finalizingAction!=null) {
						finalizingAction.start(bot);
						renderBuffer[4] = "fin "+cur.getClass().getSimpleName();
						while(!finalizingAction.finished(bot) && Button.ESCAPE.isUp()) Screen.sleep(50);
					}
					finalizingAction = null;
					transitionByEnd = false;
					cur = next;
					next = null;
					//renderBuffer[3] = "waiting for press";
					//Button.waitForAnyPress();
					for(int ri=0;ri<renderBuffer.length;ri++)renderBuffer[ri]=null;
					if(cur == State.END) break;
					cur.action.reset();
					cur.action.start(bot);
				}
				//if action has finished, proceeded to next state on the end edge
				if(cur.action.finished(bot)) {
					if(cur.next() != null) {
						next = cur.next();
						transitionByEnd = true;
						finalizingAction = cur.nextFinalizingAction();
						for(int ri=0;ri<renderBuffer.length;ri++)renderBuffer[ri]=null;
						renderBuffer[0] = cur.getClass().getSimpleName()+" end";
						renderBuffer[1] = next.getClass().getSimpleName();
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
					renderBuffer[0] = "i:" + indices.toString() + "";
					for(int i:indices) {
						Predicate<Bot> edgePred = cur.edgePreds.get(i);
						boolean step = edgePred.exec(bot);
						if(step) {
							next = cur.edgeTars().get(i);
							finalizingAction = cur.edgeFinalizingActions().get(i);
							for(int ri=0;ri<renderBuffer.length;ri++)renderBuffer[ri]=null;
							renderBuffer[1] = i +" "+cur.getClass().getSimpleName();
							renderBuffer[2] = "n "+next.getClass().getSimpleName();
							break;
						}
					}
				}
				
			}
			Screen.sleep(100);
			if(cur != null && cur.action != null && !cur.action.finished(bot)) {
				cur.action.stop(bot);
			}
			renderClock.stopp();
		} catch (Exception e) {
			if(cur != null && cur.action != null && !cur.action.finished(bot)) {
				cur.action.stop(bot);
			}
			renderClock.stopp();
			throw e;
//			Screen.clear();
//			Screen.prints(e.getStackTrace()+"");
//			Button.waitForAnyEvent();
		} finally {
			if(cur != null && cur.action != null && !cur.action.finished(bot)) {
				cur.action.stop(bot);
			}
			renderClock.stopp();
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
