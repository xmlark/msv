/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and  use in  source and binary  forms, with  or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions  of  source code  must  retain  the above  copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution  in binary  form must  reproduct the  above copyright
 *   notice, this list of conditions  and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither  the  name   of  Sun  Microsystems,  Inc.  or   the  names  of
 * contributors may be  used to endorse or promote  products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS  OR   IMPLIED  CONDITIONS,  REPRESENTATIONS   AND  WARRANTIES,
 * INCLUDING  ANY  IMPLIED WARRANTY  OF  MERCHANTABILITY,  FITNESS FOR  A
 * PARTICULAR PURPOSE  OR NON-INFRINGEMENT, ARE HEREBY  EXCLUDED. SUN AND
 * ITS  LICENSORS SHALL  NOT BE  LIABLE  FOR ANY  DAMAGES OR  LIABILITIES
 * SUFFERED BY LICENSEE  AS A RESULT OF OR  RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF  THE SOFTWARE OR ITS DERIVATIVES.  IN NO EVENT WILL
 * SUN OR ITS  LICENSORS BE LIABLE FOR ANY LOST  REVENUE, PROFIT OR DATA,
 * OR  FOR  DIRECT,   INDIRECT,  SPECIAL,  CONSEQUENTIAL,  INCIDENTAL  OR
 * PUNITIVE  DAMAGES, HOWEVER  CAUSED  AND REGARDLESS  OF  THE THEORY  OF
 * LIABILITY, ARISING  OUT OF  THE USE OF  OR INABILITY TO  USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.sun.msv.reader.xmlschema;

import java.util.Map;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

import com.sun.msv.reader.GrammarReaderController2;

/**
 * Catch error messages and resolve schema locations.
 */
public class WSDLGrammarReaderController implements
		GrammarReaderController2, LSResourceResolver {
	
	private GrammarReaderController2 nextController;
	
	private Map<String, EmbeddedSchema> schemas;
	private String baseURI;

	/**
	 * create the resolving controller.
	 * @param baseURI URI of the WSDL.
	 * @param sources
	 */
	public WSDLGrammarReaderController(GrammarReaderController2 nextController,
									   String baseURI, Map<String, EmbeddedSchema> sources) {
		this.nextController = nextController;
		this.baseURI = baseURI;
		this.schemas = sources;
	}

	public void error(Locator[] locs, String msg, Exception nestedException) {
		if (nextController != null) {
			nextController.error(locs, msg, nestedException);
		}
	}

	public void warning(Locator[] locs, String errorMessage) {
		if (nextController != null) {
			nextController.warning(locs, errorMessage);
		}
	}

	public InputSource resolveEntity(String publicId, String systemId) {
		return null;
	}

	public LSResourceResolver getLSResourceResolver() {
		return this;
	}

	public LSInput resolveResource(String type, String namespaceURI,
			String publicId, String systemId, String baseURI) {
		EmbeddedSchema schema = schemas.get(namespaceURI);
		if (schema != null) {
			return new DOMLSInputImpl(this.baseURI, schema.getSystemId(), 
					schema.getSchemaElement());
		} else {
			return null;
		}
	}
}
