// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Xrpcc.java

package com.sun.xml.rpc.tools.ant;

import com.sun.xml.rpc.tools.xrpcc.Main;
import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.*;

public class Xrpcc extends MatchingTask {

    protected boolean verbose;
    protected boolean both;
    protected Path compileClasspath;
    protected boolean client;
    protected boolean server;
    private File baseDir;
    private boolean debug;
    private File sourceBase;
    private boolean includeAntRuntime;
    private boolean includeJavaRuntime;
    private File config;

    public Xrpcc() {
        verbose = false;
        both = true;
        client = false;
        server = false;
        debug = false;
        includeAntRuntime = true;
        includeJavaRuntime = false;
    }

    public boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean getBoth() {
        return both;
    }

    public void setBoth(boolean both) {
        this.both = both;
    }

    public void setClasspath(Path classpath) {
        if(compileClasspath == null)
            compileClasspath = classpath;
        else
            compileClasspath.append(classpath);
    }

    public Path createClasspath() {
        if(compileClasspath == null)
            compileClasspath = new Path(super.project);
        return compileClasspath.createPath();
    }

    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }

    public Path getClasspath() {
        return compileClasspath;
    }

    public boolean getClient() {
        return client;
    }

    public void setClient(boolean client) {
        this.client = client;
        setBoth(false);
    }

    public boolean getServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
        setBoth(false);
    }

    public void setBase(File base) {
        baseDir = base;
    }

    public File getBase() {
        return baseDir;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean getDebug() {
        return debug;
    }

    public void setSourceBase(File sourceBase) {
        this.sourceBase = sourceBase;
    }

    public File getSourceBase() {
        return sourceBase;
    }

    public void setIncludeantruntime(boolean include) {
        includeAntRuntime = include;
    }

    public boolean getIncludeantruntime() {
        return includeAntRuntime;
    }

    public void setIncludejavaruntime(boolean include) {
        includeJavaRuntime = include;
    }

    public boolean getIncludejavaruntime() {
        return includeJavaRuntime;
    }

    public void setConfig(File config) {
        this.config = config;
    }

    public File getConfig() {
        return config;
    }

    private Path generateCompileClasspath() {
        Path classpath = new Path(getProject());
        classpath.setLocation(getBase());
        if(getClasspath() == null) {
            if(getIncludeantruntime())
                classpath.addExisting(Path.systemClasspath);
        } else
        if(getIncludeantruntime())
            classpath.addExisting(getClasspath().concatSystemClasspath("last"));
        else
            classpath.addExisting(getClasspath().concatSystemClasspath("ignore"));
        if(getIncludejavaruntime()) {
            classpath.addExisting(new Path(null, System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"));
            classpath.addExisting(new Path(null, System.getProperty("java.home") + File.separator + "jre" + File.separator + "lib" + File.separator + "rt.jar"));
        }
        return classpath;
    }

    private Commandline setupXrpccCommand(String options[]) {
        Commandline cmd = new Commandline();
        if(options != null) {
            for(int i = 0; i < options.length; i++)
                cmd.createArgument().setValue(options[i]);

        }
        cmd.createArgument().setValue("-d");
        cmd.createArgument().setFile(getBase());
        Path classpath = generateCompileClasspath();
        cmd.createArgument().setValue("-classpath");
        cmd.createArgument().setPath(classpath);
        if(null != getSourceBase()) {
            cmd.createArgument().setValue("-keepgenerated");
            cmd.createArgument().setValue("-s");
            cmd.createArgument().setFile(getSourceBase());
        }
        if(getDebug())
            cmd.createArgument().setValue("-g");
        if(getBoth())
            cmd.createArgument().setValue("-both");
        else
        if(getClient())
            cmd.createArgument().setValue("-client");
        else
        if(getServer())
            cmd.createArgument().setValue("-server");
        if(getVerbose()) {
            cmd.createArgument().setValue("-verbose");
            cmd.createArgument().setValue("-Xprintstacktrace");
        }
        cmd.createArgument().setValue(config.toString());
        return cmd;
    }

    private Commandline setupXrpccCommand() {
        return setupXrpccCommand(null);
    }

    public void execute() throws BuildException {
        if(baseDir == null)
            throw new BuildException("base attribute must be set!", super.location);
        if(!baseDir.exists())
            throw new BuildException("base does not exist!", super.location);
        if(!config.exists())
            throw new BuildException("xrpcc config file does not exist!", super.location);
        Commandline cmd = setupXrpccCommand();
        if(verbose)
            log("command line: " + cmd.toString());
        LogOutputStream logstr = new LogOutputStream(this, 1);
        try {
            Main main = new Main(logstr, "xrpcc");
            boolean ok = main.compile(cmd.getArguments());
            if(!ok)
                throw new BuildException("xrpcc failed", super.location);
        }
        catch(Exception ex) {
            if(ex instanceof BuildException)
                throw (BuildException)ex;
            else
                throw new BuildException("Error starting xrpcc: ", ex, getLocation());
        }
        finally {
            try {
                logstr.close();
            }
            catch(IOException e) {
                throw new BuildException(e);
            }
        }
    }
}
