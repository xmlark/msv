package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.SequenceState;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.AnyNameClass;

/**
 * Base implementation for ElementState and AttributeState
 * 
 * This class collects one name class and patterns
 * 
 */
public abstract class NameClassAndExpressionState extends SequenceState implements NameClassOwner
{
	protected NameClass nameClass=null;
	
	/**
	 * gets namespace URI to which this declaration belongs
	 */
	protected String getNamespace()
	{
		// usually, propagated "ns" attribute should be used
		return ((TREXGrammarReader)reader).targetNamespace;
	}
	
	protected void startSelf()
	{
		super.startSelf();
		// if name attribtue is specified, use it.
		final String name = startTag.getAttribute("name");
		if( name!=null )
			nameClass = new SimpleNameClass( getNamespace(), name );
//				((TREXGrammarReader)reader).targetNamespace, name );
			
	}
	public void onEndChild( NameClass p )
	{
		if( nameClass!=null )	// name class has already specified
			reader.reportError( TREXGrammarReader.ERR_MORE_THAN_ONE_NAMECLASS );
		nameClass = p;
	}

	protected State createChildState( StartTagInfo tag )
	{
		if( nameClass==null )	// nameClass should be specified before content model.
		{
			State nextState = TREXGrammarReader.createNameClassChildState(tag);
			if( nextState!=null )	return nextState;
			
			// to provide better error message, analyze the situation further.
			// users tend to forget to supply nameClass and name attribute.
			
			nextState = reader.createExpressionChildState(tag);
			if( nextState !=null )
			{
				// OK. tag is recognized as an content model.
				// so probably this user forgot to specify name class.
				// report so and recover by assuming some NameClass
				reader.reportError( TREXGrammarReader.ERR_MISSING_CHILD_NAMECLASS );
				nameClass = AnyNameClass.theInstance;
				return nextState;
			}
			else
				// probably this user made a typo. let the default handler reports an error
				return null;
		}
		else
			return reader.createExpressionChildState(tag);
	}
	
	protected void endSelf()
	{
		if( nameClass==null )
		{// name class is missing
			reader.reportError( TREXGrammarReader.ERR_MISSING_CHILD_NAMECLASS );
			nameClass = AnyNameClass.theInstance;
		}
		
		super.endSelf();
	}
}
