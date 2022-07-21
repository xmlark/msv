/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>**********************************************************************
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
