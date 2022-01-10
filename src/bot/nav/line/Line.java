package bot.nav.line;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import bot.*;
import bot.nav.act.*;
import util.func.*;
import util.coll.*;
import util.state.*;
import static bot.nav.ParcourConstants.*;

public class Line {
	
	static State online;
	static State rotToRFull; static State rotToLFull;
	static State rotToR; static State rotToL;
	static State rotRToL; static State rotLToR;
	static State skipline;
	static State obstacle;
	static int lastSuccessLR = 0;
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
	
	public static void exec(Bot bot, boolean debug) {
		StateExecutor executor = Line.instantiate(bot);
		executor.render = true;
		executor.setHistorian(debug);
		executor.exec(bot);
		if(debug) {
			String fn = "line_log.txt";
			Path file = Paths.get(fn);
			try {
				Files.write(file, Arrays.asList(CollUtil.toString(executor.getHistory())), StandardCharsets.US_ASCII);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
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
							new Predicate.False<Bot>(),
							brownWhite().and(new Predicate<Bot>() {
								@Override
								public Boolean exec(Bot t) {
									return lastSuccessLR>=0;
								}
							}),
							brownWhite().and(new Predicate<Bot>() {
								@Override
								public Boolean exec(Bot t) {
									return lastSuccessLR<=0;
								}
							}),
							new Touched()
							)
					);
		}
		@Override
		public List<State> edgeTars() {
			return CollUtil.listOf(State.END,rotToR,rotToL,obstacle);
		}
	}
	public static class RotToRFullState extends State {
		public RotToRFullState() {
			super(
					new FiniteTurnAction(-90,LINE_TURN_CROSSING_DETECTION_SPEED),
					CollUtil.<Predicate<Bot>>listOf(
							whiteBrown()
							)
					);
			this.edgeFinalizingActions = new HashMap<>();
			this.edgeFinalizingActions.put(0, SetLastSuccessAction.right());
		}
	}
	public static class RotToLFullState extends State {
		public RotToLFullState() {
			super(
					new FiniteTurnAction(-90,LINE_TURN_CROSSING_DETECTION_SPEED),
					CollUtil.<Predicate<Bot>>listOf(
							whiteBrown()
							)
					);
			this.edgeFinalizingActions = new HashMap<>();
			this.edgeFinalizingActions.put(0, SetLastSuccessAction.right());
		}
	}
	public static class RotToRState extends State {
		public RotToRState() {
			super(
					new FiniteTurnAction(-90,LINE_TURN_CROSSING_DETECTION_SPEED),
					CollUtil.<Predicate<Bot>>listOf(
							whiteBrown()
							)
					);
			this.edgeFinalizingActions = new HashMap<>();
			this.edgeFinalizingActions.put(0, SetLastSuccessAction.right());
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
							whiteBrown()
							)
					);
			this.edgeFinalizingActions = new HashMap<>();
			this.edgeFinalizingActions.put(0, SetLastSuccessAction.left());
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
							whiteBrown()
							)
					);
			
			this.nextFinalizingAction = new FiniteTurnAction(-(90+DEGREE_EPSILON),DEFAULT_TURN_SPEED);

			this.edgeFinalizingActions = new HashMap<>();
			this.edgeFinalizingActions.put(0, SetLastSuccessAction.left());
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
							whiteBrown()
							)
					);
			this.nextFinalizingAction = new FiniteTurnAction(90+DEGREE_EPSILON,DEFAULT_TURN_SPEED);

			this.edgeFinalizingActions = new HashMap<>();
			this.edgeFinalizingActions.put(0, SetLastSuccessAction.right());
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
							whiteBrown()
							)
					);
		}
		@Override
		public List<State> edgeTars() {
			return CollUtil.listOf(online);
		}
	}
	
	public static class SetLastSuccessAction extends ImmediateAction {
		int target;
		
		public SetLastSuccessAction(int target) {
			super();
			this.target = target;
		}

		@Override
		public void start(Bot bot) {
			lastSuccessLR = this.target;
		}
		
		public static SetLastSuccessAction left() {
			return new SetLastSuccessAction(-1);
		}
		public static SetLastSuccessAction right() {
			return new SetLastSuccessAction(1);
		}
		
	}
}
