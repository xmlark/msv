/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.trex.TypedString;
import com.sun.msv.verifier.*;
import com.sun.msv.datatype.DataType;
import com.sun.msv.datatype.DataTypeErrorDiagnosis;
import com.sun.msv.datatype.ValidationContextProvider;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;
import com.sun.msv.util.DataTypeRef;
import java.util.*;

/**
 * lazy AutomatonAcceptor.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ExpressionAcceptor implements Acceptor
{
	/** current state.
	 * 
	 * At the same time, right language (a regular expression that represents
	 * the language it can accept from now on).
	 */
	private Expression	expression;
	
	
	
	/** this object provides various function objects */
	protected final REDocumentDeclaration docDecl;
	
	public ExpressionAcceptor(
		REDocumentDeclaration docDecl, Expression exp )
	{
		this.docDecl	= docDecl;
		this.expression	= exp;
	}
	
	
	/**
	 * creates combined child acceptor and primitive child acceptors (if necessary).
	 * 
	 * be careful not to keep returned object too long because
	 * it is reused whenever the method is called.
	 * 
	 * @return null
	 *		if errRef is null and this expression cannot accept given start tag.
	 *		if errRef is non-null and error recovery is not possible.
	 */
	public Acceptor createChildAcceptor( StartTagInfo tag, StringRef errRef )
	{
		final CombinedChildContentExpCreator cccc = docDecl.getCombinedChildContentExp();
		final StartTagInfoEx sti = new StartTagInfoEx(tag,docDecl);
		
		// obtains fully combined child content pattern
		CombinedChildContentExpCreator.ExpressionPair e = cccc.get(expression,sti);
		if( e.content==Expression.nullSet )
		{// no element declaration is satisfied by this start tag.
		//	this must be an error of input document.
			
			if( errRef==null )
				// fail immediately to notify the caller that an error is encountered.
				return null;
			
			return recover( sti, errRef );	// recover from error.
		}
		
		if( com.sun.msv.driver.textui.Debug.debug )
		{
			System.out.println("accept start tag <"+ sti.qName+">. combined content pattern is");
			System.out.println(com.sun.msv.grammar.trex.util.TREXPatternPrinter.printContentModel(e.content));
			
			if( e.continuation!=null )
				System.out.println("continuation is:\n"+
					com.sun.msv.grammar.trex.util.TREXPatternPrinter.printContentModel(e.continuation)
					);
			else
				System.out.println("no continuation");
		}
		
		return createAcceptor( e.content, e.continuation, cccc.getElementsOfConcern() );
	}
	
	protected abstract Acceptor createAcceptor(
		Expression contentModel, Expression continuation/*can be null*/,
		CombinedChildContentExpCreator.OwnerAndContent primitives );
										  

	
	
	protected boolean stepForward( Token token, StringRef errRef )
	{
		Expression residual = docDecl.getResidualCalculator().calcResidual(
			expression, token );
		
		// if token is ignorable, make expression as so.
		if( token.isIgnorable() )
			residual = docDecl.getPool().createChoice( residual, expression );
		else
			residual = residual;
		
		if( com.sun.msv.driver.textui.Debug.debug )
		{
			System.out.println("residual of stepForward("+token+")");
			System.out.print(com.sun.msv.grammar.trex.util.TREXPatternPrinter.printContentModel(expression));
			System.out.print("   ->   ");
			System.out.println(com.sun.msv.grammar.trex.util.TREXPatternPrinter.printContentModel(residual));
		}
		
		if( residual==Expression.nullSet )
		{// error: we can't accept this token
			
			if( errRef!=null )
			{// diagnose error.
				if( token instanceof StringToken )
					errRef.str = 
						docDecl.localizeMessage( docDecl.DIAG_BAD_LITERAL_VALUE_WRAPUP,
							diagnoseUnexpectedLiteral( (StringToken)token ) );
				// TODO: diagnosis for ElementToken
				
				// recovery by ignoring this token.
				// TODO: should we modify this to choice(expression,EoCR)?
				// we need some measure to prevent redundant choice
			}
			else
			{
				// do not mutate any member variables.
				// caller may call stepForward again with error recovery.
			}
			
			return false;
		}
		
		expression = residual;
		return true;
	}
	
	public boolean stepForward( String literal, ValidationContextProvider provider, StringRef refErr, DataTypeRef refType )
	{
		return stepForward( new StringToken(literal,provider,refType), refErr );
	}
	
	public final boolean stepForwardByContinuation( Expression continuation, StringRef errRef )
	{
		if( continuation!=Expression.nullSet )
		{// successful transition
			if( com.sun.msv.driver.textui.Debug.debug )
				System.out.println("stepForwardByCont. :  " +
					com.sun.msv.grammar.trex.util.TREXPatternPrinter.printContentModel(continuation));
			expression = continuation;
			return true;
		}
		
		if( errRef==null )		return false;	// fail immediately.
		
		// TODO: diagnose uncompleted content model.
		return false;
	}
	
	
	/** checks if this Acceptor is satisifed */
	public boolean isAcceptState()
	{
		return expression.isEpsilonReducible();
	}

	public int getStringCareLevel()
	{	// if the value is cached, return cached value.
		// otherwise, calculate it now.
		OptimizationTag ot = (OptimizationTag)expression.verifierTag;
		if(ot==null)	expression.verifierTag = ot = new OptimizationTag();
		
		if(ot.stringCareLevel==ot.STRING_NOTCOMPUTED)
			ot.stringCareLevel = docDecl.getStringCareLevelCalculator().calc(expression);
		
		return ot.stringCareLevel;
	}
	
	
	

	
// error recovery
//==================================================
	
	
	
	private final Expression mergeContinuation( Expression exp1, Expression exp2 )
	{
		if(exp1==null && exp2==null)	return null;
		if(exp1==null || exp1==Expression.nullSet)	return exp2;
		if(exp2==null || exp2==Expression.nullSet)	return exp1;
		
		return docDecl.getPool().createChoice(exp1,exp2);
	}
	
	/**
	 * creates Acceptor that recovers from errors.
	 * 
	 * This method also modifies the current expression in preparation to
	 * accept newly created child acceptor.
	 * 
	 * Recovery will be done by preparing to accept two possibilities.
	 * 
	 * <ol>
	 *  <li>We may get back to sync by ignoring the newly found illegal element.
	 *      ( this is for mistake like "abcdXefg")
	 *  <li>We may get back to sync by replacing newly found illegal element
	 *      by one of the valid elements.
	 *      ( this is for mistake like "abcXefg")
	 * </ol>
	 */
	private final Acceptor createRecoveryAcceptors()
	{
		final CombinedChildContentExpCreator cccc = docDecl.getCombinedChildContentExp();
		
		// cccc leaves attributes. so we have to "remove" them.
		// note the difference between pruning and removing.
		// pruning replaces unconsumed attributes by nullSet, whereas removing
		// replaces them by epsilon.
		// since we are in error recovery, removing is what we want here.
		final AttributeRemover ar = docDecl.getAttributeRemover();
		
		CombinedChildContentExpCreator.ExpressionPair combinedEoC =
			cccc.get( expression, null, false, false );
		
		// get residual of EoC.
		Expression eocr = docDecl.getResidualCalculator().calcResidual( expression, AnyElementToken.theInstance );
		
		CombinedChildContentExpCreator.ExpressionPair combinedEoC_EoCR =
			cccc.continueGet( eocr, null, false, false );
			// append result to the previous result.

		// alter this.expression for error recovery
		this.expression = docDecl.getPool().createChoice( this.expression, eocr );
		
		Expression continuation = mergeContinuation( combinedEoC.continuation, combinedEoC_EoCR.continuation );
		if( continuation==null || continuation==Expression.nullSet )
			continuation = this.expression;
				
		Expression contentModel =
			docDecl.getPool().createChoice(combinedEoC.content,combinedEoC_EoCR.content);
		contentModel = contentModel.visit(ar);
		
		if( com.sun.msv.driver.textui.Debug.debug )
		{
			System.out.println("content model of recovery acceptor:"+
				com.sun.msv.grammar.trex.util.TREXPatternPrinter.printContentModel(contentModel) );
			System.out.println("continuation of recovery acceptor:"+
				com.sun.msv.grammar.trex.util.TREXPatternPrinter.printSmallest(continuation) );
		}
		
		// by passing null as elements of concern and
		// using continuation, we are effectively "generating"
		// the content model for error recovery.
		return createAcceptor( contentModel, continuation, null );
	}
	
	protected Acceptor recover( StartTagInfoEx sti, StringRef errRef )
	{
		final CombinedChildContentExpCreator cccc = docDecl.getCombinedChildContentExp();
		
		// get combined expression before feeding attributes.
		Expression e = cccc.get(expression,sti,false,true).content;
		
		if( com.sun.msv.driver.textui.Debug.debug )
		{
			System.out.print("content model by tag name only:");
			System.out.println(com.sun.msv.grammar.trex.util.TREXPatternPrinter.printContentModel(e));
		}
		
		if( e==Expression.nullSet )
		{
			// no ElementExp accepts this tag name
			// (actually, some ElementExp may have possibly accepted this tag name,
			// but as a result of <concur>, no expression left ).
					
			// so now we are sure that tag name is wrong, at least.
			// try creating combined child content pattern without tag name check.
			e = cccc.get(expression,sti,false,false).content;
			
			if( e==Expression.nullSet )
			{
				// no element is allowed here.
				errRef.str = docDecl.localizeMessage( docDecl.DIAG_ELEMENT_NOT_ALLOWED, sti.qName );
				return createRecoveryAcceptors();
			}
			
			errRef.str = diagnoseBadTagName(e,sti,cccc);
			if( errRef.str==null )
				// no detailed error message was prepared.
				// use some generic one.
				errRef.str = docDecl.localizeMessage( docDecl.DIAG_BAD_TAGNAME_GENERIC, sti.qName );
			
			// prepare child acceptor.
			return createRecoveryAcceptors();
		}
		else
		{
			// now the situation is
			//  (1) we have some ElementExp that accepts tag name,
			//  (2) but attributes were not accepted by them.
			
			// Whether <concur> is used or not is very critical
			// for the quality of error message.
//			final boolean isComplex = cccc.isComplex();
			
			// get the flag that indicates whether these ElementExps ignores
			// undeclared attributes or not.
			// Since this value depends on grammar language, all of EoC have the
			// same value.
			final boolean ignoreUndeclaredAttributes =
				cccc.getElementsOfConcern().owner.ignoreUndeclaredAttributes;

			// so let's see what attribute is wrong.
			for( int i=0; i<sti.attTokens.length; i++ ) {
				
				Expression r = docDecl.getAttributeFeeder().feed(
					e, sti.attTokens[i], ignoreUndeclaredAttributes );
				if( r!=Expression.nullSet ) {
					e = r;
					continue;
				}
				
				// this attribute was not accepted.
				// its value may be wrong.
				// try feeding wild card and see if it's accepted.
				AttributeRecoveryToken rtoken = sti.attTokens[i].createRecoveryAttToken();
				r = docDecl.getAttributeFeeder().feed(
					e, rtoken, ignoreUndeclaredAttributes );
					
				if( r==Expression.nullSet )
				{
					// even the wild card was rejected.
					// this means that this attribute
					// is not specified by the grammar.
					errRef.str = docDecl.localizeMessage(
						docDecl.DIAG_UNDECLARED_ATTRIBUTE,
						sti.attributes.getQName(i) );
				}
				else
				{
					// wild card was accepted, so the value must be wrong.
					errRef.str = diagnoseBadAttributeValue( rtoken, sti, i, cccc );
					if( errRef.str==null )
					{
						// no detailed error message can be provided
						// so use generic one.
						errRef.str = docDecl.localizeMessage(
							docDecl.DIAG_BAD_ATTRIBUTE_VALUE_GENERIC,
							sti.attributes.getQName(i) );
					}
				}
				
				// now we have found one error for this attribute.
				// let's prepare recovery acceptor
				
				return createRecoveryAcceptors();
			}
			
			// now all attributes that were present were fed successfully.
			// so there must be required attributes that are missing.
			if( e==Expression.nullSet )	throw new Error();	// assertion
			
			errRef.str = diagnoseMissingAttribute(e,sti,cccc);
			if( errRef.str==null )
				// no detailed error message can be provided
				// so use generic one.
				errRef.str = docDecl.localizeMessage(
					docDecl.DIAG_MISSING_ATTRIBUTE_GENERIC,
					sti.qName );
			
			return createRecoveryAcceptors();
		}
	}
	
	/**
	 * format list of candidates to one string.
	 * 
	 * this method
	 *  (1) inserts separator into appropriate positions
	 *  (2) appends "more" message when items are only a portion of candidates.
	 */
	private final String concatenateMessages( List items, boolean more,
											  String separatorStr, String moreStr )
	{
		String r="";
		String sep = docDecl.localizeMessage(separatorStr,null);
		
		Collections.sort(items,
			new Comparator(){
				public int compare( Object o1, Object o2 )
				{
					return ((String)o1).compareTo((String)o2);
				}
			});	// sort candidates.
		
		for( int i=0; i<items.size(); i++ )
		{
			if(r.length()!=0)		r+= sep;
			r += items.get(i);
		}
		if( more )
			r += docDecl.localizeMessage(moreStr,null);
		
		return r;
	}

	private final String concatenateMessages( Set items, boolean more,
											  String separatorStr, String moreStr )
	{
		return concatenateMessages( new Vector(items), more, separatorStr, moreStr );
	}

	/**
	 * gets error diagnosis message from datatype.
	 * 
	 * @return null
	 *		if diagnosis failed.
	 */
	private final String getDiagnosisFromTypedString( TypedStringExp exp, StartTagInfoEx sti, int index )
	{
		try
		{
			DataTypeErrorDiagnosis diag;
			diag = exp.dt.diagnose(	sti.attributes.getValue(index), sti.context );
										
			if( diag!=null )	// diag is null if the implementation has flaw
								// and unable to provide diagnosis.
								// should we throw an assertion here?
				return diag.message;
		}
		catch( UnsupportedOperationException uoe )
		{
			// implementation may throw this exception
			// when it doesn't implement diganose method.
			;
		}
		
		return null;
	}


	/**
	 * computes diagnosis message for bad tag name
	 * 
	 * @param exp
	 *		combined child content expression without
	 *		tag name check and attributes check.
	 * 
	 * @return null
	 *		if diagnosis fails.
	 */
	private final String diagnoseBadTagName( Expression exp, StartTagInfoEx sti,
											 CombinedChildContentExpCreator cccc )
	{
		if( cccc.isComplex() )
		{
			// probably <concur> is used.
			// there is no easy way to tell which what tag name is expected.
						
			// TODO: we can reduce strength by treating concur as choice.
			// do it.
			return null;
		}

		// we are now sure that combined child content expression will be
		// the choice of all elements of concern.
		// so if tag name satisfies one of those elements,
		// it can be accepted.
						
		// therefore we can provide candidates for users.
						
		Set s = new java.util.HashSet();
		boolean more = false;

		// if there is a SimpleNameClass with the same localName
		// but with a different namespace URI,
		// this variable will receive that URI.
		String wrongNamespace = null;
		
		CombinedChildContentExpCreator.OwnerAndContent oac;
		for( oac=cccc.getElementsOfConcern(); oac!=null; oac=oac.next )
		{
			// test some typical name class patterns.
			final NameClass nc = oac.owner.getNameClass();
						
			if( nc instanceof SimpleNameClass )
			{
				SimpleNameClass snc = (SimpleNameClass)nc;
				
				if( snc.localName.equals(sti.localName) )
				{
					// sometimes, people simply forget to add namespace decl,
					// or declare the wrong name.
					wrongNamespace = snc.namespaceURI;
				}
				
				s.add( docDecl.localizeMessage(
					docDecl.DIAG_SIMPLE_NAMECLASS, nc.toString() ) );
				continue;
			}
			if( nc instanceof NamespaceNameClass )
			{
				s.add( docDecl.localizeMessage(
					docDecl.DIAG_NAMESPACE_NAMECLASS, ((NamespaceNameClass)nc).namespaceURI ) );
				continue;
			}
			if( nc instanceof NotNameClass )
			{
				NameClass ncc = ((NotNameClass)nc).child;
				if( ncc instanceof NamespaceNameClass )
				{
					s.add( docDecl.localizeMessage(
						docDecl.DIAG_NOT_NAMESPACE_NAMECLASS, ((NamespaceNameClass)nc).namespaceURI ) );
					continue;
				}
			}
			// this name class is very complex and
			// therefore we were unable to provide appropriate suggestion.
			more = true;
		}
		
		// no candidate was collected. bail out.
		if( s.size()==0 )			return null;
		
		if( wrongNamespace!=null )
		{
			if( s.size()==1 )
				// only one candidate.
				return docDecl.localizeMessage(
					docDecl.DIAG_BAD_TAGNAME_WRONG_NAMESPACE, sti.localName, wrongNamespace );
			else
				// probably wrong namespace,
				// but show the user that he/she has other choices.
				return docDecl.localizeMessage(
					docDecl.DIAG_BAD_TAGNAME_PROBABLY_WRONG_NAMESPACE, sti.localName, wrongNamespace );
		}

		
		// there is no clue about user's intention.
		return docDecl.localizeMessage(
			docDecl.DIAG_BAD_TAGNAME_WRAPUP, sti.qName,
			concatenateMessages( s, more,
				docDecl.DIAG_BAD_TAGNAME_SEPARATOR,
				docDecl.DIAG_BAD_TAGNAME_MORE ) );
	}


	/**
	 * computes diagnosis message for bad attribute value
	 * 
	 * @param rtoken
	 *		wild card AttributeToken that was used.
	 * @param attIndex
	 *		index in sti.attributes of the attribute
	 * 
	 * @return null
	 *		if diagnosis fails.
	 */
	private final String diagnoseBadAttributeValue( AttributeRecoveryToken rtoken, StartTagInfoEx sti, int attIndex,
													CombinedChildContentExpCreator cccc )
	{
		// if the expression is complex, bail out. 
		// the chance that we can provide simple error message is remote.
		if( cccc.isComplex() )		return null;

		// if the combined child content expression is not complex,
		// only binary expressions used are choice and sequence.
							
		// this is the choice of all constraints that made this
		// attribute fail.
		Expression constraint = rtoken.getFailedExp();
							
		// The problem here is that sti.attributes.getValue(i)
		// didn't satisfy this expression.
							
		// test some typical expression patterns and
		// provide error messages if it matchs the pattern.
		// otherwise provide a generic error message.
		
		// resolve indirect references first, if any.
		while( constraint instanceof ReferenceExp ) 
			constraint = ((ReferenceExp)constraint).exp;
							
		if( constraint instanceof TypedStringExp ) {
			// if only one AttributeExp is specified for this attribute
			// and if it has a TypedString as its child.					
			// for RELAX, this is the only possible case
			TypedStringExp tse = (TypedStringExp)constraint;
			
			if( tse.dt == com.sun.msv.grammar.relax.NoneType.theInstance ) {
				// if the underlying datatype is "none",
				// this should be reported as unexpected attribute.
				return docDecl.localizeMessage(
							docDecl.DIAG_UNDECLARED_ATTRIBUTE, sti.attributes.getQName(attIndex) );
			}
			
			String dtMsg = getDiagnosisFromTypedString( tse, sti, attIndex );
			if(dtMsg==null)		return null;
			
			return docDecl.localizeMessage(
						docDecl.DIAG_BAD_ATTRIBUTE_VALUE_DATATYPE,
						sti.attributes.getQName(attIndex), dtMsg );
		}
		if( constraint instanceof ChoiceExp ) {
			// choice of <string>s.
			//
			// this is also a frequently used pattern by TREX.
			// an expression like
			// 
			// <attribute name="export">
			//   <choice>
			//     <string>yes</string><string>no</string>
			//   </choice>
			// </attribute>
			//
			// falls into this pattern.
								
			final Set items = new java.util.HashSet();
			boolean more = false;
								
			
			ChoiceExp ch = (ChoiceExp)constraint;
								
			while(true)
			{
				if( ch.exp1 instanceof TypedStringExp
				&&  ((TypedStringExp)ch.exp1).dt instanceof TypedString )
					items.add( ((TypedString)((TypedStringExp)ch.exp1).dt).value );
				else
					// left hand may not be ChoiceExp.
					// so this must be some fairly complex expression
					// that we can't provide diagnosis.
					more = true;
								
				// right hand
				if( ch.exp2 instanceof TypedStringExp
				&&  ((TypedStringExp)ch.exp2).dt instanceof TypedString )
				{
					items.add( ((TypedString)((TypedStringExp)ch.exp2).dt).value );
					break;	// finished.
				}
								
				if( ch.exp2 instanceof ChoiceExp )
				{
					ch = (ChoiceExp)ch.exp2;
					continue;
				}
									
				// right hand side expression is complex.
				more = true;	
				break;	// bail out
			}
			
			// no candidates was simple. bail out.
			if( items.size()==0 )	return null;
			
			// at least we have one suggestion.
			return docDecl.localizeMessage(
				docDecl.DIAG_BAD_ATTRIBUTE_VALUE_WRAPUP,
				sti.attributes.getQName(attIndex),
				concatenateMessages( items, more,
					docDecl.DIAG_BAD_ATTRIBUTE_VALUE_SEPARATOR,
					docDecl.DIAG_BAD_ATTRIBUTE_VALUE_MORE ) );
		}
		
		return null;	// this constraint didn't fall into known patterns.
	}						



	/**
	 * computes diagnosis message for missing attribute
	 * 
	 * @param e
	 *		expression after feeding attributes.
	 * 
	 * @return null
	 *		if diagnosis fails.
	 */
	private final String diagnoseMissingAttribute( Expression e, StartTagInfoEx sti, CombinedChildContentExpCreator cccc )
	{
		if( cccc.isComplex() )
			// again if the expression is complex,
			// hope is remote that we can find required attributes.
				
			// TODO: reduce strength by converting concur to choice?
			return null;
		
		e = e.visit(new AttributePicker(docDecl.getPool()));
				
		if( e.isEpsilonReducible() )	throw new Error();	// assertion
		// if attribute expression is epsilon reducible, then
		// AttributePruner must return Expression other than nullSet.
		// In that case, there is no error.

		final Set s = new java.util.HashSet();
		boolean more = false;
				
		while( e instanceof ChoiceExp )
		{
			ChoiceExp ch = (ChoiceExp)e;
					
			NameClass nc = ((AttributeExp)ch.exp1).nameClass;
			if( nc instanceof SimpleNameClass )
				s.add( nc.toString() );
			else
				more = true;
			
			e = ch.exp2;
		}
		
		if(!(e instanceof AttributeExp ))	throw new Error();	//assertion
		
		NameClass nc = ((AttributeExp)e).nameClass;
		if( nc instanceof SimpleNameClass )
			s.add( nc.toString() );
		else
			more = true;
				
		if( s.size()==0 )		return null;
		
		// at least one candidate is found
		if( s.size()==1 && !more )
		{// only one candidate
			return docDecl.localizeMessage(
				docDecl.DIAG_MISSING_ATTRIBUTE_SIMPLE,
				sti.qName,s.iterator().next() );
		}
		else
			// list candidates
			return docDecl.localizeMessage(
				docDecl.DIAG_MISSING_ATTRIBUTE_WRAPUP,
				sti.qName,
				concatenateMessages( s, more,
					docDecl.DIAG_MISSING_ATTRIBUTE_SEPARATOR,
					docDecl.DIAG_MISSING_ATTRIBUTE_MORE ) );
	}
	
	private final String diagnoseUnexpectedLiteral( StringToken token )
	{
		final StringRecoveryToken srt = new StringRecoveryToken(token);
		
		// this residual corresponds to the expression we get
		// when we replace thie unexpected token by one of expected tokens.
		Expression recoveryResidual
			= docDecl.getResidualCalculator().calcResidual(expression,srt);
		
		if( recoveryResidual==Expression.nullSet )
		{
			// we now know that no string literal was expected at all.
			return docDecl.localizeMessage( docDecl.DIAG_STRING_NOT_ALLOWED, null );
			// keep this.expression untouched. This is equivalent to ignore this token.
		}
		
		// there are two possible "recovery" for this error.
		//  (1) ignore this token
		//  (2) replace this token by a valid token.
		// by the following choice implements both of them.
		expression = docDecl.getPool().createChoice( expression, recoveryResidual );
		
		// TODO: check if expressions are complex
		if( srt.failedTypes.size()==1 )
		{
			DataType dt = (DataType)srt.failedTypes.iterator().next();
			try
			{
				DataTypeErrorDiagnosis diag = dt.diagnose( srt.literal, srt.context );
				if( diag==null )// datatype implementation doesn't support diagnosis
					return null;
				
				return diag.message;
			}
			catch( UnsupportedOperationException uoe )
			{// datatype implementation doesn't support diagnosis
				return null;
			}
		}
		
		return null;
	}
}
