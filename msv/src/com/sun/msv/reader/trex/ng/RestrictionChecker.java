/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.ng;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.util.ExpressionWalker;
import org.xml.sax.Locator;
import java.util.Vector;

/**
 * Checks RELAX NG contextual restrictions defined in the section 7.
 * 
 * <p>
 * ExpressionWalker is used to walk the content model thoroughly.
 * Depending on the current context, different walkers are used so that
 * we can detect contextual restrictions properly.
 * 
 * <p>
 * For each ElementExp and AttributeExp, its name class is checked to detect
 * the constraint set out in the section 7.1.6. Also, a set is used to avoid
 * redundant checks.
 * 
 * <p>
 * 
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RestrictionChecker {
	
	private RestrictionChecker( RELAXNGReader _reader ) {
		this.reader = _reader;
	}
	
	public static void check( RELAXNGReader reader ) {
		reader.getGrammar().start.visit(new RestrictionChecker(reader).inStart);
	}
	
	/** Reader object to which errors are reported. */
	private final RELAXNGReader reader;
	
	/**
	 * The source location of this expression should be also reported in case of error.
	 */
	private Expression errorContext;
	
	private void reportError( Expression exp, String errorMsg ) {
		reader.reportError(
			new Locator[]{
				reader.getDeclaredLocationOf(exp),
				reader.getDeclaredLocationOf(errorContext)
			}, errorMsg, null );
	}
	
	/**
	 * Visited ElementExp/AttributeExps.
	 */
	private final java.util.Set visitedExps = new java.util.HashSet();

	/**
	 * Duplicate attribute checker for the current content model.
	 */
	private DuplicateAttributesChecker attDupChecker;
	
/*
	
	content model checker
	=====================
*/
	
	/**
	 * The base class of all other context-specific checker.
	 * This class performs the context switching.
	 */
	private class DefaultChecker extends ExpressionWalker {
		public void onElement( ElementExp exp ) {
			if( !visitedExps.add(exp) )		return;
			
			
			// push context element,
			final Expression oldContext = errorContext;
			final DuplicateAttributesChecker oldADC = attDupChecker;
			
			errorContext = exp;
			attDupChecker = new DuplicateAttributesChecker();

			exp.getNameClass().visit(inNameClass);	// check the name
			
			// it is important to use the expanded exp because
			// section 7 has to be applied after patterns are expanded.
			exp.contentModel.getExpandedExp(reader.pool).visit(inElement);
			errorContext = oldContext;
			attDupChecker = oldADC;
		}
		public void onAttribute( AttributeExp exp ) {
			if( !visitedExps.add(exp) )		return;
			
			// check duplicate attributes
			attDupChecker.addAttribute(exp);
			
			Expression oldContext = errorContext;
			errorContext = exp;
			
			exp.getNameClass().visit(inNameClass);	// check the name
			
			exp.exp.getExpandedExp(reader.pool).visit(inAttribute);
			errorContext = oldContext;
		}
		public void onList( ListExp exp ) {
			exp.exp.visit(inList);
		}
		public void onTypedString( TypedStringExp exp ) {
			exp.except.visit(inExcept);
		}
		public void onChoice( ChoiceExp exp ) {
			if(attDupChecker==null)
				// if a 'choice' appears at the top level,
				// there is no enclosing element, so no attDupChecker is present.
				super.onChoice(exp);
			else {
				int idx = attDupChecker.getExclusionIndex();
				exp.exp1.visit(this);
				attDupChecker.createExclusion(idx);
				exp.exp2.visit(this);
				attDupChecker.removeExclusion();
			}
		}
	}
	
	/**
	 * Used to visit children of the 'except' clause of data.
	 */
	private final ExpressionWalker inExcept = new DefaultChecker() {
		public void onAttribute( AttributeExp exp ) {
			reportError( exp, ERR_ATTRIBUTE_IN_EXCEPT );
		}
		public void onElement( ElementExp exp ) {
			reportError( exp, ERR_ELEMENT_IN_EXCEPT );
		}
		public void onList( ListExp exp ) {
			reportError( exp, ERR_LIST_IN_EXCEPT );
		}
		public void onAnyString() {
			reportError( null, ERR_TEXT_IN_EXCEPT );
		}
		public void onSequence( SequenceExp exp ) {
			reportError( exp, ERR_SEQUENCE_IN_EXCEPT );
		}
		public void onInterleave( InterleaveExp exp ) {
			reportError( exp, ERR_INTERLEAVE_IN_EXCEPT );
		}
		public void onOneOrMore( OneOrMoreExp exp ) {
			reportError( exp, ERR_ONEORMORE_IN_EXCEPT );
		}
	};
	
	/**
	 * Used to visit children of group/interleave in oneOrMore in elements.
	 */
	private final ExpressionWalker inGroupInOneOrMoreInElement = new DefaultChecker() {
		public void onAttribute( AttributeExp exp ) {
			reportError( exp, ERR_REPEATED_GROUPED_ATTRIBUTE );
		}
	};
	
	/**
	 * Used to visit children of oneOrMore in elements.
	 */
	private final ExpressionWalker inOneOrMoreInElement = new DefaultChecker() {
		public void onSequence( SequenceExp exp ) {
			exp.visit(inGroupInOneOrMoreInElement);
		}
		public void onInterleave( InterleaveExp exp ) {
			exp.visit(inGroupInOneOrMoreInElement);
		}
	};
	
	/**
	 * Used to visit children of elements.
	 */
	private final ExpressionWalker inElement = new DefaultChecker() {
		public void onOneOrMore( OneOrMoreExp exp ) {
			exp.exp.visit(inOneOrMoreInElement);
		}
	};
	
	/**
	 * Used to visit children of attributes.
	 */
	private final ExpressionWalker inAttribute = new DefaultChecker(){
		public void onElement( ElementExp exp ) {
			reportError( exp, ERR_ELEMENT_IN_ATTRIBUTE );
		}
		public void onAttribute( AttributeExp exp ) {
			reportError( exp, ERR_ATTRIBUTE_IN_ATTRIBUTE );
		}
	};
	
	
	/**
	 * Used to visit children of lists.
	 */
	private final ExpressionWalker inList = new DefaultChecker() {
		public void onAttribute( AttributeExp exp ) {
			reportError( exp, ERR_ATTRIBUTE_IN_LIST );
		}
		public void onElement( ElementExp exp ) {
			reportError( exp, ERR_ELEMENT_IN_LIST );
		}
		public void onList( ListExp exp ) {
			reportError( exp, ERR_LIST_IN_LIST );
		}
		public void onAnyString() {
			reportError( null, ERR_TEXT_IN_LIST );
		}
	};
	
	/**
	 * Used to visit the start pattern.
	 */
	private final ExpressionWalker inStart = new DefaultChecker() {
		public void onAttribute( AttributeExp exp ) {
			reportError( exp, ERR_ATTRIBUTE_IN_START );
		}
		public void onList( ListExp exp ) {
			reportError( exp, ERR_LIST_IN_START );
		}
		public void onAnyString() {
			reportError( null, ERR_TEXT_IN_START );
		}
		public void onSequence( SequenceExp exp ) {
			reportError( exp, ERR_SEQUENCE_IN_START );
		}
		public void onInterleave( InterleaveExp exp ) {
			reportError( exp, ERR_INTERLEAVE_IN_START );
		}
		public void onTypedString( TypedStringExp exp ) {
			reportError( exp, ERR_DATA_IN_START );
		}
		public void onOneOrMore( OneOrMoreExp exp ) {
			reportError( exp, ERR_ONEORMORE_IN_START );
		}
	};
	

/*
	
	name class checker
	==================
*/
	
	class NameClassWalker implements NameClassVisitor {
		public Object onAnyName( AnyNameClass nc ) { return null; }
		public Object onSimple( SimpleNameClass nc ) { return null; }
		public Object onNsName( NamespaceNameClass nc ) { return null; }
		public Object onNot( NotNameClass nc ) { throw new Error(); }	// should not be used
		public Object onDifference( DifferenceNameClass nc ) {
			nc.nc1.visit(this);
			if(nc.nc1 instanceof AnyNameClass)
				nc.nc2.visit(inAnyNameClass);
			else
			if(nc.nc1 instanceof NamespaceNameClass)
				nc.nc2.visit(inNsNameClass);
			else
				throw new Error();	// this is not possible in RELAX NG.
			return null;
		}
		public Object onChoice( ChoiceNameClass nc ) {
			nc.nc1.visit(this);
			nc.nc2.visit(this);
			return null;
		}
	}
	
	/**
	 * Used to visit name classes.
	 */
	private final NameClassWalker inNameClass = new NameClassWalker();
		
	/**
	 * Used to visit children of AnyNameClass
	 */
	private final NameClassVisitor inAnyNameClass = new NameClassWalker(){
		public Object onAnyName( AnyNameClass nc ) {
			reportError(null,ERR_ANYNAME_IN_ANYNAME);
			return null;
		}
	};
	
	/**
	 * Used to visit children of NamespaceNameClass
	 */
	private final NameClassVisitor inNsNameClass = new NameClassWalker(){
		public Object onAnyName( AnyNameClass nc ) {
			reportError(null,ERR_ANYNAME_IN_NSNAME);
			return null;
		}
		public Object onNsName( NamespaceNameClass nc ) {
			reportError(null,ERR_NSNAME_IN_NSNAME);
			return null;
		}
	};

	
	
	
/*
	
	duplicate attributes check
	==========================
*/
	private class DuplicateAttributesChecker {
		
		/** AttributeExps will be added into this array. */
		private AttributeExp[] atts = new AttributeExp[16];
		/** Number of items in the atts array. */
		private int attLen=0;
		
		/** exclusion areas.
		 * 
		 * <p>
		 * An exclusion area consists of two items.
		 * <pre>{ start, end, start, end, ... }</pre>
		 * 
		 * <p>
		 * Exclusion areas are created by choice. When the second branch of
		 * choice is being visited, attributes appeared in the first branch
		 * should be excluded.
		 */
		private int[] exclusions = new int[8];
		private int excLen=0;
		
		public void addAttribute( AttributeExp exp ) {
			
			// check the consistency with existing attributes.
			int j=0;
			for( int i=0; i<excLen; i+=2 ) {
				while( j<exclusions[i] )
					check(exp,atts[j++]);
				j=exclusions[i+1];
			}
			while(j<attLen)
				check(exp,atts[j++]);
				
			
			// add it to the array
			if(atts.length==attLen) {
				// expand buffer
				AttributeExp[] n = new AttributeExp[attLen*2];
				System.arraycopy(atts,0,n,0,atts.length);
				atts = n;
			}
			atts[attLen++] = exp;
			
		}
		
		
		public int getExclusionIndex() {
			return attLen;
		}
		
		public void createExclusion( int start ) {
			if( exclusions.length==excLen ) {
				// expand buffer
				int[] n = new int[excLen*2];
				System.arraycopy(exclusions,0,n,0,excLen);
				exclusions = n;
			}
			exclusions[excLen++] = start;
			exclusions[excLen++] = attLen;
		}
		
		public void removeExclusion() {
			excLen-=2;
		}

		/**
		 * Tests two name classes to see if they collide.
		 */
		private void check( AttributeExp newExp, AttributeExp oldExp ) {
			if(checker.check( newExp.nameClass, oldExp.nameClass )) {
				// two attributes collide
				reader.reportError( 
					new Locator[]{
						reader.getDeclaredLocationOf(errorContext),	// the parent element
						reader.getDeclaredLocationOf(newExp),
						reader.getDeclaredLocationOf(oldExp)},
					ERR_DUPLICATE_ATTRIBUTES, null );
			}
		}
		
		
		private final NameClassCollisionChecker checker = new NameClassCollisionChecker();
		private class NameClassCollisionChecker implements NameClassVisitor {
			
			/** Two name classes to be tested. */
			NameClass nc1,nc2;
			
			/**
			 * This exception will be thrown when a collision is found.
			 */
			private final RuntimeException eureka = new RuntimeException();
			
			/**
			 * Returns true if two name classes collide.
			 */
			boolean check( NameClass _new, NameClass _old ) {
				
				if( _new instanceof SimpleNameClass ) {
					// short cut for 90% of the cases
					SimpleNameClass nnc = (SimpleNameClass)_new;
					return _old.accepts( nnc.namespaceURI, nnc.localName );
				}
				
				try {
					nc1 = _new;
					nc2 = _old;
					_old.visit(this);
					_new.visit(this);
					return false;
				} catch( RuntimeException e ) {
					if(e==eureka)	return true;	// the collision was found.
					throw e;
				}
			}
			
			private void probe( String uri, String local ) {
				if(nc1.accepts(uri,local) && nc2.accepts(uri,local))
					// conflict is found.
					throw eureka;
			}
			
			private /*static*/ final String MAGIC = "\u0000";
			
			public Object onAnyName( AnyNameClass nc ) {
				probe(MAGIC,MAGIC);
				return null;
			}
			public Object onNsName( NamespaceNameClass nc ) {
				probe(nc.namespaceURI,MAGIC);
				return null;
			}
			public Object onSimple( SimpleNameClass nc ) {
				probe(nc.namespaceURI,nc.localName);
				return null;
			}
			public Object onNot( NotNameClass nc ) {
				nc.child.visit(this);
				return null;
			}
			public Object onDifference( DifferenceNameClass nc ) {
				nc.nc1.visit(this);
				nc.nc2.visit(this);
				return null;
			}
			public Object onChoice( ChoiceNameClass nc ) {
				nc.nc1.visit(this);
				nc.nc2.visit(this);
				return null;
			}
		}
	}
	
	
	
// error messages
	
	private static final String ERR_ATTRIBUTE_IN_EXCEPT =
		"RELAXNGReader.AttributeInExcept";
	private static final String ERR_ELEMENT_IN_EXCEPT =
		"RELAXNGReader.ElementInExcept";
	private static final String ERR_LIST_IN_EXCEPT =
		"RELAXNGReader.ListInExcept";
	private static final String ERR_TEXT_IN_EXCEPT =
		"RELAXNGReader.TextInExcept";
	private static final String ERR_SEQUENCE_IN_EXCEPT =
		"RELAXNGReader.SequenceInExcept";
	private static final String ERR_INTERLEAVE_IN_EXCEPT =
		"RELAXNGReader.InterleaveInExcept";
	private static final String ERR_ONEORMORE_IN_EXCEPT =
		"RELAXNGReader.OneOrMoreInExcept";
	private static final String ERR_REPEATED_GROUPED_ATTRIBUTE =
		"RELAXNGReader.RepeatedGroupedAttribute";
	private static final String ERR_ELEMENT_IN_ATTRIBUTE =
		"RELAXNGReader.ElementInAttribute";
	private static final String ERR_ATTRIBUTE_IN_ATTRIBUTE =
		"RELAXNGReader.AttributeInAttribute";
	private static final String ERR_ATTRIBUTE_IN_LIST =
		"RELAXNGReader.AttributeInList";
	private static final String ERR_ELEMENT_IN_LIST =
		"RELAXNGReader.ElementInList";
	private static final String ERR_LIST_IN_LIST =
		"RELAXNGReader.ListInList";
	private static final String ERR_TEXT_IN_LIST =
		"RELAXNGReader.TextInList";
	private static final String ERR_ATTRIBUTE_IN_START =
		"RELAXNGReader.AttributeInStart";
	private static final String ERR_LIST_IN_START =
		"RELAXNGReader.ListInStart";
	private static final String ERR_TEXT_IN_START =
		"RELAXNGReader.TextInStart";
	private static final String ERR_SEQUENCE_IN_START =
		"RELAXNGReader.SequenceInStart";
	private static final String ERR_INTERLEAVE_IN_START =
		"RELAXNGReader.InterleaveInStart";
	private static final String ERR_DATA_IN_START =
		"RELAXNGReader.DataInStart";
	private static final String ERR_ONEORMORE_IN_START =
		"RELAXNGReader.OneOrMoreInStart";
	
	private static final String ERR_ANYNAME_IN_ANYNAME =
		"RELAXNGReader.AnyNameInAnyName";
	private static final String ERR_ANYNAME_IN_NSNAME =
		"RELAXNGReader.AnyNameInNsName";
	private static final String ERR_NSNAME_IN_NSNAME =
		"RELAXNGReader.NsNameInNsName";
	
	private static final String ERR_DUPLICATE_ATTRIBUTES =
		"RELAXNGReader.DuplicateAttributes";
}
