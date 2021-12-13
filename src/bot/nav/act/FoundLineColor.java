package bot.nav.act;

import static bot.nav.ParcourConstants.*;

import bot.Bot;
import util.func.*;
import util.state.State;

public class FoundLineColor extends _Predicate<Bot> implements Predicate<Bot> {
		
	int lineColorI;
	
	public FoundLineColor(int lineColorI) {
		this.lineColorI = lineColorI;
	}
	
	@Override
	public Boolean exec(Bot t) {
		return colorClassify(t.sensors.getRGB(),ALL_COLORS) == this.lineColorI;
	}
}
