package bot.nav.act;

import bot.Bot;
import util.state.NoSEAction;
import util.thrd.Timer;

public class InfiniteDriveAction extends NoSEAction {
	
	public float driveSpeed;
	
	private Timer startTimer;
	
	public InfiniteDriveAction(float driveSpeed) {
		super();
		this.driveSpeed = driveSpeed;
	}
	@Override
	public void start(Bot bot) {
		bot.driver.driveForever(driveSpeed);
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
