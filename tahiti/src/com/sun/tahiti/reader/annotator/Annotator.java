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
	public static Expression annotate( AnnotatedGrammar grammar, GrammarReader reader ) {
		
		ClassItem[] classes;
		
		/*
		remove <notAllowed/> from the grammar. <notAllowed/> affects the
		calculation of multiplicity and therefore has to be removed first.
		*/
		{
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
			PrimitiveTypeAnnotator visitor = new PrimitiveTypeAnnotator(grammar.getPool());
			grammar.topLevel = grammar.topLevel.visit( visitor );
			if( grammar.topLevel==Expression.nullSet )	return Expression.nullSet;
			classes = grammar.getClasses();
			for( int i=0; i<classes.length; i++ )
				classes[i].exp = classes[i].exp.visit( visitor );
		}
		
		/*
		then remove temporarily added class items. temporary class items
		are added while parsing various grammars into the AGM. And some
		of them are unnecessary.
		*/
		TemporaryClassItemRemover.remove( grammar );
		
		/*
		perform field annotation. this will normalize
		C-C/C-P/C-I relation and make up for missing FieldItems.
		*/
		FieldItemAnnotation.annotate( grammar );
		
		/*
		finally perform overall normalization. This will ensure that
		JavaItems are used correctly and compute various field values for
		JavaItems.
		*/
		RelationNormalizer.normalize( reader, grammar );
		
		/*
		removes ClassItems which corresponds to the definition of super-class.
		this is necessary to produce marshallers correctly.
		TODO: maybe this shouldn't be included here.
		*/
		SuperClassBodyRemover.remove( grammar );
		
		return grammar.topLevel;
	}
}
