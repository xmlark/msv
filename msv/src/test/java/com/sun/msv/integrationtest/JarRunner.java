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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;

public class JarRunner {

    private static final Logger LOG = Logger.getLogger(JarRunner.class.getName());
    private static final String JAR_NAME_PREFIX = "msv-core-";
    private static final String JAR_NAME_SUFFIX = "-jar-with-dependencies.jar";
    private static final String JAR_PARAMETER = "-jar";

    static String getJarPath() {
        String msvVersion = System.getProperty("msv.version");
        String workingDir = System.getProperty("user.dir");
        return workingDir + File.separatorChar + "target" + File.separatorChar + JAR_NAME_PREFIX + msvVersion + JAR_NAME_SUFFIX;
    }

    static void getOutputLinesFromJarCall(String[] cmdLineParameters, String[] expectedOutputLines) {
        List<String> outputLines = getOutputLines(cmdLineParameters);
        compareOutputWithReference(outputLines, expectedOutputLines);
    }

    private static List<String> getOutputLines(String[] cmdLineParameters) {
        // CREATING THE JAR PATH
        String javaHome = System.getenv("JAVA_HOME");
        ProcessBuilder builder;
        String javaPath;
        if (javaHome == null || javaHome.isEmpty()) {
            LOG.info("JAVA_HOME not set, therefore calling default java!");
            javaPath = "java";
        } else {
            LOG.log(Level.INFO, "Calling java defined by JAVA_HOME: {0}/bin/java", javaHome);
            javaPath = System.getenv("JAVA_HOME") + File.separator + "bin" + File.separator + "java";
        }
        String jarPath = getJarPath();
        String cmdLineCall = javaPath + " -jar " + jarPath + " " + String.join(" ", cmdLineParameters);

// Enable this for debugging with an IDE as within the same process (and not as by JAR execution in a new process)
//        try {
//            Driver.run(cmdLineParameters);
//        } catch (Exception ex) {
//            Logger.getLogger(JarRunner.class.getName()).log(Level.SEVERE, null, ex);
//        }
        LOG.log(Level.INFO, cmdLineCall);
        List parameters = Arrays.asList(cmdLineParameters);
        List cmdCall = new ArrayList();
        cmdCall.add(javaPath);
        cmdCall.add(JAR_PARAMETER);
        cmdCall.add(jarPath);
        cmdCall.addAll(parameters);
        builder = new ProcessBuilder(cmdCall);
        builder.redirectErrorStream(true);
        Process p = null;
        try {
            p = builder.start();
        } catch (IOException ex) {
            Logger.getLogger(JarRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<String> lines = null;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            lines = new LinkedList<>();
            String line = null;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                if (line.contains("Exception")) {
                    throw new IOException(line);
                }
                lines.add(line);
            }
        } catch (IOException t) {
            StringWriter errors = new StringWriter();
            t.printStackTrace(new PrintWriter(errors));
            Assert.fail(t.toString() + "\n" + errors.toString());
        }
        return lines;
    }

    private static void compareOutputWithReference(List<String> outputLines, String[] expectedOutputLines) {

        try {
            String outputLine = null;
            for (int i = 0; i < expectedOutputLines.length; i++) {
                outputLine = outputLines.get(i);
                if (outputLine == null || !outputLine.equals(expectedOutputLines[i])) {
                    System.err.print("Output not as expected in line " + i + "\n");
                    String expected = expectedOutputLines[i];
                    System.err.print("EXPECTED: '" + expectedOutputLines[i] + "'\n");
                    String found = outputLine;
                    System.err.print("FOUND:    '" + outputLine + "'\n\n");
                    System.err.print("EXPECTED OUTPUT:\n");
                    for (i = 0; i < expectedOutputLines.length; i++) {
                        System.err.print(expectedOutputLines[i] + "\n");
                    }
                    System.err.print("\n\nCURRENT OUTPUT:\n");
                    for (String outLine : outputLines) {
                        System.err.print(outLine + "\n");
                    }
                    Assert.fail("\nEXPECTED: '" + expected + "'\nFOUND:    '" + found + "'\n\n");
                }
            }
        } catch (Error e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Assert.fail(e.toString() + "\n" + errors.toString());
        }
    }
    
    
    private static final String OS = System.getProperty("os.name").toLowerCase(); 
    private static boolean isWindows() {
        return OS.contains("win");
    }
    static String getUserDirectoryUrlPath(){
        String userDir = null;
        if(isWindows()){
            userDir = "/" + System.getProperty("user.dir").replace('\\', '/');
        }else{
            userDir = System.getProperty("user.dir").replace('\\', '/');
        }
        return userDir;
    }    
}
