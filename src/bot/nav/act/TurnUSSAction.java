package bot.nav.act;

import bot.Bot;
import util.state.*;

public class TurnUSSAction extends NoSEAction {
	
	public float turnDegrees;
	public float turnSpeed;	

	public TurnUSSAction(float turnDegrees, float turnSpeed) {
		super();
		this.turnDegrees = turnDegrees;
		this.turnSpeed = turnSpeed;
	}

	@Override
	public void start(Bot bot) {
		bot.driver.turnUS(turnDegrees, turnSpeed, false);
	}

	@Override
	public void stop(Bot bot) {
		bot.driver.USStop();
	}

	@Override
	public boolean finished(Bot bot) {
		return !bot.driver.isUSMoving();
	}

}
