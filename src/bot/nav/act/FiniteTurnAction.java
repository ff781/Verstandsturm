package bot.nav.act;

import bot.Bot;

import util.state.*;
import util.thrd.Timer;

public class FiniteTurnAction extends NoSEAction {
	
	public float turnDegrees;
	public float turnSpeed;
	
	private Timer startTimer;
	private boolean hard;

	public FiniteTurnAction(float turnDegrees, float turnSpeed) {
		this(turnDegrees, turnSpeed, false);
	}
	public FiniteTurnAction(float turnDegrees, float turnSpeed, boolean hard) {
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
	}

	@Override
	public boolean finished(Bot bot) {
		return startTimer==null || !startTimer.isAlive() && !bot.driver.isMoving();
	}

}
