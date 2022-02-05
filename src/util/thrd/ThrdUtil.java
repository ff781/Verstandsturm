package util.thrd;

public final class ThrdUtil {
	
	private ThrdUtil() {}
	
	public static boolean allCollected(Thread[]as) {
		for(Thread a:as) {
			if(a.isAlive()) return false;
		}
		return true;
	}

}
