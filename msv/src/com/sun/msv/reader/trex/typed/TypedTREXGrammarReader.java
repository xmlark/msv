package com.sun.tranquilo.reader.trex.typed;

import com.sun.tranquilo.reader.*;
import com.sun.tranquilo.reader.trex.TREXGrammarReader;
import com.sun.tranquilo.reader.trex.RootState;
import com.sun.tranquilo.grammar.trex.TREXPatternPool;
import com.sun.tranquilo.grammar.trex.TREXGrammar;
import com.sun.tranquilo.util.StartTagInfo;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;

/** reads TREX grammar with 'label' annotation. */
public class TypedTREXGrammarReader extends TREXGrammarReader
{
	public final static String LABEL_NAMESPACE =
		"http://www.sun.com/xml/tranquilo/trex-type";
	
	protected TypedTREXGrammarReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory,
		TREXPatternPool pool )
	{
		super(controller,parserFactory,pool);
	}
	
	public State createExpressionChildState( StartTagInfo tag )
	{
		if(tag.localName.equals("element"))		return new TypedElementState();
		return super.createExpressionChildState(tag);
	}
	
	/** loads TREX pattern */
	public static TREXGrammar parse( String grammarURL,
		SAXParserFactory factory, GrammarReaderController controller )
	{
		TypedTREXGrammarReader reader = new TypedTREXGrammarReader(controller,factory,new TREXPatternPool());
		reader._parse(grammarURL,new RootState());
		if(reader.hadError)	return null;
		else				return reader.grammar;
	}
	
	/** loads TREX pattern */
	public static TREXGrammar parse( InputSource grammar,
		SAXParserFactory factory, GrammarReaderController controller )
	{
		TypedTREXGrammarReader reader = new TypedTREXGrammarReader(controller,factory,new TREXPatternPool());
		reader._parse(grammar,new RootState());
		if(reader.hadError)	return null;
		else				return reader.grammar;
	}
}
