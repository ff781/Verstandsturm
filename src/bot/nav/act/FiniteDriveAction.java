package bot.nav.act;

import bot.Bot;
import bot.Driver;
import util.state.*;

public class FiniteDriveAction extends NoSEAction {
	
	public float distance;
	public float speed;
	public float rotation;

	//defaults to driving forward
	public FiniteDriveAction(float distance, float speed) {
		this(distance, speed, Driver.FORWARD_DEGREES);
	}
	
	public FiniteDriveAction(float distance, float speed, float rotation) {
		super();
		this.distance = distance;
		this.speed = speed;
		this.rotation = rotation;
	}

	@Override
	public void start(Bot bot) {
		bot.driver.drive_(distance, speed, rotation, false);		
	}

	@Override
	public void stop(Bot bot) {
		bot.driver.driveStop();
	}

	@Override
	public boolean finished(Bot bot) {
		return !bot.driver.isMoving();
	}

}
