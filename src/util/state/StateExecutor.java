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
	//whether to render information on brick screen
	public boolean render;
	//whether to record a state history, e.g. for debugging
	boolean historian;
	
	State start;
	State cur;
	
	List<State> history;
	List<State.Transition> transitionHistory;
	public StringBuilder extraLogInfo;
	
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
		long lastTick = System.currentTimeMillis();
		final long[]lastDiff = {0};
		Clock renderClock = new Clock(250) {

			@Override
			public void exec() {
				if(render) {
					Screen.clear();
					Screen.prints(((cur==null)?null:cur.getName()) + "");
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
					Screen.prints("ts:"+lastDiff[0]);
					//Screen.prints("ep:"+cur.edgePreds.size());
					//Screen.prints("tal:"+bot.lMotor.getTachoCount());
					//Screen.prints("tar:"+bot.rMotor.getTachoCount());
					for(String s : renderBuffer) {
						if(s!=null) {
							Screen.prints(s);
						}
					}
				}
			}};
			renderClock.start();
		try {
			cur = start;
			if(this.historian) {
				this.history.add(cur);
			}
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
					if(this.historian) {
						this.history.add(next);
						this.transitionHistory.add(new State.Transition(cur, next, !transitionByEnd));
					}
					if(!transitionByEnd) {
						cur.action.stop(bot);	
					}
					if(finalizingAction!=null) {
						finalizingAction.reset();
						finalizingAction.start(bot);
						renderBuffer[4] = "fin "+cur.getName();
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
					if(cur.action instanceof ActionUtil.ConcatAction) {
						ActionUtil.ConcatAction cca = (ActionUtil.ConcatAction)cur.action;
						boolean isNull = cca.thread == null;
						extraLogInfo.append(isNull?1:0).append(' ');
						if(!isNull) {
							extraLogInfo.append(cca.thread.isAlive()?1:0).append('\n');
						}
					}
					next = cur.next();
					if(next != null) {
						transitionByEnd = true;
						finalizingAction = cur.nextFinalizingAction();
						for(int ri=0;ri<renderBuffer.length;ri++)renderBuffer[ri]=null;
						renderBuffer[0] = cur.getName()+" end";
						renderBuffer[1] = next.getName();
					} else {
						throw new RuntimeException("undefined transition when action ends for state " + cur.getName());
					}
				}
				//else check whether some edge predicate is fulfilled and go to the corresponding state
				else
				{
					List<Integer> indices = Meth.intRange(0, cur.edgePreds.size());
					if(nondeterministic) {
						Meth.shuffle(indices);
					}
					//renderBuffer[0] = "i:" + indices.toString() + "";
					long now = System.currentTimeMillis();
					lastDiff[0] = now - lastTick;
					lastTick = now;
					for(int i:indices) {
						Predicate<Bot> edgePred = cur.edgePreds.get(i);
						boolean step = edgePred.exec(bot);
						if(step) {
							next = cur.edgeTars().get(i);
							finalizingAction = cur.edgeFinalizingActions().get(i);
							for(int ri=0;ri<renderBuffer.length;ri++)renderBuffer[ri]=null;
							renderBuffer[0] = i +" "+cur.getName();
							renderBuffer[1] = "n "+next.getName();
							break;
						}
					}
				}
				
			}
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
	
	public void setHistorian(boolean a) {
		if(this.historian != a) {
			this.historian = a;
			this.history = this.historian ? new ArrayList<State>() : null;
			this.transitionHistory = this.historian ? new ArrayList<State.Transition>() : null;
			this.extraLogInfo = this.historian ? new StringBuilder() : null;
		}
	}

	public List<State> getHistory() {
		return this.history;
	}
	
	public List<State.Transition> getTransitionHistory() {
		return this.transitionHistory;
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
