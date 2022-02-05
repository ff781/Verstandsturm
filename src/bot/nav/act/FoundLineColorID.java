package bot.nav.act;

import bot.Bot;
import util.func.Predicate;

public class FoundLineColorID implements Predicate<Bot> {
	
	int id;
	
	public FoundLineColorID(int id) {
		this.id = id;
	}
	
	@Override
	public Boolean exec(Bot t) {
		return t.sensors.getColorID() == this.id;
	}

}
