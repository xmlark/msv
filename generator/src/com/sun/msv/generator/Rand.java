package com.sun.tranquilo.generator;

import java.util.Random;

/**
 * creates random integer.
 */
public interface Rand
{
	int next();
	
	public static class UniformRand implements Rand
	{
		private final Random rand;
		private int max;
		
		public UniformRand( Random rand, int max ) {
			this.rand = rand;
			this.max = max;
		}
		public int next() {
			return rand.nextInt(max);
		}
	}
}
