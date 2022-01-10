package util.state;

import bot.Bot;

public abstract class ImmediateAction implements Action {

	@Override
	public void stop(Bot bot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean finished(Bot bot) {
		return true;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
