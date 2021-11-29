package bot.nav.line;

import bot.Bot;

//action that can be executed until termination or interruption
public interface Action {
	
	//starts the action
	abstract void start(Bot bot);
	//stops the action
	abstract void stop(Bot bot);
	//polls whether the action has finished
	abstract boolean finished(Bot bot);
	//resets the action to be used again
	abstract void reset();

}
