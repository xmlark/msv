/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.reader.annotator;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.GrammarReader;
import com.sun.tahiti.grammar.AnnotatedGrammar;
import com.sun.tahiti.grammar.ClassItem;
import com.sun.tahiti.grammar.util.*;

/**
 * Forges a raw AGM into the fully-fledged annotated AGM.
 * 
 * TahitiGrammarReader can add annotation partially. This class performs
 * several processes and forges those partially annotated AGM into fully
 * annotated AGM.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Annotator
{
	private static java.io.PrintStream debug = System.out;

	public static Expression annotate( AnnotatedGrammar grammar, GrammarReader reader ) {
		
		ClassItem[] classes;
		
		/*
		remove <notAllowed/> from the grammar. <notAllowed/> affects the
		calculation of multiplicity and therefore has to be removed first.
		*/
		{
			if(debug!=null)	debug.println("removing notAllowed");
			
			NotAllowedRemover visitor = new NotAllowedRemover(grammar.getPool());
			grammar.topLevel = grammar.topLevel.visit( visitor );
			if( grammar.topLevel==Expression.nullSet )	return Expression.nullSet;
			// abstract elements of XSD makes AGM disjoint.
			// so we have to explicitly visit each children.
			classes = grammar.getClasses();
			for( int i=0; i<classes.length; i++ )
				classes[i].exp = classes[i].exp.visit( visitor );
		}
		
		/*
		add PrimitiveItem.
		*/
		{
			if(debug!=null)	debug.println("examining primitive types");
			
			PrimitiveTypeAnnotator visitor = new PrimitiveTypeAnnotator(grammar.getPool());
			grammar.topLevel = grammar.topLevel.visit( visitor );
			if( grammar.topLevel==Expression.nullSet )	return Expression.nullSet;
			classes = grammar.getClasses();
			for( int i=0; i<classes.length; i++ )
				classes[i].exp = classes[i].exp.visit( visitor );
		}
		
		/*
		added even more ClassItem and InterfaceItem to annotate <choice>s.
		*/
		if(debug!=null)	debug.println("annotating complex choices");
		ChoiceAnnotator.annotate( grammar );
		
		/*
		then remove temporarily added class items. temporary class items
		are added while parsing various grammars into the AGM. And some
		of them are unnecessary.
		*/
		if(debug!=null)	debug.println("removing temporary class items");
		TemporaryClassItemRemover.remove( grammar );
		
		/*
		perform field annotation. this will normalize
		C-C/C-P/C-I relation and make up for missing FieldItems.
		*/
		if(debug!=null)	debug.println("adding field items");
		FieldItemAnnotation.annotate( grammar );
		
		/*
		finally perform overall normalization. This will ensure that
		JavaItems are used correctly and compute various field values for
		JavaItems.
		*/
		if(debug!=null)	debug.println("normalizing relations");
		RelationNormalizer.normalize( reader, grammar );
		
		/*
		removes ClassItems which corresponds to the definition of super-class.
		this is necessary to produce marshallers correctly.
		TODO: maybe this shouldn't be included here.
		*/
		if(debug!=null)	debug.println("removing superClass body definition");
		SuperClassBodyRemover.remove( grammar );
		
		return grammar.topLevel;
	}
}
