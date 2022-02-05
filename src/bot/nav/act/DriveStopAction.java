package bot.nav.act;

import bot.Bot;
import util.state.ImmediateAction;
import util.state.NoSEAction;

public class DriveStopAction extends ImmediateAction {

	int hardness;
	
	public DriveStopAction(int hardness) {
		this.hardness = hardness;
	}
	
	@Override
	public void start(Bot bot) {
		bot.driver.driveStop(this.hardness);
	}

}
