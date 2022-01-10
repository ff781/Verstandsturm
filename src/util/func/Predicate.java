package util.func;

public interface Predicate<T> extends Function<T,Boolean> {	
	
	public static class False<T> implements Predicate<T> {
		@Override
		public Boolean exec(T t) {
			return false;
		}}
	public static class True<T> implements Predicate<T> {
		@Override
		public Boolean exec(T t) {
			return true;
		}}
}
