package com.sun.msv.schematron.verifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.XPathContext;
import org.relaxng.datatype.Datatype;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

import com.sun.msv.schematron.grammar.SAction;
import com.sun.msv.schematron.grammar.SActions;
import com.sun.msv.schematron.grammar.SElementExp;
import com.sun.msv.schematron.grammar.SRule;
import com.sun.msv.schematron.util.DOMBuilder;
import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.ValidityViolation;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.VerifierFilter;

public class RelmesVerifier implements IVerifier {
	
	public RelmesVerifier( IVerifier core ) throws ParserConfigurationException {
		this.core = new VerifierFilter(core);
		this.schChecker = new SchematronVerifier();
		this.core.setContentHandler(schChecker);
	}
	public RelmesVerifier( DocumentDeclaration docDecl, ErrorHandler handler )
				throws ParserConfigurationException {
		this(new Verifier(docDecl,handler));
	}
	
	/**
	 * this object performs RELAX NG validation,
	 * then pass type-information to the schematron checker.
	 */
	private final VerifierFilter core;
	
	/**
	 * this object will perform the schematron validation on the endDocument method.
	 */
	private final SchematronVerifier schChecker;
	
	/**
	 * this flag is set to true when all schematron validations are successful.
	 */
	private boolean schematronValid;

	/** performs schematron validation. */
	class SchematronVerifier extends DOMBuilder {
		
        /**
         * ID -> value map. We can't trust DOM to do this for us.
         */
        private final Map idMap = new HashMap();
        
        
		SchematronVerifier() throws ParserConfigurationException {}
		
		public void startElement( String ns, String local, String qname, Attributes atts ) throws SAXException {
			super.startElement(ns,local,qname,atts);
			locationMap.put( super.parent, new Loc(getLocator()) );
			
			Object o = getCurrentElementType();
			if( o instanceof SElementExp ) {
//				System.out.println("Schematron node found");
				// memorize this node so that we can check it later.
				checks.put( super.parent, o );
			}
            
            // build ID map
            for( int i=0; i<atts.getLength(); i++ ) {
                if( "ID".equals(atts.getType(i)) ) {
                    // since there's no way to achieve this via DOM,
                    // we need to directly talk to the implementation.
                    try {
                        // Xerces?
                        ((org.apache.xerces.dom.CoreDocumentImpl)super.dom).putIdentifier(
                            atts.getValue(i), (Element)super.parent );
                    } catch( Throwable t ) {
                        ; // ignore any error. it wasn't Xerces.
                    }
                    try {
                        // Crimson?
                        ((org.apache.crimson.tree.ElementNode2)super.parent)
                            .setIdAttributeName(atts.getQName(i));
                    } catch( Throwable t ) {
                        ; // ignore any error. it wasn't Crimson.
                    }
                }
            }
		}
		
		/**
		 * a map from Element to Loc object.
		 * This map will be used to report the source of error.
		 */
		private final Map locationMap = new java.util.HashMap();
		
		/**
		 * a map from Node to SElementExp.
		 * These are checked later.
		 */
		private final Map checks = new java.util.HashMap();
		
		public void startDocument() throws SAXException {
			super.startDocument();
			checks.clear();
			locationMap.clear();
		}
		
		public void endDocument() throws SAXException {
			super.endDocument();
			schematronValid = true;

			try {
				testNode(super.dom);
			} catch( TransformerException e ) {
				getErrorHandler().error( new ValidityViolation(
					null, "XPath error:"+e.getMessage(), null ) );
				schematronValid = false;
			}
		}
		
		/** SRule objects that are currently in effect. */
		private final Stack effectiveRules = new Stack();
		
		private void testNode( Node node ) throws SAXException, TransformerException {
			
			// if this node has the corresponding rule to be checked,
			// push it to the stack.
			// if we already have the same rule object, there is no need to
			// add it twice.
			SElementExp exp = (SElementExp)checks.get(node);
//			System.out.println("node tested");
			int numRulesAdded = 0;
			if(exp!=null ) {
//				System.out.println("rule added");
				for( int i=0; i<exp.rules.length; i++ ) {
					if(!effectiveRules.contains(exp.rules)) {
						effectiveRules.push(exp.rules[i]);
						numRulesAdded++;
					}
				}
                // if the element decl has any direction actions, run them now.
                testActions( exp.actions, node );
			}
			
			// test effective rules against this node
			int len = effectiveRules.size();
			for( int i=0; i<len; i++ )
				testRule( (SRule)effectiveRules.get(i), node );
			
			// recursively process children
			if( node.getNodeType()==Node.ELEMENT_NODE ) {
				Element e = (Element)node;
				NamedNodeMap atts = e.getAttributes();
				len = atts.getLength();
				for( int i=0; i<len; i++ )
					testNode( atts.item(i) );
			}
			
			NodeList children = node.getChildNodes();
			len = children.getLength();
			for( int i=0; i<len; i++ )
				testNode( children.item(i) );
			
			// a rule is in effect only in itself or descendants.
			for( ; numRulesAdded>0; numRulesAdded-- )
				effectiveRules.pop();
		}
		
		/**
		 * tests the specified rule against the node.
		 */
		private void testRule( SRule rule, Node node )
					throws SAXException, TransformerException {
			
			if( !rule.matches(node) )	return;
            
            testActions(rule,node);
        }
                    
                    
		private void testActions( SActions actions, Node node )	
                throws SAXException, TransformerException {
//			System.out.println("rule tested");
			
			PrefixResolverDefault resolver = new PrefixResolverDefault(node);
			
			synchronized(actions) {
				// I'm not sure whether XPath object is thread-safe.
				// so for precaution, synchronize it.
                
                // I'm not sure if XPathContextImpl is reusable,
                // so again just for precaution, we will create new ones each time
				
				for( int i=0; i<actions.asserts.length; i++ )
					if(!actions.asserts[i].xpath.execute(
						new XPathContext(), node, resolver ).bool() )
						reportError( node, actions.asserts[i] );
							
				for( int i=0; i<actions.reports.length; i++ )
					if(actions.reports[i].xpath.execute(
						new XPathContext(), node, resolver ).bool() )
						reportError( node, actions.reports[i] );
			}					
		}

		private void reportError( Node node, SAction action ) throws SAXException {
			Loc loc = (Loc)locationMap.get(node);
            if( loc==null ) {
                if( node instanceof Attr ) {
                    reportError( ((Attr)node).getOwnerElement(), action );
                    return;
                }
                if( node.getParentNode()!=null ) {
				    reportError( node.getParentNode(), action );
				    return;
                }
			}
			
			LocatorImpl src = new LocatorImpl();
            if(loc!=null) {
			    src.setLineNumber(loc.line);
			    src.setColumnNumber(loc.col);
            }
			src.setSystemId(getLocator().getSystemId());
			src.setPublicId(getLocator().getPublicId());
			
			schematronValid = false;
			getErrorHandler().error( new ValidityViolation(
				src, action.document, null ));
		}
        
        
        /**
         * XPathContext implementation that supports ID.
         */
        private class XPathContextImpl extends XPathContext
        {
        }
	}
	
	
	
	
	public void setPanicMode(boolean v) {
		core.setPanicMode(v);
	}
	public boolean isValid() {
		return schematronValid && core.isValid();
	}
	public Object getCurrentElementType() {
		return core.getCurrentElementType();
	}
	public Datatype[] getLastCharacterType() {
		return core.getLastCharacterType();
	}
	public final Locator getLocator() {
		return core.getLocator();
	}
	public final ErrorHandler getErrorHandler() {
		return core.getErrorHandler();
	}
	public final void setErrorHandler( ErrorHandler handler ) {
		core.setErrorHandler(handler);
	}

	
	
	
	
	
	
    public void setDocumentLocator(Locator locator) {
		core.setDocumentLocator(locator);
    }

    public void startDocument() throws SAXException {
		core.startDocument();
    }

    public void endDocument() throws SAXException {
		core.endDocument();
    }

    public void startPrefixMapping( String prefix, String uri ) throws SAXException {
		core.startPrefixMapping(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
		core.endPrefixMapping(prefix);
    }

    public void startElement( String namespaceURI, String localName, String qName, Attributes atts ) throws SAXException {
		core.startElement(namespaceURI, localName, qName, atts);
    }

    public void endElement(	String namespaceURI, String localName, String qName ) throws SAXException {
		core.endElement(namespaceURI, localName, qName);
    }

    public void characters( char ch[], int start, int length ) throws SAXException {
		core.characters(ch, start, length);
    }

    public void ignorableWhitespace( char ch[], int start, int length ) throws SAXException {
		core.ignorableWhitespace(ch, start, length);
    }

    public void processingInstruction(String target, String data) throws SAXException {
		core.processingInstruction(target, data);
    }

    public void skippedEntity(String name) throws SAXException {
		core.skippedEntity(name);
    }
}
