package com.sun.tahiti.grammar;

public class Multiplicity {
	public final int min;
	public final Integer max;	// null is used to represent "unbounded".
			
	public Multiplicity( int min, Integer max ) {
		this.min = min; this.max = max;
	}
	public Multiplicity( int min, int max ) {
		this.min = min; this.max = new Integer(max);
	}
	
	/** returns true if the multiplicity is (1,1). */
	public boolean isUnique() {
		if(max==null)	return false;
		return min==1 && max.intValue()==1;
	}
	
	/** the constant representing the (0,0) multiplicity. */
	public static Multiplicity zero = new Multiplicity(0,0);
	
	/** the constante representing the (1,1) multiplicity. */
	public static Multiplicity one = new Multiplicity(1,1);
	
// arithmetic methods
//============================
	public static Multiplicity choice( Multiplicity lhs, Multiplicity rhs ) {
		return new Multiplicity(
			Math.min(lhs.min,rhs.min),
			(lhs.max==null||rhs.max==null)?
				null:
				new Integer(Math.max(lhs.max.intValue(),rhs.max.intValue())) );
	}
	public static Multiplicity group( Multiplicity lhs, Multiplicity rhs ) {
		return new Multiplicity( lhs.min+rhs.min,
			(lhs.max==null||rhs.max==null)?
				null:
				new Integer(lhs.max.intValue()+rhs.max.intValue()) );
	}
	public static Multiplicity oneOrMore( Multiplicity c ) {
		if(c.max==null)				return c; // (x,*) => (x,*)
		if(c.max.intValue()==0 )	return c; // (0,0) => (0,0)
		else		return new Multiplicity( c.min, null );	// (x,y) => (x,*)
	}
}
	
