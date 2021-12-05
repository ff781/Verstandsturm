package bot.nav.act;

import bot.Bot;
import util.func.Predicate;
import util.func._Predicate;

/*
 * Predicate whether the ultrasonic sensor detects a reflecting object within a distance within the interval
 */
public class DistanceInInterval extends _Predicate<Bot> implements Predicate<Bot> {

	float l, h; 
	
	public DistanceInInterval(float h) {
		this(0, h);
	}
	public DistanceInInterval(float l, float h) {
		assert l <= h;
		this.l = l;
		this.h = h;
	}
	
	// minimum distance of l
	public static Predicate<Bot> min(float l){
		return new DistanceInInterval(l, Float.POSITIVE_INFINITY);
	}
	// distance within interval [l,h]
	public static Predicate<Bot> in(float l, float h){
		return new DistanceInInterval(l, h);
	}
	// maximum distance of h
	public static Predicate<Bot> max(float h){
		return new DistanceInInterval(h);
	}
	// distance close to center
	public static Predicate<Bot> close(float center, float epsilon) {
		return new DistanceInInterval(center - epsilon, center + epsilon);
	}

	@Override
	public Boolean exec(Bot t) {
		float distance = t.sensors.getDistance();
		return l<=distance && h>=distance;
	}

}
