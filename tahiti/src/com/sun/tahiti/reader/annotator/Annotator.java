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
import com.sun.tahiti.grammar.util.*;

public class Annotator
{
	public static Expression annotate( Expression topLevel, GrammarReader reader ) {
		
		/*
		remove <notAllowed/> from the grammar. <notAllowed/> affects the
		calculation of multiplicity and therefore has to be removed first.
		*/
		topLevel = topLevel.visit( new NotAllowedRemover(reader.pool) );
		if( topLevel==Expression.nullSet )	return topLevel;
		
		/*
		then remove temporarily added class items. temporary class items
		are added while parsing various grammars into the AGM. And some
		of them are unnecessary.
		*/
		topLevel = TemporaryClassItemRemover.remove( topLevel, reader.pool );
		
		/*
		perform field annotation. this will normalize
		C-C/C-P/C-I relation and make up for missing FieldItems.
		*/
		topLevel = FieldItemAnnotation.annotate( topLevel, reader.pool );
		
		/*
		finally perform overall normalization. This will ensure that
		JavaItems are used correctly and compute various field values for
		JavaItems.
		*/
		topLevel = RelationNormalizer.normalize( reader, topLevel );
		
		return topLevel;
	}
}
