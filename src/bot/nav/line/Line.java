package bot.nav.line;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import bot.*;
import bot.men.Screen;
import bot.nav.act.*;
import lejos.robotics.Color;
import util.func.*;
import util.coll.*;
import util.state.*;
import static bot.nav.ParcourConstants.*;
import static bot.Driver.*;

public class Line {
	
	static State online;
	static State rotToRFull; static State rotToLFull;
	static State skipline;
	static State obstacle1;
	static State obstacle2;
	static State obstacleFinale;
	static State beepAndStop;
	static int lastSuccessLR = 0;
	static int lastLR = 0;
	static float[]escalator={10,90,90};
	static {
		online = new OnlineState();
		rotToRFull = new RotToXFullState(-1);rotToLFull = new RotToXFullState(1);
		skipline = new SkiplineStateV2();
		obstacle1 = new Obstacle1State();
		obstacle2 = new Obstacle2State();
		obstacleFinale = new ObstacleFinaleStateHC();
		//obstacleFinale = new ObstacleFinaleState();
		beepAndStop = new BeepAndStopState();
	}
	
	public static StateExecutor instantiate(Bot bot) {
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
				StringBuilder outus = new StringBuilder();
				for(int i=0; i<executor.getHistory().size(); i++) {
					outus.append(executor.getHistory().get(i).getName()).append('\n');
					if(i<executor.getTransitionHistory().size()) {
						outus.append(((Object)executor.getTransitionHistory().get(i)).toString()).append('\n');
					}
				}
				outus.append(executor.extraLogInfo);
				Files.write(file, Arrays.asList(outus.toString()), StandardCharsets.US_ASCII);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
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
							new Touchee()
							)
					);
		}
		@Override
		public List<State> edgeTars() {
			return CollUtil.listOf(State.END,rotToLFull,rotToRFull,obstacle1);
		}
	}
	public static class RotToXFullState extends State {
		public RotToXFullState(int flip) {
			super(
					sophisticatedRotSearch(escalator,flip),
					CollUtil.<Predicate<Bot>>listOf(
							whiteBrown()
							)
					);
			this.name = flip < 0 ? "RotToR" : "RotToL";
			this.nextFinalizingAction = new Drive_Action((escalator[escalator.length-1]) / DIST_TO_DEG,DEFAULT_TURN_SPEED,-flip*Driver.LEFT_DEGREES);
			this.edgeFinalizingActions = new HashMap<>();
			List<Action>actions = new ArrayList<>();
			actions.add(SetLastSuccessToCurAction.ins);
			this.edgeFinalizingActions.put(0, ActionUtil.concat(actions));	
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
	public static Action sophisticatedRotSearch(float[]escalator, final float direction) {
		List<Action> actions = new ArrayList<>();
		float last = 0;
		for(int i=0;i<escalator.length;i++) {
			float esc = escalator[i];
			final float flip = i%2==0 ? 1 : -1;
			actions.add(new ImmediateAction() {
				@Override
				public void start(Bot bot) {
					lastLR = (int)(flip * direction);
				}});
			float rot = flip*direction*LEFT_DEGREES;
			actions.add(new Drive_Action((last+esc) / DIST_TO_DEG, LINE_TURN_CROSSING_DETECTION_SPEED, rot));
			last = esc;
		}
		return ActionUtil.concat(actions);
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
	public static class SkiplineStateV2 extends State {

		static final float searchAngel = 85;
		static final float searchDistance = 18;
		
		public SkiplineStateV2() {
			super(
					ActionUtil.multiply(3, 
							ActionUtil.concat(
									Arrays.<Action>asList(
											new Drive_Action(searchDistance, DEFAULT_DRIVE_SPEED, FORWARD_DEGREES),
											new Drive_Action(searchAngel / DIST_TO_DEG, LINE_TURN_CROSSING_DETECTION_SPEED,LEFT_DEGREES),
											new Drive_Action((searchAngel * 2) / DIST_TO_DEG, LINE_TURN_CROSSING_DETECTION_SPEED, RIGHT_DEGREES),
											new Drive_Action(searchAngel / DIST_TO_DEG, LINE_TURN_CROSSING_DETECTION_SPEED,LEFT_DEGREES)
											)
									)
							),
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
	
	public static class Obstacle1State extends State {

		public Obstacle1State() {
			super(
					ActionUtil.concat(
							Arrays.<Action>asList(
									new Drive_Action(OBSTACLE1_BACKOFF_DISTANCE, DEFAULT_DRIVE_SPEED, BACKWARD_DEGREES ),
									new Drive_Action(OBSTACLE_ROT_DEGREES / DIST_TO_DEG, DEFAULT_TURN_SPEED, RIGHT_DEGREES),
									new Drive_Action(OBSTACLE_SLIDEWAYS_DISTANCE, DEFAULT_DRIVE_SPEED, FORWARD_DEGREES),
									new Drive_Action(OBSTACLE_ROT_DEGREES / DIST_TO_DEG, DEFAULT_TURN_SPEED, LEFT_DEGREES)
									)
						),
					Arrays.<Predicate<Bot>>asList(
							
							)
					);
		}
		
		@Override
		public State next() {
			return obstacle2;
		}

		@Override
		public List<State> edgeTars() {
			return CollUtil.listOf();
		}
		
	}
	
	public static class Obstacle2State extends State {
		public Obstacle2State() {
			super(
				ActionUtil.concat(
						Arrays.<Action>asList(
								new Drive_Action(OBSTACLE_TO_2ND_CRASH_DISTANCE, DEFAULT_DRIVE_SPEED, FORWARD_DEGREES)
								)
						),
				Arrays.<Predicate<Bot>>asList(
						new Touchee()
						)
				);
		}
		
		@Override
		public State next() {
			return obstacleFinale;
		}

		@Override
		public List<State> edgeTars() {
			return Arrays.asList(obstacleFinale);
		}
	}
	
	public static class ObstacleFinaleStateHC extends State {
		public ObstacleFinaleStateHC() {
			super(
				ActionUtil.concat(
						Arrays.<Action>asList(
								new Drive_Action(OBSTACLE1_BACKOFF_DISTANCE, DEFAULT_DRIVE_SPEED, BACKWARD_DEGREES),
								new Drive_Action(OBSTACLE_ROT_DEGREES / DIST_TO_DEG, DEFAULT_TURN_SPEED, LEFT_DEGREES),
								new Drive_Action(OBSTACLE_SLIDEWAYS_DISTANCE, DEFAULT_DRIVE_SPEED, FORWARD_DEGREES),
								new Drive_Action(OBSTACLE_ROT_DEGREES / DIST_TO_DEG, DEFAULT_TURN_SPEED, RIGHT_DEGREES),
								new Drive_Action(OBSTACLE_ENDSPURT_DISTANCE, DEFAULT_DRIVE_SPEED, FORWARD_DEGREES),
								new BeepAction()
								)
						),
				Arrays.<Predicate<Bot>>asList(
						)
					);
		}
		
		@Override
		public State next() {
			Screen.beep();
			return State.END;
		}

		@Override
		public List<State> edgeTars() {
			return Arrays.asList();
		}
	}
	
	public static class ObstacleFinaleState extends State {

		public ObstacleFinaleState() {
			super(
				ActionUtil.concat(
						Arrays.<Action>asList(
								new Drive_Action(OBSTACLE1_BACKOFF_DISTANCE, DEFAULT_DRIVE_SPEED, BACKWARD_DEGREES),
								new Drive_Action(OBSTACLE_ROT_DEGREES / DIST_TO_DEG, DEFAULT_TURN_SPEED, LEFT_DEGREES),
								new Drive_Action(OBSTACLE_SLIDEWAYS_DISTANCE, DEFAULT_DRIVE_SPEED, FORWARD_DEGREES),
								new Drive_Action(OBSTACLE_ROT_DEGREES / DIST_TO_DEG, DEFAULT_TURN_SPEED, RIGHT_DEGREES),
								new Drive_Action(OBSTACLE_ENDSPURT_DISTANCE, DEFAULT_DRIVE_SPEED, FORWARD_DEGREES)
								)
						),
				Arrays.<Predicate<Bot>>asList(
						new FoundLineColorID(Color.BLUE)
						)
					);
		}
		
		@Override
		public State next() {
			return beepAndStop;
		}

		@Override
		public List<State> edgeTars() {
			return Arrays.asList(beepAndStop);
		}
	}
	
	public static class BeepAndStopState extends State {
		
		public BeepAndStopState() {
			super(
				ActionUtil.concat(
						Arrays.<Action>asList(
								new BeepAction()
								)
						),
				Arrays.<Predicate<Bot>>asList(
						)
					);
			this.next = State.END;
		}
		
	}
	
	public static class SetLastSuccessToCurAction extends ImmediateAction {

		public static SetLastSuccessToCurAction ins = new SetLastSuccessToCurAction(); 
		
		@Override
		public void start(Bot bot) {
			lastSuccessLR = lastLR;
		}
		
	}
}
