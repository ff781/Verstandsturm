package bot.nav.act;

import static bot.nav.ParcourConstants.*;

import bot.Bot;
import util.func.*;
import util.state.State;

public class FoundLineColor extends _Predicate<Bot> implements Predicate<Bot> {
		
	float[]lineColor;
	
	public FoundLineColor(float[]lineColor) {
		this.lineColor = lineColor;
	}
	
	@Override
	public Boolean exec(Bot t) {
		return colorMatch(t.sensors.getRGB(),lineColor);
	}
}
