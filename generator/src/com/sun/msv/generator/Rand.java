/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.generator;

import java.util.Random;

/**
 * creates random integer.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface Rand
{
	int next();
	
	/**
	 * uniform distribution of [0,x).
	 * 
	 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
	 */
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
