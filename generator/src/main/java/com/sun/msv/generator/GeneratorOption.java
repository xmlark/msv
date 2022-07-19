/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and  use in  source and binary  forms, with  or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions  of  source code  must  retain  the above  copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution  in binary  form must  reproduct the  above copyright
 *   notice, this list of conditions  and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither  the  name   of  Sun  Microsystems,  Inc.  or   the  names  of
 * contributors may be  used to endorse or promote  products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS  OR   IMPLIED  CONDITIONS,  REPRESENTATIONS   AND  WARRANTIES,
 * INCLUDING  ANY  IMPLIED WARRANTY  OF  MERCHANTABILITY,  FITNESS FOR  A
 * PARTICULAR PURPOSE  OR NON-INFRINGEMENT, ARE HEREBY  EXCLUDED. SUN AND
 * ITS  LICENSORS SHALL  NOT BE  LIABLE  FOR ANY  DAMAGES OR  LIABILITIES
 * SUFFERED BY LICENSEE  AS A RESULT OF OR  RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF  THE SOFTWARE OR ITS DERIVATIVES.  IN NO EVENT WILL
 * SUN OR ITS  LICENSORS BE LIABLE FOR ANY LOST  REVENUE, PROFIT OR DATA,
 * OR  FOR  DIRECT,   INDIRECT,  SPECIAL,  CONSEQUENTIAL,  INCIDENTAL  OR
 * PUNITIVE  DAMAGES, HOWEVER  CAUSED  AND REGARDLESS  OF  THE THEORY  OF
 * LIABILITY, ARISING  OUT OF  THE USE OF  OR INABILITY TO  USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.sun.msv.generator;

import java.util.Random;

import com.sun.msv.grammar.ExpressionPool;

/**
 * set of options that controls generation behavior.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
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
	
	public ExpressionPool pool;
	
	/**
	 * probability of "sequencing error" per # of sequences.
	 * 0: no error, 1.0: error at every occurence of sequence.
	 * 
	 * sequencing error is to generate B,A where its content model is A,B.
	 */
	public double probSeqError = 0;
	
	/**
	 * probability of "missing element error" per # of elements.
	 * Missing element error is to generate epsilon where its content model is A.
	 */
	public double probMissingElemError = 0;
	
	/**
	 * probability of "missing plus error" per # of '+' operator.
	 * Missing plus error is to generate epsilon where the content model is A+.
	 * Similar to "missing element error"
	 */
	public double probMissingPlus = 0;
	
	/**
	 * probability of "slip-in element error" per # of elements.
	 * 
	 * slip-in element error is to generate a random element before valid element X.
	 */
	public double probSlipInElemError = 0;

	/**
	 * probability of "mutated element error" per # of elements.
	 * 
	 * mutated element error is to replace a valid element by a random element.
	 * Or this error can be considered as combination of "missing element error"
	 * and "slip-in element error".
	 */
	public double probMutatedElemError = 0;
	
	/**
	 * probability of "missing attribute error" per # of attributes.
	 */
	public double probMissingAttrError = 0;
	
	/**
	 * probability of "slip-in attribute error" per # of attributes.
	 * 
	 * slip-in attribute error is to generate a random attribute before valid attribute X.
	 */
	public double probSlipInAttrError = 0;

	/**
	 * probability of "mutated attribute error" per # of attributes.
	 * 
	 * mutated attribute error is to replace a valid attribute by a random attribute.
	 * Or this error can be considered as combination of "missing attribute error"
	 * and "slip-in attribute error".
	 */
	public double probMutatedAttrError = 0;
	
	/**
	 * probability of "greedy choice error" per # of choice.
	 * 
	 * greedy choice error is to generate A,B (or B,A) where its content model is (A|B).
	 */
	public double probGreedyChoiceError = 0;
	
	/**
	 * probability of "element name typo" per # of elements.
	 */
	public double probElemNameTypo = 0;
	
	/**
	 * probability of "attribute name typo" per # of attributes.
	 */
	public double probAttrNameTypo = 0;
	
	/**
	 * if set to true, comments are inserted whenever an error is generated.
	 */
	public boolean insertComment = true;
	
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
		if( pool==null )			pool = new ExpressionPool();
	}
	
	public boolean errorSpecified()
	{
		return probGreedyChoiceError!=0
			|| probMissingAttrError!=0
			|| probMissingElemError!=0
			|| probMissingPlus!=0
			|| probMutatedAttrError!=0
			|| probMutatedElemError!=0
			|| probSeqError!=0
			|| probSlipInAttrError!=0
			|| probSlipInElemError!=0;
	}
}
