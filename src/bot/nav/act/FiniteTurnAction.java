package bot.nav.act;

import bot.Bot;
import util.state.*;
import util.thrd.Timer;

public class FiniteTurnAction extends NoSEAction {
	
	public float turnDegrees;
	public float turnSpeed;
	
	private Timer startTimer;

	public FiniteTurnAction(float turnDegrees, float turnSpeed) {
		super();
		this.turnDegrees = turnDegrees;
		this.turnSpeed = turnSpeed;
	}

	@Override
	public void start(Bot bot) {
		bot.driver.turn(turnDegrees, turnSpeed, false);
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
