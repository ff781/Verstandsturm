package util.coll;

import java.util.*;

import util.func.Function;

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
	
	public static <A> String toString(Collection<A> c) {
		return toString(c, new Function<A,String>(){
			@Override
			public String exec(A t) {
				return t.toString();
			}
		}); 
	}
	
	public static <A> String toString(Collection<A> c, Function<A,String>mapper) {
		StringBuilder r = new StringBuilder();
		for(A a : c)
			r.append(mapper.exec(a)).append('\n');
		return r.toString();
	}

}
