package com.sun.tahiti.reader.relaxng;

import com.sun.msv.grammar.*;
import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.util.StartTagInfo;
import com.sun.tahiti.reader.annotator.Annotator;
import com.sun.tahiti.reader.NameUtil;
import com.sun.tahiti.grammar.*;
import javax.xml.parsers.SAXParserFactory;
import java.util.Map;
import java.util.Stack;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

public class TRELAXNGReader extends RELAXNGReader {

	public static final String TahitiNamespace = 
		"http://www.sun.com/xml/tahiti/";
	
	public TRELAXNGReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory,
		StateFactory stateFactory,
		ExpressionPool pool ) {
		
		super( controller, parserFactory, stateFactory, pool );
	}

	public TRELAXNGReader( GrammarReaderController controller, SAXParserFactory parserFactory ) {
		super( controller, parserFactory );
	}
	
	/**
	 * gets Type of Java object from TypedStringExp.
	 */
	protected Class getJavaType( TypedStringExp texp ) {
		if( texp.dt instanceof DatabindableDatatype ) {
			return ((DatabindableDatatype)texp.dt).getJavaObjectType();
		}
		
		// if any other attempt fails, use String.
		return String.class;
	}
	
	/**
	 * a map from TypedStringExp to the PrimitiveItem which wraps it.
	 * used to unify PrimitiveItems.
	 */
	private final Map primitiveItems = new java.util.HashMap();
	
	protected Expression interceptExpression( ExpressionState state, Expression exp ) {
		// if an error was found, stop further processing.
		if( hadError )	return exp;
		
		
		if( exp instanceof TypedStringExp ) {
			// if this is a typed string, then wrap it by the PrimitiveItem.
			
			if( primitiveItems.containsKey(exp) )
				// if this exp is already wrapped, use it instead of creating another one.
				// this will reduce the size of the LL grammar for data-binding.
				exp = (Expression)primitiveItems.get(exp);
			else {
				// if this is the first time, wrap it and memorize it.
				PrimitiveItem p = new PrimitiveItem(getJavaType((TypedStringExp)exp));
				primitiveItems.put( exp, p );
				p.exp = exp;
				exp = p;
			}
		}
		
		// check Tahiti attributes.
		
		final StartTagInfo tag = state.getStartTag();
		String role = tag.getAttribute(TahitiNamespace,"role");
		
		if( role==null ) {
			// there is no markup.
			
			// insert an ClassItem if this is the <element> tag.
			// some of those temporarily added ClassItems will be removed
			// in the final wrap up.
			if( exp instanceof ElementExp ) {
				ClassItem t = new ClassItem( decideName(state,exp,"class") );
				t.isTemporary = true;	// this flag indicates that this class item is a temporary one.
				t.exp = exp;
				return t;
			}
			
			return exp;	// the "role" attribute is not present.
		}
		
		
		ReferenceExp roleExp;
		
		if( role.equals("none") ) {
			// do nothing. this will prevent automatic ClassItem insertion.
			return exp;
		} else
		if( role.equals("superClass") ) {
			roleExp = new SuperClassItem();
		} else
		if( role.equals("class") ) {
			/*
			removes silly use of temporary ClassItem.
			Consider the following grammar fragment:
			<define name="foo" t:role="class">
				<element name="foo">
					...
				</element>
			</define>
			
			Since there is no tahiti markup for <element> element, a temporary ClassItem
			is inserted. Immediately after that, a <define> element is processed, and 
			a new ClassItem is inserted.
			
			The following code is a quick hack to prevent this situation by removing
			temporary ClassItem if it is the immediate child of the user-defined class.
			 */
			if( (exp instanceof ClassItem) && ((ClassItem)exp).isTemporary )
				exp = ((ClassItem)exp).exp;
			if( exp.getClass()==ReferenceExp.class ) {
				ReferenceExp rexp = (ReferenceExp)exp;
				if( (rexp.exp instanceof ClassItem) && ((ClassItem)rexp.exp).isTemporary )
					rexp.exp = ((ClassItem)rexp.exp).exp;
			}
			
			roleExp = new ClassItem(decideName(state,exp,role));
		} else
		if( role.equals("field") ) {
			roleExp = new FieldItem(decideName(state,exp,role));
		} else
		if( role.equals("interface") ) {
			roleExp = new InterfaceItem(decideName(state,exp,role));
		} else
		if( role.equals("ignore") ) {
			roleExp = new IgnoreItem();
		} else {
			reportError( ERR_UNDEFINED_ROLE, role );
			return exp;
		}
		
		setDeclaredLocationOf(roleExp);	// memorize where this expression is defined.
		
		
		if( tag.localName.equals("define") ) {
			// if this is <define>, then
			// this tag should be placed as the sole child of <define>.
			ReferenceExp rexp = (ReferenceExp)exp;
			roleExp.exp = rexp.exp;
			rexp.exp = roleExp;
		} else {
			// wrap the expression by this tag.
			roleExp.exp = exp;
			exp = roleExp;
		}
		
		return exp;
	}

	/**
	 * compute the name for the item.
	 * 
	 * @param role
	 *		the role of this expression. One of "field","interface", and "class".
	 */
	protected String decideName( ExpressionState state, Expression exp, String role ) {
		
		final StartTagInfo tag = state.getStartTag();
	
		String name = tag.getAttribute(TahitiNamespace,"name");
		// if we have t:name attribute, use it.
		if(name==null) {
		
			// if the current tag has the name attribute, use it.
			// this is the case of <define/>,<ref/>, and sometimes
			// <element/> and <attribute/>
			name = tag.getAttribute("name");
			if(name!=null)	name = NameUtil.xmlNameToJavaName(role,name);
		}
		
		if(name==null) {
			// otherwise, sniff the name.
		
			// if it's element/attribute, then we may be able to use its name.
			if( exp instanceof NameClassAndExpression ) {
				NameClass nc = ((NameClassAndExpression)exp).getNameClass();
				if( nc instanceof SimpleNameClass )
					name = NameUtil.xmlNameToJavaName(role,((SimpleNameClass)nc).localName);
					
				// if it's not a simple type, abort.
			}
		}
		
		if(name==null) {
			// we can't generate a proper name. bail out
			reportError( ERR_NAME_NEEDED );
			return "";
		}
		
		if( role.equals("class") || role.equals("interface") ) {
			// append the default package name, if necessary.
			int idx = name.indexOf('.');
			if(idx<0)	name = defaultPackageName+"."+name;
		}
		
		return name;
	}
	
	public void wrapUp() {
		// First, let the super class do its job.
		super.wrapUp();
		
		// if we already have an error, abort further processing.
		if(hadError)	return;

		
		// add missing annotations and normalizes them.
		grammar.start = Annotator.annotate( grammar.start, this );
	}

	
	
	/**
	 * propagatable 't:package' attribute that specifies the default package 
	 * for unqualified java classes/interfaces.
	 */
	private final Stack packageNameStack = new Stack();
	private String defaultPackageName="";
	
	public void startElement( String a, String b, String c, Attributes d ) throws SAXException {
		// handle "t:package" attribute here.
		packageNameStack.push(defaultPackageName);
		if( d.getIndex(TahitiNamespace,"package")!=-1 ) {
			defaultPackageName = d.getValue(TahitiNamespace,"package");
		}
		
		super.startElement(a,b,c,d);
	}
	public void endElement( String a, String b, String c ) throws SAXException {
		super.endElement(a,b,c);
		defaultPackageName = (String)packageNameStack.pop();
	}
	
	
	

	protected String localizeMessage( String propertyName, Object[] args ) {
		String format;
		try {
			format = ResourceBundle.getBundle(
				"com.sun.tahiti.reader.relaxng.Messages").getString(propertyName);
		} catch( Exception e ) {
			try {
				format = ResourceBundle.getBundle(
					"com.sun.tahiti.reader.Messages").getString(propertyName);
			} catch( Exception ee ) {
				return super.localizeMessage(propertyName,args);
			}
		}
	    return MessageFormat.format(format, args );
	}
	
// TODO: add more arguments to produce user-friendly messages.
	public static final String ERR_UNDEFINED_ROLE = // arg:1
		"UndefinedRole"; // "{0}" is a bad value for the role attribute.
	public static final String ERR_NAME_NEEDED = // arg:0
		"NameNeeded";	// failed to generate a proper name for this role.
				// specify t:name attribute.
	
	
}
