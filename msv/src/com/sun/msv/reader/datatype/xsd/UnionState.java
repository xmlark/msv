/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.datatype.xsd;

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.reader.State;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.datatype.TypeOwner;
import com.sun.msv.util.StartTagInfo;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;

/**
 * State that parses &lt;union&gt; element and its children.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class UnionState extends TypeState implements TypeOwner {
	
	protected final String newTypeName;
	protected UnionState( String newTypeName ) {
		this.newTypeName = newTypeName;
	}
	
	private final ArrayList memberTypes = new ArrayList();
												  
	protected State createChildState( StartTagInfo tag )
	{
		// accepts elements from the same namespace only.
		if( !startTag.namespaceURI.equals(tag.namespaceURI) )	return null;
		
		if( tag.localName.equals("annotation") )	return new IgnoreState();
		if( tag.localName.equals("simpleType") )	return new SimpleTypeState();
		
		return null;	// unrecognized
	}
	
	protected void startSelf() {
		super.startSelf();
		
		// if memberTypes attribute is used, load it.
		String memberTypes = startTag.getAttribute("memberTypes");
		if(memberTypes!=null) {
			StringTokenizer tokens = new StringTokenizer(memberTypes);
			while( tokens.hasMoreTokens() )
				onEndChild( (XSDatatype)reader.resolveDataType(tokens.nextToken()) );
		}
	}
	
	/**
	 * this flag is set to true if there is a child LateBindDatatype object.
	 * In that case, the deriveByUnion method has to be called after the parsing
	 * is completed.
	 */
	private boolean performLateBinding = false;
	
	public void onEndChild( XSDatatype type ) {
		if( type instanceof LateBindDatatype )
			performLateBinding = true;
		memberTypes.add(type);
	}
	
	protected final XSDatatype makeType() throws DatatypeException {
		if( !performLateBinding )
			// normal construction.
			return DatatypeFactory.deriveByUnion( newTypeName, memberTypes );
		
		// late binding
		return new LateBindDatatype( new LateBindDatatype.Renderer() {
			public XSDatatype render( LateBindDatatype.RenderingContext context ) throws DatatypeException {
				int len = memberTypes.size();
				for( int i=0; i<len; i++ ) {
					XSDatatype dt = (XSDatatype)memberTypes.get(i);
					if( dt instanceof LateBindDatatype )
						memberTypes.set(i, ((LateBindDatatype)dt).getBody(context) );
				}
				
				return DatatypeFactory.deriveByUnion( newTypeName, memberTypes );
			}
		}, this );
	}

}
