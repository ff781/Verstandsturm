package bot.sen;

public class EmptySensorWrap extends SensorWrap {
	
	boolean ignore = false;
	
	@Override
	public void get() {
		
	}

	@Override
	public float getSample() {
		return 0;
	}

	@Override
	public float[] getSamples() {
		return new float[] {0,0,0};
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public boolean isIgnorant() {
	  boolean r = ignore;
	  if(!ignore) ignore = true;
	  return r;
	}

}
