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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

public class JarManifestIT {

  private static final Logger LOG = Logger.getLogger(JarManifestIT.class.getName());
  private static final String JAR_NAME_PREFIX = "msv-core-";
  private static final String JAR_NAME_SUFFIX_1 = "-jar-with-dependencies.jar";

  @Test
  public void testMsvWithDependenciesJar() {
    testJar(JAR_NAME_SUFFIX_1);
  }

  private void testJar(String commandSuffix) {
    try {
      // CREATING THE JAR PATH
      String msvVersion = System.getProperty("msv.version");
      String jarPath =
          "target" + File.separatorChar + JAR_NAME_PREFIX + msvVersion + commandSuffix;

      // TRIGGERING COMMAND LINE JAR EXECUTION
      String firstOutputLine = null;
      String secondOutputLine = null;
      String thirdOutputLine = null;
      String fourthOutputLine = null;
      try {
        String javaHome = System.getenv("JAVA_HOME");
        ProcessBuilder builder;
        String javaPath;
        if (javaHome == null || javaHome.isEmpty()) {
          LOG.info("JAVA_HOME not set, therefore calling default java!");
          javaPath = "java";
        } else {
          LOG.log(Level.INFO, "Calling java defined by JAVA_HOME: {0}/bin/java", javaHome);
          javaPath = System.getenv("JAVA_HOME") + "/bin/java";
        }
        builder = new ProcessBuilder(javaPath, "-jar", jarPath, "-version");
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
          line = r.readLine();
          if (line == null) {
            break;
          }
          if (line.contains("Exception")) {
            throw new IOException(line);
          }
          if (firstOutputLine == null) {
            firstOutputLine = line;
          } else {
            if (secondOutputLine == null) {
                secondOutputLine = line;
            }else {
                if (thirdOutputLine == null) {
                    thirdOutputLine = line;
                }else {
                    fourthOutputLine = line;
                }
            }
          } 
        }
      } catch (IOException t) {
        StringWriter errors = new StringWriter();
        t.printStackTrace(new PrintWriter(errors));
        Assert.fail(t.toString() + "\n" + errors.toString());
      }

      // EVALUATING COMMAND LINE INFORMATION
      LOG.log(
          Level.INFO,
          "The version info from commandline given by {0} is:\n",
          "java -jar" + jarPath);
      LOG.log(Level.INFO, "\"{0}\"", firstOutputLine);
      LOG.log(Level.INFO, "\"{0}\"", secondOutputLine);
      LOG.log(Level.INFO, "\"{0}\"", thirdOutputLine);
      LOG.log(Level.INFO, "\"{0}\"", fourthOutputLine);
      Assert.assertEquals(
            "Multi Schema Validator (MSV)", firstOutputLine);
      Assert.assertEquals(
              "ver. " + Driver.getMsvVersion(),
            secondOutputLine);
      Assert.assertEquals(
              "(build "
              + Driver.getMsvBuildDate()
              + ")",
            thirdOutputLine);
      Assert.assertEquals(
              "from "
              + Driver.getMsvWebsite(),
            fourthOutputLine);

      // EVALUATING JAR MANIFEST INFORMATION
      LOG.log(Level.INFO, "\nDriver.getName(): {0}", Driver.getMsvName());
      Assert.assertNotNull(Driver.getMsvName());

      LOG.log(Level.INFO, "\nDriver.getTitle(): {0}", Driver.getMsvTitle());
      Assert.assertNotNull(Driver.getMsvTitle());

      LOG.log(Level.INFO, "\nDriver.getVersion(): {0}", Driver.getMsvVersion());
      Assert.assertNotNull(Driver.getMsvVersion());

      LOG.log(Level.INFO, "\nDriver.getBuildDate(): {0}", Driver.getMsvBuildDate());
      Assert.assertNotNull(Driver.getMsvBuildDate());

    } catch (Exception e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }
}
