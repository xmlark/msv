package com.sun.tranquilo.reader.xmlschema;

import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.xmlschema.AttributeGroupExp;
import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.reader.State;
import org.xml.sax.Locator;

/**
 * parses &lt;attributeGroup /&gt; element
 */
public class AttributeGroupState extends RedefinableDeclState {
	
	protected State createChildState( StartTagInfo tag ) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		return reader.createAttributeState(this,tag);
	}

	protected Expression initialExpression() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		String refQName = startTag.getAttribute("ref");
		if( refQName==null )
			// child expressions are expected.
			return null;
		
		String[] r = reader.splitQName(refQName);
		if(r==null) {
			reader.reportError( reader.ERR_UNDECLARED_PREFIX, refQName );
			// recover by returning something meaningless.
			return Expression.nullSet;
		}
		
		// TODO: memorize this reference.
		return reader.getOrCreateSchema(r[0]/*uri*/).
			attributeGroups.getOrCreate(r[1]/*localName*/);
	}

	protected Expression castExpression( Expression halfCastedExpression, Expression newChildExpression ) {
		if( startTag.containsAttribute("ref") )
			reader.reportError( reader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION );
		if( halfCastedExpression==null )
			return newChildExpression;	// the first one.
		return reader.pool.createSequence( newChildExpression, halfCastedExpression );
	}
	
	protected Expression annealExpression(Expression contentType) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		if( !isGlobal() )		return contentType;
		
		// if this is a global declaration register it.
		String name = startTag.getAttribute("name");
		if( name==null ) {
			reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "attributeGroup", "name" );
			return Expression.epsilon;
			// recover by returning something meaningless.
			// the parent state will ignore this.
		}
		AttributeGroupExp exp;
		if( isRedefine() )
			exp = (AttributeGroupExp)super.oldDecl;
		else {
			exp = reader.currentSchema.attributeGroups.getOrCreate(name);
			if( exp.exp!=null )
				reader.reportError( 
					new Locator[]{this.location,reader.getDeclaredLocationOf(exp)},
					reader.ERR_DUPLICATE_ATTRIBUTE_GROUP_DEFINITION,
					new Object[]{name} );
		}
		reader.setDeclaredLocationOf(exp);
		exp.exp = contentType;
		return exp;
	}
}
