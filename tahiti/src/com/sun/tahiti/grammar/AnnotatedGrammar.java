/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.grammar;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import java.util.Set;
import java.util.Collection;

/**
 * An AGM with Tahiti annotation.
 * 
 * This object keeps track of all JavaItems added to the grammar.
 */
public final class AnnotatedGrammar implements Grammar
{
	/**
	 * creates an instance by copying values from the specified grammar.
	 */
	public AnnotatedGrammar( Grammar source ) {
		this( source.getTopLevel(), source.getPool() );
	}
	
	public AnnotatedGrammar( Expression topLevel, ExpressionPool pool ) {
		this.topLevel = topLevel;
		this.pool = pool;
	}
	
	public Expression topLevel;
	public Expression getTopLevel() { return topLevel; }
	
	private final ExpressionPool pool;
	public ExpressionPool getPool() { return pool; }
	
	
	
	/**
	 * the grammar file will be generated as this name.
	 * This field would be something like "com.example.abc.FileName".
	 * It shouldn't have a file extension.
	 * 
	 * This field must be set by the reader.
	 */
	public String grammarName;
	
	
	
	/** all ClassItems in this grammar. */
	public final Set classes = new java.util.HashSet();
	public ClassItem[] getClasses() {
		return (ClassItem[])classes.toArray( new ClassItem[classes.size()] );
	}
	
	/** all InterfaceItems in this grammar. */
	public final Set interfaces = new java.util.HashSet();
	public InterfaceItem[] getInterfaces() {
		return (InterfaceItem[])interfaces.toArray( new InterfaceItem[interfaces.size()] );
	}
	
	
	/** creates a new ClassItem. */
	public ClassItem createClassItem( String typeFullName, Expression body ) {
		ClassItem o = new ClassItem(typeFullName,body);
		classes.add(o);
		return o;
	}
	
	/** creates a new InterfaceItem. */
	public InterfaceItem createInterfaceItem( String typeFullName, Expression body ) {
		InterfaceItem o = new InterfaceItem(typeFullName,body);
		interfaces.add(o);
		return o;
	}
	
	
	
	/** removes a ClassItem. */
	public void removeClassItem( ClassItem c ) {
		// only a temporary class item can be removed.
		assert(c.isTemporary);
		assert(classes.contains(c));
		
		classes.remove(c);
	}
	
	public void removeClassItems( Collection col ) {
		Object[] o = col.toArray();
		for( int i=0; i<o.length; i++ )
			removeClassItem( (ClassItem)o[i] );
	}
	
	
	
	private static final void assert( boolean b ) {
		if(!b)	throw new Error();
	}
}
