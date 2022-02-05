package bot.nav.act;

import bot.Bot;
import bot.men.Screen;
import util.state.ImmediateAction;

public class BeepAction extends ImmediateAction {

	@Override
	public void start(Bot bot) {
		Screen.beep();
	}

}
