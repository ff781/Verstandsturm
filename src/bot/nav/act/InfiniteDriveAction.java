package bot.nav.act;

import bot.Bot;
import util.state.NoSEAction;

public class InfiniteDriveAction extends NoSEAction {
	
	public float driveSpeed;
	
	public InfiniteDriveAction(float driveSpeed) {
		super();
		this.driveSpeed = driveSpeed;
	}
	@Override
	public void start(Bot bot) {
		bot.driver.driveForever(driveSpeed);
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
