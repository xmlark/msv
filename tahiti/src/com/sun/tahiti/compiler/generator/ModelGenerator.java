/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler.generator;

import com.sun.msv.grammar.*;
import com.sun.tahiti.compiler.Symbolizer;
import com.sun.tahiti.grammar.*;

public interface ModelGenerator
{
	/**
	 * generate object models.
	 */
	void generate( AnnotatedGrammar grammar, Symbolizer symbolizer, Controller controller ) throws Exception ;
	
	public static final ModelGenerator xmlGenerator = new ModelGenerator() {
		public void generate( AnnotatedGrammar grammar, Symbolizer symbolizer, Controller controller ) throws Exception {
			new XMLGenerator(grammar,symbolizer,controller).generate();
		}
	};
	public static final ModelGenerator javaGenerator = new ModelGenerator() {
		public void generate( AnnotatedGrammar grammar, Symbolizer symbolizer, Controller controller ) throws Exception {
			new JavaGenerator(grammar,symbolizer,controller).generate();
		}
	};
}
