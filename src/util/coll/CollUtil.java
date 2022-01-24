package util.coll;

import java.util.*;

//java 1.7 incident :(
public class CollUtil {
	
	private CollUtil() {}

	@SafeVarargs
	public static <B> List<B> listOf(B...as){
		if(as.length==0) return Collections.emptyList();
		ArrayList<B> r = new ArrayList<>(as.length);
		for(B a:as)r.add(a);
		return r;
	}

}
