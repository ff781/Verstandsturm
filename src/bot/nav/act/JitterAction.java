package bot.nav.act;

import bot.Bot;

import util.state.*;
import util.func.*;

public class JitterAction extends ThreadBoundAction {

	public JitterAction(float jitterDistance, float jitterSpeed, float jitterAngel) {
		super(new JitterThreadFactory(jitterDistance,jitterSpeed,jitterAngel));
	}
	
	public static final class JitterThread extends Thread {
		Bot bot;
		boolean[]stop;
		public float jitterDistance;
		public float jitterSpeed;
		public float jitterAngel;
		
		public JitterThread(Bot bot, boolean[] stop, float jitterDistance, float jitterSpeed, float jitterAngel) {
			super();
			this.bot = bot;
			this.stop = stop;
			this.jitterDistance = jitterDistance;
			this.jitterSpeed = jitterSpeed;
			this.jitterAngel = jitterAngel;
		}

		@Override
		public void run() {
			int i = 0;
			while(!stop[0]) {
				if(i==0) {
					bot.driver.drive_(jitterDistance * .88f, jitterSpeed, jitterAngel);
				}
				else {
					if(i%2==0) {
						bot.driver.drive_(jitterDistance, jitterSpeed, jitterAngel);
					} else {
						bot.driver.drive_(jitterDistance, jitterSpeed, -jitterAngel);
					}
				}
				i++;
			}
		}
	}
	
	public static final class JitterThreadFactory implements Function2<Bot,boolean[],Thread> {
		
		public float jitterDistance;
		public float jitterSpeed;
		public float jitterAngel;

		public JitterThreadFactory(float jitterDistance, float jitterSpeed, float jitterAngel) {
			super();
			this.jitterDistance = jitterDistance;
			this.jitterSpeed = jitterSpeed;
			this.jitterAngel = jitterAngel;
		}

		@Override
		public Thread exec(Bot s, boolean[] t) {
			return new JitterThread(s,t,jitterDistance,jitterSpeed,jitterAngel);
		}
		
	} 

}
