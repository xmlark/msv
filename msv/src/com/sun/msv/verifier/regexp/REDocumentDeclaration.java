/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.verifier.regexp;

import com.sun.tranquilo.verifier.Acceptor;
import com.sun.tranquilo.verifier.DocumentDeclaration;
import com.sun.tranquilo.verifier.regexp.ExpressionAcceptor;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ExpressionPool;
import java.util.Map;

/**
 * Adaptor between abstract grammar model and verifier's grammar model.
 * 
 * Grammar object can be shared among multiple threads, but this object
 * cannot be shared.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class REDocumentDeclaration implements DocumentDeclaration
{
	/** obtains an ExpressionPool that can be used to play with expressions */
	public abstract ExpressionPool	getPool();
	
	/** obtains a thread-local copy of ResidualCalculator */
	public abstract ResidualCalculator getResidualCalculator();
	
	/** obtains a thread-local copy of CombinedChildContentExpCreator */
	public abstract CombinedChildContentExpCreator getCombinedChildContentExp();
	
	/** obtains a thread-local copy of AttributeFeeder */
	public abstract AttributeFeeder getAttributeFeeder();

	/** obtains a thread-local copy of AttributeFeeder */
	public abstract AttributePruner getAttributePruner();

	/** obtains a thread-local copy of ElementsOfConcernCollector */
	public abstract ElementsOfConcernCollector getElementsOfConcernCollector();
	
	/** obtains a thread-local copy of StringCareLevelCalculator */
	public abstract StringCareLevelCalculator getStringCareLevelCalculator();
	
	/** obtains a thread-local copy of AttributeFreeMarker */
	public abstract AttributeFreeMarker getAttributeFreeMarker();
	
	public String localizeMessage( String propertyName, Object[] args )
	{
		String format = java.util.ResourceBundle.getBundle(
			"com.sun.tranquilo.verifier.regexp.Messages").getString(propertyName);
		
	    return java.text.MessageFormat.format(format, args );
	}
	
	public final String localizeMessage( String propName, Object arg1 ) {
		return localizeMessage(propName, new Object[]{arg1} );
	}
	public final String localizeMessage( String propName, Object arg1, Object arg2 ) {
		return localizeMessage(propName, new Object[]{arg1,arg2} );
	}

	
	public static final String DIAG_ELEMENT_NOT_ALLOWED =
		"Diagnosis.ElementNotAllowed";
	public static final String DIAG_BAD_TAGNAME_GENERIC =
		"Diagnosis.BadTagName.Generic";
	public static final String DIAG_BAD_TAGNAME_WRAPUP =
		"Diagnosis.BadTagName.WrapUp";
	public static final String DIAG_BAD_TAGNAME_SEPARATOR =
		"Diagnosis.BadTagName.Separator";
	public static final String DIAG_BAD_TAGNAME_MORE =
		"Diagnosis.BadTagName.More";
	public static final String DIAG_BAD_TAGNAME_WRONG_NAMESPACE =
		"Diagnosis.BadTagName.WrongNamespace";
	public static final String DIAG_BAD_TAGNAME_PROBABLY_WRONG_NAMESPACE =
		"Diagnosis.BadTagName.ProbablyWrongNamespace";
	public static final String DIAG_UNDECLARED_ATTRIBUTE =
		"Diagnosis.UndeclaredAttribute";
	public static final String DIAG_BAD_ATTRIBUTE_VALUE_GENERIC =
		"Diagnosis.BadAttributeValue.Generic";
	public static final String DIAG_BAD_ATTRIBUTE_VALUE_DATATYPE =
		"Diagnosis.BadAttributeValue.DataType";
	public static final String DIAG_BAD_ATTRIBUTE_VALUE_WRAPUP =
		"Diagnosis.BadAttributeValue.WrapUp";
	public static final String DIAG_BAD_ATTRIBUTE_VALUE_SEPARATOR =
		"Diagnosis.BadAttributeValue.Separator";
	public static final String DIAG_BAD_ATTRIBUTE_VALUE_MORE =
		"Diagnosis.BadAttributeValue.More";
	public static final String DIAG_MISSING_ATTRIBUTE_SIMPLE =
		"Diagnosis.MissingAttribute.Simple";
	public static final String DIAG_MISSING_ATTRIBUTE_GENERIC =
		"Diagnosis.MissingAttribute.Generic";
	public static final String DIAG_MISSING_ATTRIBUTE_WRAPUP =
		"Diagnosis.MissingAttribute.WrapUp";
	public static final String DIAG_MISSING_ATTRIBUTE_SEPARATOR =
		"Diagnosis.MissingAttribute.Separator";
	public static final String DIAG_MISSING_ATTRIBUTE_MORE =
		"Diagnosis.MissingAttribute.More";
	public static final String DIAG_SIMPLE_NAMECLASS =
		"Diagnosis.SimpleNameClass";
	public static final String DIAG_NAMESPACE_NAMECLASS =
		"Diagnosis.NamespaceNameClass";
	public static final String DIAG_NOT_NAMESPACE_NAMECLASS =
		"Diagnosis.NotNamespaceNameClass";
	public static final String DIAG_STRING_NOT_ALLOWED =
		"Diagnosis.StringNotAllowed";
	public static final String DIAG_BAD_LITERAL_VALUE_WRAPUP =
		"Diagnosis.BadLiteralValue.WrapUp";

}
