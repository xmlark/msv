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

import org.relaxng.datatype.Datatype;

/**
 * generates an text value that matchs to a datatype.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface DataTypeGenerator {
	String generate( Datatype dt, ContextProviderImpl context );
	
	public static class GenerationException extends RuntimeException {
		public GenerationException( String msg ) {
			super(msg);
		}
	}
}
