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
		
		/*
		remove <notAllowed/> from the grammar. <notAllowed/> affects the
		calculation of multiplicity and therefore has to be removed first.
		*/
		grammar.topLevel = grammar.topLevel.visit( new NotAllowedRemover(grammar.getPool()) );
		if( grammar.topLevel==Expression.nullSet )	return Expression.nullSet;
		
		/*
		add PrimitiveItem.
		*/
		grammar.topLevel = grammar.topLevel.visit( new PrimitiveTypeAnnotator(grammar.getPool()) );
		if( grammar.topLevel==Expression.nullSet )	return Expression.nullSet;
		
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
		grammar.topLevel = RelationNormalizer.normalize( reader, grammar.topLevel );
		
		return grammar.topLevel;
	}
}
