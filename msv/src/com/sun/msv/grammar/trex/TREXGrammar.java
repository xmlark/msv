package com.sun.tranquilo.grammar.trex;

import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.reader.datatype.DataTypeVocabularyMap;

public class TREXGrammar
{
	static final public class RefContainer extends ReferenceContainer
	{
		public ReferenceExp getOrCreate( String name )
		{ return super._getOrCreate(name); }
	
		protected ReferenceExp createReference( String name )
		{ return new ReferenceExp(name); }
	}
	
	/** named patterns which are defined by using &lt;define&gt; element.
	 * 
	 * this is a map from pattern name to RefPattern object
	 */
	public final RefContainer namedPatterns = new RefContainer();
	
	/** Start pattern.
	 * 
	 * The pattern defined under &lt;start&gt; element.
	 * This pattern will be used to verify document element.
	 */
	public Expression start;
	
	/** TREXPatternPool that is associated with this grammar */
	public final TREXPatternPool pool;
	
	/** in case of nested grammar, this variable points to the parent grammar.
	 *  Otherwise null.
	 */
	protected final TREXGrammar parentGrammar;
	
	/**
	 * gets a parent TREXGrammar.
	 * 
	 * In case of nested grammar, the parent grammar will be returned.
	 * Otherwise, it returns null.
	 */
	public final TREXGrammar getParentGrammar() { return parentGrammar; }
	
	/**
	 * data type vocabularies used and defined by this grammar.
	 */
	public final DataTypeVocabularyMap dataTypes = new DataTypeVocabularyMap();
	
	/**
	 * 
	 * @param parentGrammar
	 *		this object is used to resolve &lt;ref&gt; element with parent
	 *		attribute.
	 */
	public TREXGrammar( TREXPatternPool pool, TREXGrammar parentGrammar )
	{
		this.pool = pool;
		this.parentGrammar = parentGrammar;
	}
	
	public TREXGrammar( TREXPatternPool pool )	{ this(pool,null); }
}
