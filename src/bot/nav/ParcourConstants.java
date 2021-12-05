package bot.nav;

import util.meth.Meth;

public class ParcourConstants {
	
	private ParcourConstants() {}
	
	//#distances
	
	public static final float DISTANCE_TOLERANCE = .1f;
	
	//really big number :O that represents virtually infinite (centimeters or degrees)
	public static final float VIRTUALLY_INFINITE = 10e6f;
	
	public static final float OBSTACLE_DETECTION_DISTANCE = 10f;
	
	public static final float OBSTACLE_BACKOFF_DISTANCE = 7f;
	
	public static final float PEEKING_OVER_OBSTACLE_EDGE_DISTANCE_MIN = 15f;
	
	public static final float CRAWL_TOWARDS_OBSTACLE_EDGE_STEP = 5;
	
	public static final float CRAWL_TOWARDS_OBSTACLE_EDGE_DISTANCE_MAX = 25;
	
	//TODO
	public static final float USS_CENTER_TO_BOT_REAR_DISTANCE = 11;
	
	public static final float COLOR_SENSOR_TO_BOT_REAR_DISTANCE = 13; 
	
	public static final boolean distanceMatch(float is, float goal) {
		return Math.abs((is-goal)/goal) < DISTANCE_TOLERANCE;
	}
	
	//#speed
	
	//speed at which the robot can detect crossing a line (~3cm wide)
	public static final float LINE_CROSSING_DETECTION_SPEED = 1f;
	
	//speed at which the robot can detect crossing a line by turning (~3cm wide)
	public static final float LINE_TURN_CROSSING_DETECTION_SPEED = .5f;
	
	//speed at which the robot can sufficiently accurately detect the edge of an obstacle while turning
	public static final float LINE_TURN_EDGE_DETECTION_SPEED = 1f;

	public static final float CRAWL_TOWARDS_OBSTACLE_EDGE_SPEED = 1f;

	public static final float DEFAULT_DRIVE_SPEED = 5f;
	
	public static final float DEFAULT_TURN_SPEED = 1f;
	
	public static final float DEFAULT_USS_TURN_SPEED = 1f;
	
	
	//#colors
	
	public static final float COLOR_TOLERANCE = .1f;
	
	public static final float[]LINE_BLUE = {.0255f,.102f,.075f};
	
	public static final float[]LINE_WHITE = {.017f,.28f,.135f};
	
	public static boolean colorMatch(float[]a,float[]b) {
		return colorMatch(a,b,1);
	}
	public static boolean colorMatch(float[]a,float[]b,float toleranceFactor) {
		return Meth.length(Meth.sub(a, b)) < COLOR_TOLERANCE * toleranceFactor; 
	}
	
	public static final float SKIP_LINE_JITTER_DISTANCE = 4;
	public static final float SKIP_LINE_JITTER_SPEED = 1;
	public static final float SKIP_LINE_JITTER_ANGEL = 7;

	//#angels
	
	public static final float ANGEL_ABS_TOLERANCE = 5;
}
