/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.relax;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.DataTypeFactory;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.relax.EmptyStringType;
import com.sun.tranquilo.grammar.relax.NoneType;
import com.sun.tranquilo.reader.*;
import com.sun.tranquilo.reader.datatype.xsd.FacetState;
import com.sun.tranquilo.reader.datatype.xsd.XSDVocabulary;
import com.sun.tranquilo.util.StartTagInfo;

/**
 * reads RELAX grammar/module by SAX2 and constructs abstract grammar model.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class RELAXReader extends GrammarReader
{
	public RELAXReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory,
		ExpressionPool pool,
		State initialState )
	{
		super(controller,parserFactory,pool,initialState);
	}
	
	/** Namespace URI of RELAX Core */
	public static final String RELAXCoreNamespace = "http://www.xml.gr.jp/xmlns/relaxCore";
	/** Namespace URI of RELAX Namespace */
	public static final String RELAXNamespaceNamespace = "http://www.xml.gr.jp/xmlns/relaxNamespace";

	protected boolean isGrammarElement( StartTagInfo tag )
	{
		if( !RELAXCoreNamespace.equals(tag.namespaceURI)
		&&  !RELAXNamespaceNamespace.equals(tag.namespaceURI) )
			return false;
		
		// annotation is ignored at this level.
		// by returning false, the entire subtree will be simply ignored.
		if(tag.localName.equals("annotation"))	return false;
		
		return true;
	}
	
	
	public State createDefaultExpressionChildState( StartTagInfo tag )
	{
		if(! RELAXCoreNamespace.equals(tag.namespaceURI) )	return null;

		if(tag.localName.equals("ref"))				return new ElementRefState();
		if(tag.localName.equals("hedgeRef"))		return new HedgeRefState();
		if(tag.localName.equals("choice"))			return new ChoiceState();
		if(tag.localName.equals("none"))			return new NullSetState();
		if(tag.localName.equals("empty"))			return new EmptyState();
		if(tag.localName.equals("sequence"))		return new SequenceState();
		return null;		// unknown element. let the default error be thrown.
	}
	
	public static FacetState createFacetState( StartTagInfo tag )
	{
		if(! RELAXCoreNamespace.equals(tag.namespaceURI) )	return null;
		
		if( FacetState.facetNames.contains(tag.localName) )	return new FacetState();
		else	return null;
	}

	/** returns true if the given state can have "occurs" attribute. */
	protected boolean canHaveOccurs( ExpressionState state )
	{
		return
			state instanceof SequenceState
		||	state instanceof ElementRefState
		||	state instanceof HedgeRefState
		||	state instanceof ChoiceState;
	}

	protected Expression interceptExpression( ExpressionState state, Expression exp )
	{
		// handle occurs attribute here.
		final String occurs= state.getStartTag().getAttribute("occurs");
		
		if( canHaveOccurs(state) )
		{// these are the repeatable expressions
			if( occurs!=null )
			{
				if( occurs.equals("?") )	exp = pool.createOptional(exp);
				else
				if( occurs.equals("+") )	exp = pool.createOneOrMore(exp);
				else
				if( occurs.equals("*") )	exp = pool.createZeroOrMore(exp);
				else
					reportError( ERR_ILLEGAL_OCCURS, occurs );
					// recover from error by ignoring this occurs attribute
			}
		}
		else
		{
			if( occurs!=null )
				reportError( ERR_MISPLACED_OCCURS, state.getStartTag().localName );
		}
		return exp;
	}
	
	/**
	 * obtains an Expression specified by given (namespace,label) pair.
	 * this method is called to parse &lt;ref label="..." /&gt; element.
	 */
	protected abstract Expression resolveElementRef( String namespace, String label );
	/**
	 * obtains an Expression specified by given (namespace,label) pair.
	 * this method is called to parse &lt;hedgeRef label="..." /&gt; element.
	 */
	protected abstract Expression resolveHedgeRef( String namespace, String label );
	
	
// error related service
//=============================================

	protected String localizeMessage( String propertyName, Object[] args )
	{
		String format;
		
		try
		{
			format = ResourceBundle.getBundle("com.sun.tranquilo.reader.relax.Messages").getString(propertyName);
		}
		catch( Exception e )
		{
			format = ResourceBundle.getBundle("com.sun.tranquilo.reader.Messages").getString(propertyName);
		}
		
	    return MessageFormat.format(format, args );
	}
	

	
	protected ExpressionPool getPool()	{ return super.pool; }

	// error message
	public static final String ERR_ILLEGAL_OCCURS	// arg:1
		= "RELAXReader.IllegalOccurs";
	public static final String ERR_MISPLACED_OCCURS	// arg:1
		= "RELAXReader.MisplacedOccurs";
}
