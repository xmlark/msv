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

import com.sun.msv.driver.textui.Driver;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

public class JarVersionIT {

    private static final Logger LOG = Logger.getLogger(JarVersionIT.class.getName());
    private static final String[] TEST_PARAMETERS = {"-version"};
    private static final String[] EXCPECTED_OUTPUT_LINES = {
        "Multi Schema Validator (MSV)",
        "ver. " + Driver.getMsvVersion(),
        "(build " + Driver.getMsvBuildDate() + ")",
        "from " + Driver.getMsvWebsite()};

    @Test
    public void mavenEnvironmentTest() {
        // EVALUATING JAR MANIFEST INFORMATION
        LOG.log(Level.INFO, "\nDriver.getName(): {0}", Driver.getMsvName());
        Assert.assertNotNull(Driver.getMsvName());

        LOG.log(Level.INFO, "\nDriver.getTitle(): {0}", Driver.getMsvTitle());
        Assert.assertNotNull(Driver.getMsvTitle());

        LOG.log(Level.INFO, "\nDriver.getVersion(): {0}", Driver.getMsvVersion());
        Assert.assertNotNull(Driver.getMsvVersion());

        LOG.log(Level.INFO, "\nDriver.getBuildDate(): {0}", Driver.getMsvBuildDate());
        Assert.assertNotNull(Driver.getMsvBuildDate());
    }

    @Test
    public void jarVersionTest() {
        JarRunner.getOutputLinesFromJarCall(TEST_PARAMETERS, EXCPECTED_OUTPUT_LINES);
    }
}
