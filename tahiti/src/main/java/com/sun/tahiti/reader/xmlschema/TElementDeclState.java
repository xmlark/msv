package com.sun.tahiti.reader.xmlschema;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.xmlschema.ElementDeclExp;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.State;
import com.sun.msv.reader.xmlschema.ElementDeclState;
import com.sun.tahiti.grammar.ClassItem;
import com.sun.tahiti.grammar.InterfaceItem;
import com.sun.tahiti.reader.TahitiGrammarReader;

public class TElementDeclState extends ElementDeclState {

	protected Expression annealExpression(Expression contentType) {
		final Expression body = super.annealExpression(contentType);
		final TXMLSchemaReader reader = (TXMLSchemaReader)this.reader;

		if(!isGlobal())
			// we assign ClassItem only when the element type is a global one.
			return body;

		if( "none".equals(startTag.getAttribute(TahitiGrammarReader.TahitiNamespace,"role")) )
			// if "none" is specified, suppress a ClassItem.
			// note that t:role could be possibly missing.
			return body;
		
		String name = startTag.getAttribute("name");
		if(name==null)
			// a global element decl must have a name. If not, it is an error.
			return body;	// abort
		
		final ElementDeclExp decl = reader.getCurrentSchema().elementDecls.getOrCreate(name);
		
		if( startTag.containsAttribute("substitutionGroup") ) {
			// if this element decl has a substitution affiliation,
			// back-patch the head and add InterfaceItem.
			// since decl.substitutionAffiliation may be still not forged,
			// we cannot add InterfaceItem now.
			reader.addBackPatchJob( new GrammarReader.BackPatch(){
				public State getOwnerState() {
					return TElementDeclState.this;
				}
				public void patch() {
					if( decl.substitutionAffiliation.exp instanceof InterfaceItem )
						// someone has already added an InterfaceItem. abort.
						return;
					
					
					ClassItem head = reader.getElementClass(decl.substitutionAffiliation);
					
					if(head==null)
						// this substitution head doesn't have a ClassItem.
						// there is something wrong. So abort now.
						return;
					
					// append "I" to the class name and use it.
					String className="";
					if( head.getPackageName()!=null )
						className = head.getPackageName()+".";
					className += "I"+head.getBareName();
					
					InterfaceItem ii = 
						reader.annGrammar.createInterfaceItem( className, 
							decl.substitutionAffiliation.exp );
					decl.substitutionAffiliation.exp = ii;
					reader.setDeclaredLocationOf(ii);
				}
			});
		}
		
		final String className = reader.computeTypeName(this,"class");
		if(className==null)		return body;	// abort
		
		/* add a ClassItem in the element declaration.
		
		decl.exp should be made from ChoiceExp, ElementDeclExps of
		substitution groups, and ElementDeclExp.XSElementExp.
		Or possibly nullSet (if there was an error or the element decl is abstract).
		*/
		decl.exp = decl.exp.visit( new ExpressionVisitorExpression(){
			
			// do not touch those primitives.
			public Expression onRef( ReferenceExp exp ) { return exp; }
			public Expression onList( ListExp exp ) { return exp; }
			public Expression onData( DataExp exp ) { return exp; }
			public Expression onValue( ValueExp exp ) { return exp; }
			public Expression onMixed( MixedExp exp ) { return exp; }
			public Expression onEpsilon() { return Expression.epsilon; }
			public Expression onAnyString() { return Expression.anyString; }
			public Expression onNullSet() { return Expression.nullSet; }
			public Expression onSequence( SequenceExp exp ) { return exp; }
			public Expression onConcur( ConcurExp exp ) { return exp; }
			public Expression onOther( OtherExp exp ) { return exp; }
			public Expression onAttribute( AttributeExp exp ) { return exp; }
			public Expression onOneOrMore( OneOrMoreExp exp ) { return exp; }
			public Expression onInterleave( InterleaveExp exp ) { return exp; }
			public Expression onChoice( ChoiceExp exp ) {
				// recursively process choices.
				return reader.pool.createChoice( exp.exp1.visit(this), exp.exp2.visit(this) );
			}
			public Expression onElement( ElementExp exp ) {
				if( exp!=decl.body )	return exp;
				// this is the element that we are looking for.
				// wrap it by a ClassItem.
				ClassItem item = reader.annGrammar.createClassItem( className, exp );
				reader.setDeclaredLocationOf(item);
				reader.addElementClass( decl, item );
				return item;
			}
		});
		
		return body;
	}
}
