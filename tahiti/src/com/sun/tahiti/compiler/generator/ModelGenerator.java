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
	 * generate models (classes that represents the object model).
	 */
	void generate( Expression exp, String grammarClassName, Symbolizer symbolizer, OutputResolver resolver ) throws Exception ;
	
	public static final ModelGenerator xmlGenerator = new ModelGenerator() {
		public void generate( Expression exp, String grammarClassName, Symbolizer symbolizer, OutputResolver resolver ) throws Exception {
			new XMLGenerator(exp,grammarClassName,symbolizer,resolver).generate();
		}
	};
	public static final ModelGenerator javaGenerator = new ModelGenerator() {
		public void generate( Expression exp, String grammarClassName, Symbolizer symbolizer, OutputResolver resolver ) throws Exception {
			new JavaGenerator(exp,grammarClassName,symbolizer,resolver).generate();
		}
	};
}
