package bot.nav.act;

import bot.Bot;
import util.state.*;

public class FiniteTurnAction extends NoSEAction {
	
	public float turnDegrees;
	public float turnSpeed;

	public FiniteTurnAction(float turnDegrees, float turnSpeed) {
		super();
		this.turnDegrees = turnDegrees;
		this.turnSpeed = turnSpeed;
	}

	@Override
	public void start(Bot bot) {
		bot.driver.turn(turnDegrees, turnSpeed, false);
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
