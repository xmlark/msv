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

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * XSLTLiaison implementation for SAXON.
 * 
 * This doesn't use Saxon any longer. It uses the JDK TraX.
 * 
 * <p>
 * This class 
 */
public class SAXONLiaison implements org.apache.tools.ant.taskdefs.XSLTLiaison
{
    /** The trax TransformerFactory */
    private TransformerFactory tfactory = null;

    /** stylesheet stream, close it asap */
    private FileInputStream xslStream = null;

    /** Stylesheet template */
    private Templates templates = null;

    /** transformer */
    private Transformer transformer = null;

    public SAXONLiaison() throws Exception {
        tfactory = TransformerFactory.newInstance();
    }
	
//------------------- IMPORTANT
    // 1) Don't use the StreamSource(File) ctor. It won't work with
    // xalan prior to 2.2 because of systemid bugs.

    // 2) Use a stream so that you can close it yourself quickly
    // and avoid keeping the handle until the object is garbaged.
    // (always keep control), otherwise you won't be able to delete
    // the file quickly on windows.

    // 3) Always set the systemid to the source for imports, includes...
    // in xsl and xml...

    public void setStylesheet(File stylesheet) throws Exception {
        xslStream = new FileInputStream(stylesheet);
        StreamSource src = new StreamSource(xslStream);
        src.setSystemId(getSystemId(stylesheet));
        templates = tfactory.newTemplates(src);
        transformer = templates.newTransformer();
    }

    public void transform(File infile, File outfile) throws Exception {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(infile);
            fos = new FileOutputStream(outfile);
            StreamSource src = new StreamSource(fis);
            src.setSystemId(getSystemId(infile));
            StreamResult res = new StreamResult(fos);
            // not sure what could be the need of this...
            res.setSystemId(getSystemId(outfile));

            transformer.transform(src, res);
        } finally {
            // make sure to close all handles, otherwise the garbage
            // collector will close them...whenever possible and
            // Windows may complain about not being able to delete files.
            try {
                if (xslStream != null){
                    xslStream.close();
                }
            } catch (IOException ignored){}
            try {
                if (fis != null){
                    fis.close();
                }
            } catch (IOException ignored){}
            try {
                if (fos != null){
                    fos.close();
                }
            } catch (IOException ignored){}
        }
    }

    // make sure that the systemid is made of '/' and not '\' otherwise
    // crimson will complain that it cannot resolve relative entities
    // because it grabs the base uri via lastIndexOf('/') without
    // making sure it is really a /'ed path
    protected String getSystemId(File file){
      String path = file.getAbsolutePath();
      path = path.replace('\\','/');
      return FILE_PROTOCOL_PREFIX + path;
    }

    public void addParam(String name, String value){
        transformer.setParameter(name, value);
    }
}