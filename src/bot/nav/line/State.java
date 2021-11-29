package bot.nav.line;

import bot.*;

import java.util.*;
import util.func.*;

//represents a state in a state machine
public class State {
	
	//represents an ending state, from which no further action is taken
	public static final State END = new State();
	
	//action that can be executed until termination or interruption
	public Action action;
	//list of all edges from this state machine
	public List<Function<Bot, State>> edges;
	//on end
	public State next;
	
	private State() {
		super();	
	}
	
	public State(Action action, List<Function<Bot, State>> edges) {
		this();
		this.action = action;
		this.edges = edges;
	}
	
}
