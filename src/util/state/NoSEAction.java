package util.state;

//no side effect action
//side effects refer to program side effects (i.e. not physical side effects)
public abstract class NoSEAction implements Action {
	//reset does not do anything, since this action does not have any side effects
	public void reset() {}
}
