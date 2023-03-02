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

import java.io.File;
import java.util.logging.Logger;
import org.junit.Test;

public class JarOdf13MultiPrefixIT {

    private static final Logger LOG = Logger.getLogger(JarOdf13MultiPrefixIT.class.getName());
    private static final String[] TEST_PARAMETERS = {"-warning", // dump via -debug
        "src" + File.separator + "test" + File.separator + "resources" + File.separator + "grammar" + File.separator + "odf1.3" + File.separator + "OpenDocument-v1.3-schema.rng",
        "src" + File.separator + "test" + File.separator + "resources" + File.separator + "files" + File.separator + "odf-multi-prefix.xml"};
    private static final String[] EXCPECTED_OUTPUT_LINES = {
        "start parsing a grammar.",
        "The \"IDREF\" type has IDREF semantics and therefore it must be placed directly under an attribute pattern.",
        "  location unknown",
        "The \"IDREF\" type has IDREF semantics and therefore it must be placed directly under an attribute pattern.",
        "  location unknown",
        "The \"IDREF\" type has IDREF semantics and therefore it must be placed directly under an attribute pattern.",
        "  location unknown",
        "validating src" + File.separator + "test" + File.separator + "resources" + File.separator + "files" + File.separator + "odf-multi-prefix.xml",
        "the document is valid."
    };

    @Test
    public void jarVersionTest() {
        JarRunner.getOutputLinesFromJarCall(TEST_PARAMETERS, EXCPECTED_OUTPUT_LINES);
    }
}
