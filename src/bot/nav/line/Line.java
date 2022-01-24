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
import static bot.Driver.*;

public class Line {
	
	static State online;
	static State rotToRFull; static State rotToLFull;
	static State skipline;
	static State obstacle;
	static int lastSuccessLR = 0;
	static int lastLR = 0;
	static float[]escalator={10,90,90};
	static {
		online = new OnlineState();
		rotToRFull = new RotToXFullState(-1);rotToLFull = new RotToXFullState(1);
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
				StringBuilder outus = new StringBuilder();
				for(int i=0; i<executor.getHistory().size(); i++) {
					outus.append(executor.getHistory().get(i).getClass().getSimpleName()).append('\n');
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
			return CollUtil.listOf(State.END,rotToLFull,rotToRFull,obstacle);
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
			this.nextFinalizingAction = new Drive_Action((escalator[escalator.length-1]+DEGREE_EPSILON) / DIST_TO_DEG,DEFAULT_TURN_SPEED,-flip*90);
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
			float rot = flip*direction*90;
			actions.add(new Drive_Action((last+esc) / DIST_TO_DEG,LINE_TURN_CROSSING_DETECTION_SPEED, rot));
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
	
	public static class SetLastSuccessToCurAction extends ImmediateAction {

		public static SetLastSuccessToCurAction ins = new SetLastSuccessToCurAction(); 
		
		@Override
		public void start(Bot bot) {
			lastSuccessLR = lastLR;
		}
		
	}
}
