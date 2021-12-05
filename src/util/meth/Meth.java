package util.meth;

import java.util.*;

public class Meth{
	
	public static final float angelDist(float degA, float degB) {
		float dist = Math.abs(degA - degB);
		return dist > 180 ? 360 - dist : dist;
	}
	
	public static final float length(float[]a) {
		return pnorm(a, 2);
	}

	//calculates the p-norm of a given vector
	public static final float pnorm(float[]a, int p) {
		if(p==2) {
			float r=0;
			for(float e:a)r+=e*e;
			return (float)Math.sqrt(r);
		}
		throw new RuntimeException("not implemented");
	}
	
	public static final float sub(float[]a,float[]b)[]{
		float[]r = new float[a.length];
		for(int i=0;i<a.length;i++) r[i]=a[i]-b[i];
		return r;
	}
	
	//shuffles a list
	public static final <A> void shuffle(List<A> a) {
		int size = a.size();
		for(int i=size-1;i>0;i--){
			int r = (int)(Math.random()*i);
			swap(a, i, r);
		}
	}

	//swaps two elements in a list
	public static final <A> void swap(List<A> a,int l, int r) {
		A tmp = a.get(l);
		a.set(l, a.get(r));
		a.set(r, tmp);
	}

	//creates int range from start(incl) to stop(excl)
	public static final List<Integer> intRange(int start, int stop){
		List<Integer> r = new ArrayList<>(stop-start);
		for(int i=start;i<stop;i++) r.add(i);
		return r;
	}
	
	public static final float sin(float a) {
		return (float)Math.sin(a);
	}

	public static final float cos(float a) {
		return (float)Math.cos(a);
	}

	public static double radDegRatio = 180/Math.PI;
	public static float sqrtof2 = 1.4142135623730951f;
	
	public static final float radToDeg(float rad) {
		return (float)(rad / radDegRatio);
	}
	
	public static final float degToRad(float deg) {
		return (float)(deg * radDegRatio);
	}
}
