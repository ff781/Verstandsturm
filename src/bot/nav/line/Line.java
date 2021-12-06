package bot.nav.line;

import java.util.*;

import bot.*;
import bot.men.Screen;
import bot.nav.act.*;
import lejos.hardware.Button;
import util.func.*;
import util.coll.*;
import util.state.*;
import static bot.nav.ParcourConstants.*;

public class Line {
	
	static State online;
	static State rotToR; static State rotToL;
	static State rotRToL; static State rotLToR;
	static State skipline;
	static State obstacle;
	static {
		online = new OnlineState();
		rotToR = new RotToRState();rotToL = new RotToLState();
		rotRToL = new RotRToLState();rotLToR = new RotLToRState();
		skipline = new SkiplineState();
	}
	
	public static StateExecutor instantiate(Bot bot) {
		obstacle = new ObstacleState(bot);
		return new StateExecutor(online);
	}
	
	public static void exec(Bot bot) {
		StateExecutor executor = Line.instantiate(bot);
		executor.exec(bot);
	}
	
	public static class ObstacleState extends State {
		public ObstacleState(Bot bot) {
			super(
				Obstacle.instantiate(bot),
				Collections.<Predicate<Bot>>emptyList()
			);
		}
		@Override
		public State next() {
			return online;
		}
	}
	
	public static class OnlineState extends State {
		public OnlineState() {
			super(
					new InfiniteDriveAction(LINE_CROSSING_DETECTION_SPEED),
					CollUtil.<Predicate<Bot>>listOf(
							new FoundLineColor(LINE_BLUE_I),
							new FoundLineColor(LINE_WHITE_I).negate(),
							new FoundLineColor(LINE_WHITE_I).negate(),
							new Touched()
							)
					);
		}
		@Override
		public List<State> edgeTars() {
			return CollUtil.listOf(State.END,rotToR,rotToL,obstacle);
		}
	}
	public static class RotToRState extends State {
		public RotToRState() {
			super(
					new FiniteTurnAction(-90,LINE_TURN_CROSSING_DETECTION_SPEED),
					CollUtil.<Predicate<Bot>>listOf(
							new FoundLineColor(LINE_WHITE_I)
							)
					);
		}

		@Override
		public List<State> edgeTars() {
			return CollUtil.listOf(online);
		}
		@Override
		public State next() {
			return rotRToL;
		}
	}
	public static class RotToLState extends State {
		public RotToLState() {
			super(
					new FiniteTurnAction(90,LINE_TURN_CROSSING_DETECTION_SPEED),
					CollUtil.<Predicate<Bot>>listOf(
							new FoundLineColor(LINE_WHITE_I)
							)
					);
		}
		@Override
		public List<State> edgeTars() {
			return CollUtil.listOf(online);
		}
		@Override
		public State next() {
			return rotLToR;
		}
	}
	public static class RotRToLState extends State {
		public RotRToLState() {
			super(
					new FiniteTurnAction(180,LINE_TURN_CROSSING_DETECTION_SPEED),
					CollUtil.<Predicate<Bot>>listOf(
							new FoundLineColor(LINE_WHITE_I)
							)
					);
		}
		@Override
		public List<State> edgeTars() {
			return CollUtil.listOf(online);
		}
		@Override
		public State next() {
			return skipline;
		}
	}
	public static class RotLToRState extends State {
		public RotLToRState() {
			super(
					new FiniteTurnAction(-180,LINE_TURN_CROSSING_DETECTION_SPEED),
					CollUtil.<Predicate<Bot>>listOf(
							new FoundLineColor(LINE_WHITE_I)
							)
					);
		}
		@Override
		public List<State> edgeTars() {
			return CollUtil.listOf(online);
		}
		@Override
		public State next() {
			return skipline;
		}
	}
	public static class SkiplineState extends State {
		public SkiplineState() {
			super(
					new JitterAction(SKIP_LINE_JITTER_DISTANCE, SKIP_LINE_JITTER_SPEED, SKIP_LINE_JITTER_ANGEL),
					CollUtil.<Predicate<Bot>>listOf(
							new FoundLineColor(LINE_WHITE_I)
							)
					);
		}
		@Override
		public List<State> edgeTars() {
			return CollUtil.listOf(online);
		}
	}
}
