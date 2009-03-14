/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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