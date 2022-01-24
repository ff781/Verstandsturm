package bot.nav.act;

import bot.Bot;
import util.state.NoSEAction;

public class GyrosResetAction extends NoSEAction {

	@Override
	public void start(Bot bot) {
		bot.sensors.resetAngel();
	}

	@Override
	public void stop(Bot bot) {
		
	}

	@Override
	public boolean finished(Bot bot) {
		return true;
	}

}
