package com.sun.tranquilo.generator;

import java.util.Random;
import com.sun.tranquilo.grammar.trex.TREXPatternPool;

public class GeneratorOption
{
	/** random number generator. */
	public Random random;
	/**
	 * if the generated element exceeds this depth,
	 * the generator tries to cut back.
	 */
	public int cutBackDepth = 0;
	
	/**
	 * this object is responsible to calculate how many times '*' or '+' is repeated.
	 */
	public Rand width;

	public NameGenerator nameGenerator;
	public DataTypeGenerator dtGenerator;
	
	public TREXPatternPool pool;
	
	/**
	 * fills unspecified parameters by default values.
	 */
	public void fillInByDefault()
	{
		if( random==null )			random = new Random();
		if( cutBackDepth==0 )		cutBackDepth=5;
		if( width==null )			width = new Rand.UniformRand( random, 3 );
		if( nameGenerator==null )	nameGenerator = new NameGenerator(random);
		if( dtGenerator==null )		dtGenerator = new DataTypeGeneratorImpl();
		if( pool==null )			pool = new TREXPatternPool();
	}
}
