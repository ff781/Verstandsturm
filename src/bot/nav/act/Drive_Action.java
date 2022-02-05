package bot.nav.act;

import bot.Bot;
import bot.Driver;
import util.state.NoSEAction;
import util.thrd.ThrdUtil;
import util.thrd.Timer;

public class Drive_Action extends NoSEAction {
	
	float distance;
	float speed;
	float rotation;
	int hard;

	private Thread[]collector = new Thread[2];
	private Timer startTimer;

	public Drive_Action(float distance, float speed, float rotation) {
		this(distance, speed, rotation, Driver.HARD_DEFAULT);
	}
	
	public Drive_Action(float distance, float speed, float rotation, int hard) {
		super();
		this.distance = distance;
		this.speed = speed;
		this.rotation = rotation;
		this.hard = hard;
	}

	@Override
	public void start(Bot bot) {
		bot.driver.drive_(distance, speed, rotation,false,hard,collector);
	}

	@Override
	public void stop(Bot bot) {
		bot.driver.driveStop(hard);
	}

	@Override
	public boolean finished(Bot bot) {
		return ThrdUtil.allCollected(collector);
	}

}
