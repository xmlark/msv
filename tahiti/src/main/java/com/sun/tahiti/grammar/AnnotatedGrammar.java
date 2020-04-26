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
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;

/**
 * An AGM with Tahiti annotation.
 * 
 * This object keeps track of all JavaItems added to the grammar.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
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
    @Override
	public Expression getTopLevel() { return topLevel; }
	
	private final ExpressionPool pool;
    @Override
	public ExpressionPool getPool() { return pool; }
	
	
	
	/**
	 * the grammar file will be generated as this name.
	 * This field would be something like "com.example.abc.FileName".
	 * It shouldn't have a file extension.
	 * 
	 * This field must be set by the reader.
	 */
	public String grammarName;
	
	
	
	/** all ClassItems in this grammar (from the fully qualified name to ClassItem). */
	public final Map classes = new java.util.HashMap();
	public ClassItem[] getClasses() {
		return (ClassItem[])classes.values().toArray( new ClassItem[classes.size()] );
	}
	public Iterator iterateClasses() {
		return classes.values().iterator();
	}
	
	/** all InterfaceItems in this grammar (from the fully qualified name to InterfaceItem). */
	public final Map interfaces = new java.util.HashMap();
	public InterfaceItem[] getInterfaces() {
		return (InterfaceItem[])interfaces.values().toArray( new InterfaceItem[interfaces.size()] );
	}
	public Iterator iterateInterfaces() {
		return interfaces.values().iterator();
	}
	
	
	/** creates a new ClassItem. */
	public ClassItem createClassItem( String typeFullName, Expression body ) {
		// type name must be unique.
		assert(!classes.containsKey(typeFullName));
		
		ClassItem o = new ClassItem(typeFullName,body);
		classes.put(typeFullName,o);
		return o;
	}
	
	/** creates a new InterfaceItem. */
	public InterfaceItem createInterfaceItem( String typeFullName, Expression body ) {
		// type name must be unique.
		assert(!interfaces.containsKey(typeFullName));
		
		InterfaceItem o = new InterfaceItem(typeFullName,body);
		interfaces.put(typeFullName,o);
		return o;
	}
	
	
	
	/** removes a ClassItem. */
	public void removeClassItem( ClassItem c ) {
		// only a temporary class item can be removed.
		// DBG
		if(!c.isTemporary) {
			System.out.println(com.sun.msv.grammar.util.ExpressionPrinter.printContentModel(c));
			System.out.println(c.getTypeName());
		}
		assert c.isTemporary : "ClassItem is not temporary!";
		assert classes.containsValue(c) : "Classes did not contain ClassIte!"; 
		
		classes.remove(c.name);
	}
	
	public void removeClassItems( Collection col ) {
		Object[] o = col.toArray();
		for( int i=0; i<o.length; i++ )
			removeClassItem( (ClassItem)o[i] );
	}
}
