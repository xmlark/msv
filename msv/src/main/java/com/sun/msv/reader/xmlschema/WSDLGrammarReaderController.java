/*
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
