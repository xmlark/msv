/*
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
package com.sun.msv.integrationtest;

import static com.sun.msv.integrationtest.JarRunner.getUserDirectoryUrlPath;
import java.io.File;
import java.util.logging.Logger;
import org.junit.Test;

public class JarUbl23Invoice_IT {

    private static final Logger LOG = Logger.getLogger(JarUbl23Invoice_IT.class.getName());
    private static final String[] TEST_PARAMETERS = {"-warning",
        "src" + File.separator + "test" + File.separator + "resources" + File.separator + "grammar" + File.separator + "ubl2.3" + File.separator + "xsd" + File.separator + "maindoc" + File.separator + "UBL-Invoice-2.3.xsd",
        "src" + File.separator + "test" + File.separator + "resources" + File.separator + "files" + File.separator + "ubl2.3" + File.separator + "UBL-Invoice-2.1-Example.xml"};
    private static final String[] EXCPECTED_OUTPUT_LINES = {
        "start parsing a grammar.",
        "\"anyType\" is implicitly used as the content model of this element. Is this your intention? If so, please consider to write it explicitly as type=\"anyType\".",
        "  279:48@file:" + getUserDirectoryUrlPath() + "/src/test/resources/grammar/ubl2.3/xsd/common/UBL-XAdES01903v132-201601-2.3.xsd",
        "\"anyType\" is implicitly used as the content model of this element. Is this your intention? If so, please consider to write it explicitly as type=\"anyType\".",
        "  343:47@file:" + getUserDirectoryUrlPath() + "/src/test/resources/grammar/ubl2.3/xsd/common/UBL-XAdES01903v132-201601-2.3.xsd",
        "validating src" + File.separator + "test" + File.separator + "resources" + File.separator + "files" + File.separator + "ubl2.3" + File.separator + "UBL-Invoice-2.1-Example.xml",
        "the document is valid."
    };

    @Test
    public void jarVersionTest() {
        JarRunner.getOutputLinesFromJarCall(TEST_PARAMETERS, EXCPECTED_OUTPUT_LINES);
    }
}
