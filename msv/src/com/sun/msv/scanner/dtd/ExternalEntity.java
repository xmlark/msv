/*
 * @(#)ExternalEntity.java	1.3 00/02/24
 * 
 * Copyright (c) 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */

package com.sun.tranquilo.scanner.dtd;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.EntityResolver;
class ExternalEntity extends EntityDecl
{
    String	systemId;	// resolved URI (not relative)
    String	publicId;	// "-//xyz//....//en"
    String	notation;
    
    public ExternalEntity (InputEntity in) { }
    
	public InputSource getInputSource (EntityResolver r)
                       throws IOException, SAXException {

		InputSource	retval;
	
		retval = r.resolveEntity (publicId, systemId);
		// SAX sez if null is returned, use the URI directly
		if (retval == null)
		    retval = Resolver.createInputSource (new URL (systemId), false);
		return retval;
    }
}
