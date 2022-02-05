package bot.nav.act;

import bot.Bot;

import util.state.*;
import util.thrd.Timer;

public class FiniteTurnAction extends NoSEAction {
	
	public float turnDegrees;
	public float turnSpeed;
	
	private Timer startTimer;
	private int hard;

	public FiniteTurnAction(float turnDegrees, float turnSpeed) {
		this(turnDegrees, turnSpeed, 1);
	}
	public FiniteTurnAction(float turnDegrees, float turnSpeed, int hard) {
		super();
		this.turnDegrees = turnDegrees;
		this.turnSpeed = turnSpeed;
		this.hard = hard;
	}
	

	@Override
	public void start(Bot bot) {
		bot.driver.turn(turnDegrees, turnSpeed, false, hard);
		startTimer = new Timer(1000);
		startTimer.start();
	}

	@Override
	public void stop(Bot bot) {
		bot.driver.driveStop(hard);
	}

	@Override
	public boolean finished(Bot bot) {
		return startTimer==null || !startTimer.isAlive() && !bot.driver.isMoving();
	}

}
