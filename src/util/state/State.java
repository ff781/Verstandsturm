package util.state;

import bot.*;

import java.util.*;
import util.func.*;

//represents a state in a state machine
public class State {
	
	//represents an ending state, from which no further action is taken
	public static final State END = new State();
	
	//action that can be executed until termination or interruption
	public Action action;
	//list of all edge predicates from this state
	public List<Predicate<Bot>> edgePreds;
	//list of all edge targets from this state
	public List<State> edgeTars;
	//finalizing actions executed when the given edge is taken
	public Map<Integer,Action> edgeFinalizingActions;
	//next state if the action fully completes
	public State next;
	//an action that is executed if the state through action end, has to terminate in finite time
	public Action nextFinalizingAction;
	
	private State() {}
	
	public State(Action action) {
		this(action, Collections.<Predicate<Bot>>emptyList());
	}

	public State(Action action, List<Predicate<Bot>> edgePreds, List<State> edgeTars) {
		this.action = action;
		this.edgePreds = edgePreds;
		this.edgeTars = edgeTars;
	}
	
	public State(Action action, List<Predicate<Bot>> edgePreds) {
		this(action, edgePreds, Collections.<State>emptyList());
	}

	public List<State> edgeTars() {
		return this.edgeTars;
	}

	public Map<Integer,Action> edgeFinalizingActions() {
		return this.edgeFinalizingActions==null ? Collections.EMPTY_MAP : this.edgeFinalizingActions;
	}
	
	public Action nextFinalizingAction() {
		return this.nextFinalizingAction;
	}
	
	public State next() {
		return this.next;
	}
	
}
