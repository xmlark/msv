package com.sun.tahiti.grammar;

public class Cardinality {
	public final int min;
	public final Integer max;	// null is used to represent "unbounded".
			
	public Cardinality( int min, Integer max ) {
		this.min = min; this.max = max;
	}
	public Cardinality( int min, int max ) {
		this.min = min; this.max = new Integer(max);
	}
	
	/** returns true if the cardinality is (1,1). */
	public boolean isUnique() {
		if(max==null)	return false;
		return min==1 && max.intValue()==1;
	}
	
// arithmetic methods
//============================
	public static Cardinality choice( Cardinality lhs, Cardinality rhs ) {
		return new Cardinality(
			Math.min(lhs.min,rhs.min),
			(lhs.max==null||rhs.max==null)?
				null:
				new Integer(Math.max(lhs.max.intValue(),rhs.max.intValue())) );
	}
	public static Cardinality group( Cardinality lhs, Cardinality rhs ) {
		return new Cardinality( lhs.min+rhs.min,
			(lhs.max==null||rhs.max==null)?
				null:
				new Integer(lhs.max.intValue()+rhs.max.intValue()) );
	}
	public static Cardinality oneOrMore( Cardinality c ) {
		if(c.max==null)				return c; // (x,*) => (x,*)
		if(c.max.intValue()==0 )	return c; // (0,0) => (0,0)
		else		return new Cardinality( c.min, null );	// (x,y) => (x,*)
	}
}
	
