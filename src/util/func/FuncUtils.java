package util.func;

public class FuncUtils {
	
	private FuncUtils() {}
	
	public static <S,T,R> Function<T,R> exec(Function2<S,T,R> f, S s){
		return new PartialFunction<>(f, s);
	}
	
	public static class PartialFunction<S,T,R> implements Function <T,R> {
		
		Function2<S,T,R> f;
		S s;
		public PartialFunction(Function2<S,T,R> f, S s) {
			this.f = f;
			this.s = s;
		}

		@Override
		public R exec(T t) {
			return this.f.exec(s, t);
		}
		
	}
	
}
