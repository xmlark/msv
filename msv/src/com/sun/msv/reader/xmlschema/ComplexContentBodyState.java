package com.sun.tranquilo.reader.xmlschema;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ReferenceContainer;
import com.sun.tranquilo.grammar.xmlschema.ComplexTypeExp;
import com.sun.tranquilo.grammar.xmlschema.XMLSchemaSchema;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.SequenceState;
import com.sun.tranquilo.util.StartTagInfo;

public class ComplexContentBodyState extends SequenceState {
	
	/** ComplexType object that we are now constructing. */
	protected ComplexTypeExp parentDecl;
	
	/**
	 * if this state is used to parse &lt;extension&gt;, then true.
	 * if this state is used to parse &lt;restension&gt;, then false.
	 */
	protected boolean extension;
	
	protected ComplexContentBodyState( ComplexTypeExp parentDecl, boolean extension ) {
		this.parentDecl = parentDecl;
		this.extension = extension;
	}
	
	protected State createChildState( StartTagInfo tag ) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		State s;
		if( super.exp==null ) {
			// model group must be the first expression child.
			s = reader.createModelGroupState(this,tag);
			if(s!=null )	return s;
		}
		
		// attribute, attributeGroup, and anyAttribtue can be specified
		// after content model is given.
		return reader.createAttributeState(this,tag);
	}
	
	protected Expression annealExpression( Expression exp ) {
		// hook this ComplexTypeExp into the base type's restrictions list.
		
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		exp = super.annealExpression(exp);
		
		ComplexTypeExp baseType = (ComplexTypeExp)reader.resolveQNameRef(
			startTag, "base",
			new XMLSchemaReader.RefResolver() {
				public ReferenceContainer get( XMLSchemaSchema g ) {
					return g.complexTypes;
				}
			} );
		if( baseType==null )	return exp;	// recover by abandoning further processing of this declaration.
		
		if( extension ) {
			baseType.extensions.exp = reader.pool.createChoice(
											baseType.extensions.exp,
											parentDecl.selfWType );
			// actual content model will be <baseTypeContentModel>,<AddedContentModel>
			return reader.pool.createSequence(baseType.self,exp);
		} else {
			baseType.restrictions.exp = reader.pool.createChoice(
											baseType.restrictions.exp,
											parentDecl.selfWType );
			return exp;
		}
	}
}
