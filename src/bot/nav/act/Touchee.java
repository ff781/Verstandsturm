package bot.nav.act;

import bot.Bot;
import util.func.Predicate;
import util.func._Predicate;

// True if bot is being touchee
public class Touchee extends _Predicate<Bot> implements Predicate<Bot> {

	@Override
	public Boolean exec(Bot t) {
		return t.sensors.touchee();
	}

}
