package com.sun.tranquilo.schema;

import jp.gr.xml.formal.automaton.Automaton;
import jp.gr.xml.formal.automaton.AutomatonFactory;
import com.sun.tranquilo.datatype.DataType;

/**
 * typed PCDATA that can be used as a content model.
 * 
 * Although implementation allows this TypedText to be used as a 'part' of the content model,
 * like
 * 
 * <xmp>
 * <choice>
 *   <type name="integer" />
 *   <ref label="..." />
 * </choice>
 * </xmp>
 * 
 * but RELAX prohibits this.
 */
public class TypedText implements HedgeModel
{
	protected DataType dataType;
	public DataType getDataType()				{ return dataType; }
	public void setDataType( DataType dt )		{ dataType = dt; }
	
	public Automaton getAutomaton( AutomatonFactory factory )
	{
		return factory.createSingleSymbolAutomaton( dataType );
	}

	public TypedText( DataType type )
	{
		dataType = type;
	}
}
