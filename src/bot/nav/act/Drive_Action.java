package bot.nav.act;

import bot.Bot;

import util.state.NoSEAction;
import util.thrd.Timer;

public class Drive_Action extends NoSEAction {
	
	float distance;
	float speed;
	float rotation;

	private Timer startTimer;

	public Drive_Action(float distance, float speed, float rotation) {
		super();
		this.distance = distance;
		this.speed = speed;
		this.rotation = rotation;
	}

	@Override
	public void start(Bot bot) {
		bot.driver.drive_(distance, speed, rotation,false);
		startTimer = new Timer(1000);
		startTimer.start();
	}

	@Override
	public void stop(Bot bot) {
		bot.driver.driveStop();
	}

	@Override
	public boolean finished(Bot bot) {
		return startTimer==null || !startTimer.isAlive() && !bot.driver.isMoving();
	}

}
