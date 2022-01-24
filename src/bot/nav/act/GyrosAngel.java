package bot.nav.act;

import bot.Bot;
import util.func.Predicate;
import util.func._Predicate;
import util.meth.Meth;


public class GyrosAngel extends _Predicate<Bot> implements Predicate<Bot>{
	
	public float targetAngel;
	public float absTolerance;
	public GyrosAngel(float targetAngel, float tolerance) {
		super();
		this.targetAngel = targetAngel;
		this.absTolerance = tolerance;
	}
	@Override
	public Boolean exec(Bot t) {
		return Meth.angelDist(targetAngel, t.sensors.getAngel()) < absTolerance;
	}

}
