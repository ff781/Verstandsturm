package util.func;

public abstract class _Predicate<A> implements Predicate<A> {
	
	private _NegatedPredicate negation;

	public _Predicate<A> negate(){
		if(negation==null) {
			negation = new _NegatedPredicate();
		}
		return negation; 
	}
	
	public _Predicate<A> and(Predicate<A> other) {
		return new _AndedPredicate(other);
	}
	
	public class _NegatedPredicate extends _Predicate<A> {

		@Override
		public Boolean exec(A t) {
			return !_Predicate.this.exec(t);
		}
		
		public _Predicate<A> negate(){
			return _Predicate.this; 
		}
		
	}
	
	public class _AndedPredicate extends _Predicate<A> {
		Predicate<A> other;
		public _AndedPredicate(Predicate<A> other) {
			this.other = other;
		}
		@Override
		public Boolean exec(A t) {
			return _Predicate.this.exec(t) && other.exec(t);
		}
		
	}
}
