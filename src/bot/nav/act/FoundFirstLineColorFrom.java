package bot.nav.act;

import bot.Bot;
import util.func.Predicate;
import util.func._Predicate;

import static bot.nav.ParcourConstants.*;

public class FoundFirstLineColorFrom extends _Predicate<Bot> implements Predicate<Bot> {

	float[]bias;
	float[][]cs;
	
	
	public FoundFirstLineColorFrom(float[]bias,float[]... cs) {
		super();
		this.bias = bias;
		this.cs = cs;
	}



	@Override
	public Boolean exec(Bot t) {
		return colorClassify(t.sensors.getRGB(), cs, bias)==0;
	}

}
