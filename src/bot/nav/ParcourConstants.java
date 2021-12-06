package bot.nav;

import bot.Bot;
import lejos.robotics.Color;
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
	
	public static final float USS_CENTER_TO_BOT_REAR_DISTANCE = 13;
	
	public static final float COLOR_SENSOR_TO_BOT_REAR_DISTANCE = 14; 
	
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
	
	public static final float COLOR_TOLERANCE = .2f;
	
	public static final float[]LINE_BLUE = {.0255f,.11f,.06f};
	
	public static final float[]LINE_WHITE = {.0163f,.283f,.1313f};
	
	public static final float[]LINE_BROWN = {.02f,.0365f,.0039f};
	
	public static final int LINE_BLUE_I = 0;
	public static final int LINE_WHITE_I = 1;
	public static final int LINE_BROWN_I = 2;
	public static final float[][]ALL_COLORS = {LINE_BLUE,LINE_WHITE,LINE_BROWN,};
	
	public static boolean colorMatch(float[]a,float[]b) {
		return colorMatch(a,b,1);
	}
	public static boolean colorMatch(float[]a,float[]b,float toleranceFactor) {
		return Meth.length(Meth.sub(a, b)) < COLOR_TOLERANCE * toleranceFactor; 
	}
	
	public static float colorBinaryClassify(float[] rgb, float[]a, float[]b) {
		  float result = 0f;
		  
		  for (int i = 0; i < rgb.length; i++) {
			  result += (rgb[i] - a[i]) / (a[i] - b[i]);
		  }
		  
		  result /= 3.;
		  
		  return result;
	  }
	
	public static float colorBinaryClassify_(float[]rgb, float[]a, float[]b) {
		float distAToB = Meth.length(Meth.sub(b, a));
		float distToA = Meth.length(Meth.sub(rgb, a));
		float distToB = Meth.length(Meth.sub(rgb, b));
		return (distToA - distToB) / distAToB;
	}
	
	public static int colorClassify(float[]rgb, float[]...cs) {
		float[]cc = null;
		float min = Float.POSITIVE_INFINITY;
		int ci = -1;
		for(int i : Meth.intRange(0, cs.length)) {
			float[] c = cs[i];
			float dist = Meth.dist(rgb, c);
			if(dist < min) {
				cc = c;
				min = dist;
				ci = i;
			}
		}
		return ci;
	}
	
	public static final float SKIP_LINE_JITTER_DISTANCE = 4;
	public static final float SKIP_LINE_JITTER_SPEED = 1;
	public static final float SKIP_LINE_JITTER_ANGEL = 7;
	
	public static String rgbInfo(Bot bot) {
		return rgbToString(bot.sensors.getRGB());
	}
	
	public static String rgbToString(float[]rgb) {
		return String.format("%.2f,%.2f,%.2f", rgb[0],rgb[1],rgb[2]); 
	}
	
	public static String colorIDToString(int c) {
		switch(c) {
		case Color.RED: return "RED";
		case Color.GREEN: return "GREEN";
		case Color.BLUE: return "BLUE";
		case Color.YELLOW: return "YELLOW";
		case Color.MAGENTA: return "MAGENTA";
		case Color.ORANGE: return "ORANGE";
		case Color.WHITE: return "WHITE";
		case Color.BLACK: return "BLACK";
		case Color.PINK: return "PINK";
		case Color.GRAY: return "GRAY";
		case Color.LIGHT_GRAY: return "LIGHT_GRAY";
		case Color.DARK_GRAY: return "DARK_GRAY";
		case Color.CYAN: return "CYAN";
		case Color.BROWN: return "BROWN";
		case Color.NONE: return "NONE";
		}
		return "unknown color id " + c;
	}

	//#angels
	
	public static final float ANGEL_ABS_TOLERANCE = 5;
}
