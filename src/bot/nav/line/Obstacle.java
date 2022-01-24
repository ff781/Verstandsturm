package bot.nav.line;

import bot.nav.line.Obstacle;
import static bot.nav.ParcourConstants.*;

import java.util.*;

import bot.Bot;
import bot.Driver;
import bot.nav.act.*;
import util.coll.CollUtil;
import util.func.*;
import util.state.*;

public class Obstacle {
	
	static State detectingEdge;
	static State driveAroundEdge;
	static State driveAroundNextEdge;
	static State mergeWithLine;

	static {
		
	}
	
	public static StateExecutor instantiate(Bot bot) {
		return new StateExecutor(detectingEdge);
	}
	
	public static void exec(Bot bot) {
		Line.instantiate(bot).exec(bot);
	}
	
	public static class DetectingEdgeState extends State {
		public DetectingEdgeState () {
			super(
					ActionUtil.concat(CollUtil.<Action>listOf(
							new GyrosResetAction(),
							new FiniteDriveAction(OBSTACLE_BACKOFF_DISTANCE, LINE_CROSSING_DETECTION_SPEED, Driver.BACKWARD_DEGREES) {
							},
							new FiniteTurnAction(-(90+ANGEL_ABS_TOLERANCE), LINE_TURN_EDGE_DETECTION_SPEED)
							)),
					CollUtil.<Predicate<Bot>>listOf(
							new GyrosAngel(-90, ANGEL_ABS_TOLERANCE)
							)
					);
		}
		@Override
		public List<State> edgeTars() {
			return CollUtil.listOf(driveAroundEdge);
		}
		
	}
	
	public static class DriveAroundEdgeState extends State {
		public DriveAroundEdgeState () {
			super(
					new ThreadBoundAction(new _TF()));
		}
		static class _T extends Thread {
			public Bot bot;
			public boolean[] stop;
			public _T(Bot bot, boolean[] stop) {
				super();
				this.bot = bot;
				this.stop = stop;
			}
			@Override
			public void run() {
				bot.driver.turnUS(90, DEFAULT_USS_TURN_SPEED);
				bot.driver.drive_(CRAWL_TOWARDS_OBSTACLE_EDGE_DISTANCE_MAX, CRAWL_TOWARDS_OBSTACLE_EDGE_SPEED, Driver.FORWARD_DEGREES, false);
				Predicate<Bot> predator = DistanceInInterval.min(PEEKING_OVER_OBSTACLE_EDGE_DISTANCE_MIN);
				while(!stop[0] || predator.exec(bot)) {
				}
				bot.driver.driveStop();
				bot.driver.drive_(USS_CENTER_TO_BOT_REAR_DISTANCE, DEFAULT_DRIVE_SPEED, Driver.FORWARD_DEGREES);
				bot.driver.turn(90 + ANGEL_ABS_TOLERANCE, DEFAULT_TURN_SPEED, false);
				predator = new GyrosAngel(0, ANGEL_ABS_TOLERANCE);
				while(!stop[0] || predator.exec(bot));
			}
		}
		static class _TF implements Function2<Bot, boolean[], Thread> {
			@Override
			public Thread exec(Bot s, boolean[] t) {
				return new _T(s, t);
			}
			
		}
		@Override
		public State next() {
			return driveAroundNextEdge;
		}		
	}
	
	public static class DriveAroundNextEdgeState extends State {
		public DriveAroundNextEdgeState () {
			super(
					new ThreadBoundAction(new _TF()));
		}
		static class _T extends Thread {
			public Bot bot;
			public boolean[] stop;
			public _T(Bot bot, boolean[] stop) {
				super();
				this.bot = bot;
				this.stop = stop;
			}
			@Override
			public void run() {
				bot.driver.drive_(CRAWL_TOWARDS_OBSTACLE_EDGE_DISTANCE_MAX, CRAWL_TOWARDS_OBSTACLE_EDGE_SPEED, Driver.FORWARD_DEGREES, false);
				Predicate<Bot> predator = DistanceInInterval.min(PEEKING_OVER_OBSTACLE_EDGE_DISTANCE_MIN);
				while(!stop[0] || predator.exec(bot)) {
				}
				bot.driver.driveStop();
				bot.driver.drive_(USS_CENTER_TO_BOT_REAR_DISTANCE, DEFAULT_DRIVE_SPEED, Driver.FORWARD_DEGREES);
				bot.driver.turn(90 + ANGEL_ABS_TOLERANCE, DEFAULT_TURN_SPEED, false);
				predator = new GyrosAngel(90, ANGEL_ABS_TOLERANCE);
				while(!stop[0] || predator.exec(bot));
			}
		}
		static class _TF implements Function2<Bot, boolean[], Thread> {
			@Override
			public Thread exec(Bot s, boolean[] t) {
				return new _T(s, t);
			}
			
		}
		@Override
		public State next() {
			return mergeWithLine;
		}		
	}

	public static class MergeWithLineState extends State {

		public MergeWithLineState() {
			super(new ThreadBoundAction(new _TF()));
			this.next = State.END;
		}
		static class _T extends Thread {
			public Bot bot;
			public boolean[] stop;
			public _T(Bot bot, boolean[] stop) {
				super();
				this.bot = bot;
				this.stop = stop;
			}
			@Override
			public void run() {
				bot.driver.drive_(CRAWL_TOWARDS_OBSTACLE_EDGE_DISTANCE_MAX, LINE_CROSSING_DETECTION_SPEED, Driver.FORWARD_DEGREES, false);
				Predicate<Bot> predator = new FoundLineColor(LINE_WHITE_I);
				while(!stop[0] || predator.exec(bot)) {
				}
				bot.driver.driveStop();
				bot.driver.drive_(COLOR_SENSOR_TO_BOT_REAR_DISTANCE, DEFAULT_DRIVE_SPEED, Driver.FORWARD_DEGREES);
				bot.driver.turn(-(90 + ANGEL_ABS_TOLERANCE), LINE_TURN_CROSSING_DETECTION_SPEED, false);
				while(!stop[0] || predator.exec(bot));
			}
		}
		static class _TF implements Function2<Bot, boolean[], Thread> {
			@Override
			public Thread exec(Bot s, boolean[] t) {
				return new _T(s, t);
			}
			
		}
	}
}