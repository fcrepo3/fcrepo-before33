// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Main.java

package com.sun.xml.rpc.tools.xrpcc;

import com.sun.xml.rpc.processor.Processor;
import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.ProcessorNotificationListener;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.ConfigurationException;
import com.sun.xml.rpc.processor.config.RmiModelInfo;
import com.sun.xml.rpc.processor.config.WSDLModelInfo;
import com.sun.xml.rpc.processor.config.parser.ConfigurationParser;
import com.sun.xml.rpc.processor.generator.CustomClassGenerator;
import com.sun.xml.rpc.processor.generator.CustomExceptionGenerator;
import com.sun.xml.rpc.processor.generator.EnumerationEncoderGenerator;
import com.sun.xml.rpc.processor.generator.EnumerationGenerator;
import com.sun.xml.rpc.processor.generator.FaultExceptionBuilderGenerator;
import com.sun.xml.rpc.processor.generator.HolderGenerator;
import com.sun.xml.rpc.processor.generator.LiteralObjectSerializerGenerator;
import com.sun.xml.rpc.processor.generator.RemoteInterfaceGenerator;
import com.sun.xml.rpc.processor.generator.RemoteInterfaceImplGenerator;
import com.sun.xml.rpc.processor.generator.SOAPFaultSerializerGenerator;
import com.sun.xml.rpc.processor.generator.SOAPObjectBuilderGenerator;
import com.sun.xml.rpc.processor.generator.SOAPObjectSerializerGenerator;
import com.sun.xml.rpc.processor.generator.SerializerRegistryGenerator;
import com.sun.xml.rpc.processor.generator.ServiceGenerator;
import com.sun.xml.rpc.processor.generator.ServiceInterfaceGenerator;
import com.sun.xml.rpc.processor.generator.ServletConfigGenerator;
import com.sun.xml.rpc.processor.generator.StubGenerator;
import com.sun.xml.rpc.processor.generator.TieGenerator;
import com.sun.xml.rpc.processor.generator.WSDLGenerator;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.processor.util.ModelWriter;
import com.sun.xml.rpc.processor.util.XMLModelWriter;
import com.sun.xml.rpc.util.localization.Localizable;
import com.sun.xml.rpc.util.localization.Localizer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassFile;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Constants;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.javac.SourceClass;

// Referenced classes of package com.sun.xml.rpc.tools.xrpcc:
//            ActionConstants

public class Main
    implements Constants, ActionConstants, ProcessorNotificationListener {

    String classPathString;
    File destDir;
    File sourceDir;
    File configFile;
    int flags;
    long tm;
    String modelOutputFilename;
    String modelOutputFilenameXML;
    boolean nowrite;
    boolean nocompile;
    boolean keepGenerated;
    boolean status;
    boolean genInterface;
    boolean genStub;
    boolean genTie;
    boolean genServletConfig;
    boolean genService;
    boolean genServiceInterface;
    boolean noEncodedTypes;
    boolean noMultiRefEncoding;
    boolean noValidation;
    boolean explicitServiceContext;
    boolean genWsdl;
    boolean showModel;
    boolean showModelXML;
    boolean printStackTrace;
    boolean noDataBinding;
    boolean doClient;
    boolean doServer;
    boolean importOnly;
    HashMap actions;
    File actionsFile;
    Localizer localizer;
    String sourcePathArg;
    String serializerNameInfix;
    String program;
    OutputStream out;
    private static boolean resourcesInitialized = false;
    private static ResourceBundle resources;

    public Main(OutputStream out, String program) {
        this.out = out;
        this.program = program;
        localizer = new Localizer();
        actions = new HashMap();
        actions.put("remote.interface.generator", new RemoteInterfaceGenerator());
        actions.put("remote.interface.impl.generator", new RemoteInterfaceImplGenerator());
        actions.put("custom.class.generator", new CustomClassGenerator());
        actions.put("soap.object.serializer.generator", new SOAPObjectSerializerGenerator());
        actions.put("soap.object.builder.generator", new SOAPObjectBuilderGenerator());
        actions.put("literal.object.serializer.generator", new LiteralObjectSerializerGenerator());
        actions.put("stub.generator", new StubGenerator());
        actions.put("tie.generator", new TieGenerator());
        actions.put("servlet.config.generator", new ServletConfigGenerator());
        actions.put("wsdl.generator", new WSDLGenerator());
        actions.put("holder.generator", new HolderGenerator());
        actions.put("service.interface.generator", new ServiceInterfaceGenerator());
        actions.put("service.generator", new ServiceGenerator());
        actions.put("serializer.registry.generator", new SerializerRegistryGenerator());
        actions.put("custom.exception.generator", new CustomExceptionGenerator());
        actions.put("fault.exception.builder.generator", new FaultExceptionBuilderGenerator());
        actions.put("soap.fault.serializer.generator", new SOAPFaultSerializerGenerator());
        actions.put("enumeration.generator", new EnumerationGenerator());
        actions.put("enumeration.encoder.generator", new EnumerationEncoderGenerator());
    }

    public void output(String msg) {
        PrintStream out = (this.out instanceof PrintStream) ? (PrintStream)this.out : new PrintStream(this.out, true);
        out.println(msg);
    }

    public void error(String msg) {
        output(getText(msg));
    }

    public void error(String msg, String arg1) {
        output(getText(msg, arg1));
    }

    public void error(String msg, String arg1, String arg2) {
        output(getText(msg, arg1, arg2));
    }

    public void usage() {
        error("xrpcc.usage", program);
    }

    public synchronized boolean compile(String argv[]) {
        if(!parseArgs(argv))
            return false;
        else
            return doCompile();
    }

    public File getDestinationDir() {
        return destDir;
    }

    public boolean parseArgs(String argv[]) {
        sourcePathArg = null;
        classPathString = null;
        destDir = null;
        sourceDir = null;
        configFile = null;
        flags = 4;
        tm = System.currentTimeMillis();
        nowrite = false;
        nocompile = false;
        keepGenerated = false;
        genStub = false;
        genService = false;
        genTie = false;
        genServletConfig = false;
        noEncodedTypes = false;
        noMultiRefEncoding = false;
        actionsFile = null;
        serializerNameInfix = null;
        for(int i = 0; i < argv.length; i++)
            if(argv[i].equals("-g")) {
                flags &= 0xffffbfff;
                flags |= 0x3000;
                argv[i] = null;
            } else
            if(argv[i].equals("-nowarn")) {
                flags &= -5;
                argv[i] = null;
            } else
            if(argv[i].equals("-debug")) {
                flags |= 2;
                argv[i] = null;
            } else
            if(argv[i].equals("-depend")) {
                flags |= 0x20;
                argv[i] = null;
            } else
            if(argv[i].equals("-verbose")) {
                flags |= 1;
                argv[i] = null;
            } else {
                if(argv[i].equals("-version")) {
                    error("xrpcc.version", "JAX-RPC Reference Implementation", "EA2-R16");
                    return false;
                }
                if(argv[i].equals("-nowrite")) {
                    nowrite = true;
                    argv[i] = null;
                } else
                if(argv[i].equals("-Xnocompile")) {
                    nocompile = true;
                    keepGenerated = true;
                    argv[i] = null;
                } else
                if(argv[i].equals("-Xprintstacktrace")) {
                    printStackTrace = true;
                    argv[i] = null;
                } else
                if(argv[i].equals("-Xnodatabinding")) {
                    noDataBinding = true;
                    argv[i] = null;
                } else
                if(argv[i].equals("-keep") || argv[i].equals("-keepgenerated")) {
                    keepGenerated = true;
                    argv[i] = null;
                } else {
                    if(argv[i].equals("-show")) {
                        error("xrpcc.option.unsupported", "-show");
                        usage();
                        return false;
                    }
                    if(argv[i].equals("-classpath")) {
                        if(i + 1 < argv.length) {
                            if(classPathString != null) {
                                error("xrpcc.option.already.seen", "-classpath");
                                usage();
                                return false;
                            }
                            argv[i] = null;
                            classPathString = argv[++i];
                            argv[i] = null;
                        }
                    } else
                    if(argv[i].equals("-d")) {
                        if(i + 1 < argv.length) {
                            if(destDir != null) {
                                error("xrpcc.option.already.seen", "-d");
                                usage();
                                return false;
                            }
                            argv[i] = null;
                            destDir = new File(argv[++i]);
                            argv[i] = null;
                            if(!destDir.exists()) {
                                error("xrpcc.no.such.directory", destDir.getPath());
                                usage();
                                return false;
                            }
                        } else {
                            error("xrpcc.option.requires.argument", "-d");
                            usage();
                            return false;
                        }
                    } else
                    if(argv[i].equals("-s")) {
                        if(i + 1 < argv.length) {
                            if(sourceDir != null) {
                                error("xrpcc.option.already.seen", "-s");
                                usage();
                                return false;
                            }
                            argv[i] = null;
                            sourceDir = new File(argv[++i]);
                            argv[i] = null;
                            if(!sourceDir.exists()) {
                                error("xrpcc.no.such.directory", sourceDir.getPath());
                                usage();
                                return false;
                            }
                        } else {
                            error("xrpcc.option.requires.argument", "-s");
                            usage();
                            return false;
                        }
                    } else
                    if(argv[i].equalsIgnoreCase("-client")) {
                        if(doServer) {
                            error("xrpcc.option.incompatible", argv[i]);
                            usage();
                            return false;
                        }
                        argv[i] = null;
                        doClient = true;
                    } else
                    if(argv[i].equalsIgnoreCase("-server")) {
                        if(doClient) {
                            error("xrpcc.option.incompatible", argv[i]);
                            usage();
                            return false;
                        }
                        argv[i] = null;
                        doServer = true;
                    } else
                    if(argv[i].equalsIgnoreCase("-both")) {
                        if(doClient || doServer) {
                            error("xrpcc.option.incompatible", argv[i]);
                            usage();
                            return false;
                        }
                        argv[i] = null;
                        doClient = true;
                        doServer = true;
                    } else
                    if(argv[i].equalsIgnoreCase("-Ximport")) {
                        argv[i] = null;
                        importOnly = true;
                    } else
                    if(argv[i].startsWith("-Xmodel")) {
                        int index = argv[i].indexOf(':');
                        if(index != -1)
                            modelOutputFilename = argv[i].substring(index + 1);
                        argv[i] = null;
                        showModel = true;
                    } else
                    if(argv[i].startsWith("-Xxmodel")) {
                        int index = argv[i].indexOf(':');
                        if(index != -1) {
                            modelOutputFilenameXML = argv[i].substring(index + 1);
                            showModelXML = true;
                        }
                        argv[i] = null;
                    } else
                    if(argv[i].equals("-Xnomultirefs")) {
                        argv[i] = null;
                        noMultiRefEncoding = true;
                    } else
                    if(argv[i].equals("-Xnoencodedtypes")) {
                        argv[i] = null;
                        noEncodedTypes = true;
                    } else
                    if(argv[i].equals("-Xnovalidation")) {
                        argv[i] = null;
                        noValidation = true;
                    } else
                    if(argv[i].equals("-Xexplicitcontext")) {
                        argv[i] = null;
                        explicitServiceContext = true;
                    } else
                    if(argv[i].startsWith("-Xactions")) {
                        if(actionsFile != null) {
                            error("xrpcc.option.already.seen", "-Xactions");
                            usage();
                            return false;
                        }
                        int index = argv[i].indexOf(':');
                        if(index == -1) {
                            error("xrpcc.option.requires.argument", "-Xactions");
                            usage();
                            return false;
                        }
                        actionsFile = new File(argv[i].substring(index + 1));
                        argv[i] = null;
                        if(!actionsFile.exists()) {
                            error("xrpcc.no.such.file", actionsFile.getPath());
                            usage();
                            return false;
                        }
                    } else
                    if(argv[i].startsWith("-Xhttpproxy")) {
                        int index1 = argv[i].indexOf(':');
                        if(index1 == -1) {
                            error("xrpcc.option.requires.argument", "-Xhttpproxy");
                            usage();
                            return false;
                        }
                        int index2 = argv[i].indexOf(':', index1 + 1);
                        if(index2 == -1) {
                            System.setProperty("proxySet", "true");
                            System.setProperty("proxyHost", argv[i].substring(index1 + 1));
                            System.setProperty("proxyPort", "8080");
                        } else {
                            System.setProperty("proxySet", "true");
                            System.setProperty("proxyHost", argv[i].substring(index1 + 1, index2));
                            System.setProperty("proxyPort", argv[i].substring(index2 + 1));
                        }
                        argv[i] = null;
                    }
                }
            }

        if(destDir == null)
            destDir = new File(".");
        for(int i = 0; i < argv.length; i++)
            if(argv[i] != null) {
                if(argv[i].startsWith("-")) {
                    error("xrpcc.no.such.option", argv[i]);
                    usage();
                    return false;
                }
                if(configFile != null) {
                    error("xrpcc.only.one.configuration.file", argv[i]);
                    usage();
                    return false;
                }
                configFile = new File(argv[i]);
                argv[i] = null;
                if(!configFile.exists()) {
                    error("xrpcc.no.such.file", configFile.getPath());
                    usage();
                    return false;
                }
            }

        if(!doClient && !doServer) {
            error("xrpcc.option.mustSpecifyClientOrServer");
            usage();
            return false;
        }
        if(doClient) {
            genStub = true;
            genService = true;
            genServiceInterface = true;
            genInterface = true;
        }
        if(doServer) {
            genTie = true;
            genServletConfig = true;
            genWsdl = true;
            genInterface = true;
        }
        if(importOnly) {
            genStub = false;
            genTie = false;
            genService = false;
            genWsdl = false;
            genServletConfig = false;
        }
        return true;
    }

    public BatchEnvironment getEnv() {
        String cpath = classPathString + File.pathSeparator + System.getProperty("java.class.path");
        sun.tools.java.ClassPath classPath = BatchEnvironment.createClassPath(cpath);
        sun.tools.java.ClassPath sourcePath = BatchEnvironment.createClassPath(sourcePathArg);
        BatchEnvironment result = new BatchEnvironment(System.out, sourcePath, classPath, this);
        return result;
    }

    public boolean doCompile() {
        Properties properties = new Properties();
        BatchEnvironment env = getEnv();
        env.flags |= flags;
        if(sourceDir == null)
            sourceDir = destDir;
        properties.setProperty("sourceDirectory", sourceDir.getAbsolutePath());
        properties.setProperty("destinationDirectory", destDir.getAbsolutePath());
        properties.setProperty("encodeTypes", noEncodedTypes ? "false" : "true");
        properties.setProperty("multiRefEncoding", noMultiRefEncoding ? "false" : "true");
        properties.setProperty("validationWSDL", noValidation ? "false" : "true");
        properties.setProperty("explicitServiceContext", explicitServiceContext ? "true" : "false");
        properties.setProperty("printStackTrace", printStackTrace ? "true" : "false");
        properties.setProperty("noDataBinding", noDataBinding ? "true" : "false");
        if(serializerNameInfix != null)
            properties.setProperty("serializerNameInfix", serializerNameInfix);
        if(configFile == null) {
            error("xrpcc.configuration.file.not.found");
            usage();
            return false;
        }
        String noMemoryErrorString = getText("xrpcc.no.memory");
        String stackOverflowErrorString = getText("xrpcc.stack.overflow");
        try {
            ConfigurationParser parser = new ConfigurationParser(env);
            Configuration config = parser.parse(new FileInputStream(configFile));
            Processor processor = new Processor(config, properties);
            if(actionsFile != null) {
                Properties props = new Properties();
                props.load(new FileInputStream(actionsFile));
                String propName;
                Object processorAction;
                for(Enumeration enum = props.propertyNames(); enum.hasMoreElements(); actions.put(propName.toLowerCase(), processorAction)) {
                    propName = (String)enum.nextElement();
                    String className = props.getProperty(propName);
                    Class processorClass;
                    try {
                        processorClass = Class.forName(className);
                    }
                    catch(ClassNotFoundException classnotfoundexception) {
                        error("xrpcc.invalid.processoraction.class", className);
                        return false;
                    }
                    processorAction = processorClass.newInstance();
                    if(!(processorAction instanceof ProcessorAction)) {
                        error("xrpcc.invalid.processoraction.class", className);
                        return false;
                    }
                    output(getText("xrpcc.replacing.action.class", actions.get(propName.toLowerCase()).getClass().toString(), processorAction.getClass().toString()));
                }

            }
            if(importOnly && !(config.getModelInfo() instanceof WSDLModelInfo))
                error("xrpcc.option.import.requiresWSDL");
            processor.runModeler();
            if(showModel)
                if(modelOutputFilename == null)
                    processor.add(new ModelWriter(System.out));
                else
                    processor.add(new ModelWriter(new FileOutputStream(new File(destDir, modelOutputFilename))));
            if(showModelXML)
                processor.add(new XMLModelWriter(new FileOutputStream(new File(destDir, modelOutputFilenameXML))));
            if(genServiceInterface)
                processor.add((ProcessorAction)actions.get("service.interface.generator"));
            if(genService) {
                processor.add((ProcessorAction)actions.get("service.generator"));
                processor.add((ProcessorAction)actions.get("serializer.registry.generator"));
            }
            if(genInterface && !(config.getModelInfo() instanceof RmiModelInfo)) {
                processor.add((ProcessorAction)actions.get("remote.interface.generator"));
                processor.add((ProcessorAction)actions.get("remote.interface.impl.generator"));
            }
            if(genStub || genTie) {
                processor.add((ProcessorAction)actions.get("enumeration.generator"));
                processor.add((ProcessorAction)actions.get("enumeration.encoder.generator"));
                processor.add((ProcessorAction)actions.get("holder.generator"));
                processor.add((ProcessorAction)actions.get("custom.class.generator"));
                processor.add((ProcessorAction)actions.get("soap.object.serializer.generator"));
                processor.add((ProcessorAction)actions.get("soap.object.builder.generator"));
                processor.add((ProcessorAction)actions.get("literal.object.serializer.generator"));
                processor.add((ProcessorAction)actions.get("custom.exception.generator"));
                processor.add((ProcessorAction)actions.get("fault.exception.builder.generator"));
                processor.add((ProcessorAction)actions.get("soap.fault.serializer.generator"));
            } else
            if(importOnly) {
                processor.add((ProcessorAction)actions.get("enumeration.generator"));
                processor.add((ProcessorAction)actions.get("holder.generator"));
                processor.add((ProcessorAction)actions.get("custom.class.generator"));
                processor.add((ProcessorAction)actions.get("custom.exception.generator"));
            }
            if(genStub)
                processor.add((ProcessorAction)actions.get("stub.generator"));
            if(genTie && !genService)
                processor.add((ProcessorAction)actions.get("serializer.registry.generator"));
            if(genTie)
                processor.add((ProcessorAction)actions.get("tie.generator"));
            if(genServletConfig)
                processor.add((ProcessorAction)actions.get("servlet.config.generator"));
            if(genWsdl && !(config.getModelInfo() instanceof WSDLModelInfo))
                processor.add((ProcessorAction)actions.get("wsdl.generator"));
            processor.runActions();
            if(!nocompile && ((sun.tools.javac.BatchEnvironment) (env)).nerrors == 0)
                compileAllClasses(env);
        }
        catch(ConfigurationException e) {
            onError(e);
            return false;
        }
        catch(OutOfMemoryError outofmemoryerror) {
            env.output(noMemoryErrorString);
            return false;
        }
        catch(StackOverflowError ee) {
            if(printStackTrace)
                ee.printStackTrace();
            env.output(stackOverflowErrorString);
            return false;
        }
        catch(Error ee) {
            if(((sun.tools.javac.BatchEnvironment) (env)).nerrors == 0 || env.dump()) {
                ee.printStackTrace();
                env.error(0L, "fatal.error");
            }
        }
        catch(Exception ee) {
            if(((sun.tools.javac.BatchEnvironment) (env)).nerrors == 0 || env.dump()) {
                env.output(ee.getMessage());
                ee.printStackTrace();
                env.error(0L, "fatal.exception");
            }
        }
        env.flushErrors();
        boolean status = true;
        if(((sun.tools.javac.BatchEnvironment) (env)).nerrors > 0) {
            String msg = "";
            if(((sun.tools.javac.BatchEnvironment) (env)).nerrors > 1)
                msg = getText("xrpcc.errors", ((sun.tools.javac.BatchEnvironment) (env)).nerrors);
            else
                msg = getText("xrpcc.1error");
            if(((sun.tools.javac.BatchEnvironment) (env)).nwarnings > 0)
                if(((sun.tools.javac.BatchEnvironment) (env)).nwarnings > 1)
                    msg = msg + ", " + getText("xrpcc.warnings", ((sun.tools.javac.BatchEnvironment) (env)).nwarnings);
                else
                    msg = msg + ", " + getText("xrpcc.1warning");
            output(msg);
            status = false;
        } else
        if(((sun.tools.javac.BatchEnvironment) (env)).nwarnings > 0)
            if(((sun.tools.javac.BatchEnvironment) (env)).nwarnings > 1)
                output(getText("xrpcc.warnings", ((sun.tools.javac.BatchEnvironment) (env)).nwarnings));
            else
                output(getText("xrpcc.1warning"));
        if(!keepGenerated)
            env.deleteGeneratedFiles();
        if(env.verbose()) {
            tm = System.currentTimeMillis() - tm;
            output(getText("xrpcc.done_in", Long.toString(tm)));
        }
        env.shutdown();
        return status;
    }

    public void compileAllClasses(BatchEnvironment env) throws ClassNotFound, IOException, InterruptedException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream(4096);
        boolean done;
        do {
            done = true;
            for(Enumeration e = env.getClasses(); e.hasMoreElements();) {
                ClassDeclaration c = (ClassDeclaration)e.nextElement();
                done = compileClass(c, buf, env);
            }

        } while(!done);
    }

    public boolean compileClass(ClassDeclaration c, ByteArrayOutputStream buf, BatchEnvironment env) throws ClassNotFound, IOException, InterruptedException {
        boolean done;
label0:
        {
            done = true;
            env.flushErrors();
            switch(c.getStatus()) {
            case 1: // '\001'
            case 2: // '\002'
            default:
                break;

            case 0: // '\0'
            {
                if(!env.dependencies())
                    break label0;
                // fall through
            }

            case 3: // '\003'
            {
                done = false;
                env.loadDefinition(c);
                if(c.getStatus() != 4)
                    break label0;
                // fall through
            }

            case 4: // '\004'
            {
                if(c.getClassDefinition().isInsideLocal())
                    break label0;
                if(nocompile)
                    throw new IOException("Compilation required, but -Xnocompile option in effect");
                done = false;
                SourceClass src = (SourceClass)c.getClassDefinition(env);
                src.check(env);
                c.setDefinition(src, 5);
                // fall through
            }

            case 5: // '\005'
            {
                SourceClass src = (SourceClass)c.getClassDefinition(env);
                if(src.getError()) {
                    c.setDefinition(src, 6);
                    break label0;
                }
                done = false;
                buf.reset();
                src.compile(buf);
                c.setDefinition(src, 6);
                src.cleanup(env);
                if(src.getError())
                    break label0;
                String pkgName = c.getName().getQualifier().toString().replace('.', File.separatorChar);
                String className = c.getName().getFlatName().toString().replace('.', '$') + ".class";
                File file;
                if(destDir != null) {
                    if(pkgName.length() > 0) {
                        file = new File(destDir, pkgName);
                        if(!file.exists())
                            file.mkdirs();
                        file = new File(file, className);
                    } else {
                        file = new File(destDir, className);
                    }
                } else {
                    ClassFile classfile = (ClassFile)src.getSource();
                    if(classfile.isZipped()) {
                        env.error(0L, "cant.write", classfile.getPath());
                        break label0;
                    }
                    file = new File(classfile.getPath());
                    file = new File(file.getParent(), className);
                }
                try {
                    FileOutputStream out = new FileOutputStream(file.getPath());
                    buf.writeTo(out);
                    out.close();
                    if(env.verbose())
                        output(getText("xrpcc.wrote", file.getPath()));
                }
                catch(IOException ioexception) {
                    env.error(0L, "cant.write", file.getPath());
                }
                break;
            }
            }
        }
        return done;
    }

    public void onError(Localizable msg) {
        output(getText("xrpcc.error", localizer.localize(msg)));
    }

    public void onWarning(Localizable msg) {
        output(getText("xrpcc.warning", localizer.localize(msg)));
    }

    public void onInfo(Localizable msg) {
        output(getText("xrpcc.info", localizer.localize(msg)));
    }

    public static void main(String argv[]) {
        Main compiler = new Main(System.out, "xrpcc");
        System.exit(compiler.compile(argv) ? 0 : 1);
    }

    public static String getString(String key) {
        if(!resourcesInitialized)
            initResources();
        try {
            return resources.getString(key);
        }
        catch(MissingResourceException missingresourceexception) {
            return null;
        }
    }

    private static void initResources() {
        try {
            resources = ResourceBundle.getBundle("com.sun.xml.rpc.resources.xrpcc");
            resourcesInitialized = true;
        }
        catch(MissingResourceException e) {
            throw new Error("fatal: missing resource bundle: " + e.getClassName());
        }
    }

    public static String getText(String key) {
        String message = getString(key);
        if(message == null)
            message = "no text found: \"" + key + "\"";
        return message;
    }

    public static String getText(String key, int num) {
        return getText(key, Integer.toString(num), null, null);
    }

    public static String getText(String key, String arg0) {
        return getText(key, arg0, null, null);
    }

    public static String getText(String key, String arg0, String arg1) {
        return getText(key, arg0, arg1, null);
    }

    public static String getText(String key, String arg0, String arg1, String arg2) {
        String format = getString(key);
        if(format == null)
            format = "no text found: key = \"" + key + "\", " + "arguments = \"{0}\", \"{1}\", \"{2}\"";
        String args[] = new String[3];
        args[0] = arg0 == null ? "null" : arg0.toString();
        args[1] = arg1 == null ? "null" : arg1.toString();
        args[2] = arg2 == null ? "null" : arg2.toString();
        return MessageFormat.format(format, args);
    }

}
