// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JavaBean.java

package com.sun.xml.rpc.processor.modeler.rmi;

import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.StringUtils;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.ClassPath;
import sun.tools.java.Constants;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;
import sun.tools.javac.BatchEnvironment;

// Referenced classes of package com.sun.xml.rpc.processor.modeler.rmi:
//            MemberInfo, RmiConstants

public class JavaBean
    implements RmiConstants, Constants {

    private HashMap members;
    private Class remoteBean;
    private ClassDefinition remoteClass;
    com.sun.xml.rpc.processor.util.BatchEnvironment env;

    private static JavaBean forType(com.sun.xml.rpc.processor.util.BatchEnvironment env, Type type) {
        JavaBean bean = null;
        try {
            ClassDeclaration cDec = env.getClassDeclaration(type.getClassName());
            ClassDefinition classDef = cDec.getClassDefinition(env);
            URLClassLoader classLoader = new URLClassLoader(pathToURLs(env.getClassPath().toString()));
            Class beanClass = classLoader.loadClass(type.getClassName().toString());
            bean = new JavaBean(env, beanClass, classDef);
            bean.initialize();
        }
        catch(ClassNotFound classnotfound) {
            throw new ModelerException("rmimodeler.class.not.found", type.getClassName().toString());
        }
        catch(ClassNotFoundException classnotfoundexception) {
            throw new ModelerException("rmimodeler.class.not.found", type.getClassName().toString());
        }
        return bean;
    }

    public static Map modelTypeSOAP(com.sun.xml.rpc.processor.util.BatchEnvironment env, Type type) {
        JavaBean bean = forType(env, type);
        if(bean == null)
            return null;
        else
            return bean.getMembers();
    }

    private HashMap getMembers() {
        return (HashMap)members.clone();
    }

    private JavaBean(com.sun.xml.rpc.processor.util.BatchEnvironment env, Class remoteBean, ClassDefinition classDef) {
        this.env = env;
        this.remoteBean = remoteBean;
        remoteClass = classDef;
    }

    private static URL[] pathToURLs(String path) {
        StringTokenizer st = new StringTokenizer(path, File.pathSeparator);
        URL urls[] = new URL[st.countTokens()];
        int count = 0;
        while(st.hasMoreTokens())  {
            URL url = fileToURL(new File(st.nextToken()));
            if(url != null)
                urls[count++] = url;
        }
        if(urls.length != count) {
            URL tmp[] = new URL[count];
            System.arraycopy(urls, 0, tmp, 0, count);
            urls = tmp;
        }
        return urls;
    }

    private static URL fileToURL(File file) {
        String name;
        try {
            name = file.getCanonicalPath();
        }
        catch(IOException ioexception) {
            name = file.getAbsolutePath();
        }
        name = name.replace(File.separatorChar, '/');
        if(!name.startsWith("/"))
            name = "/" + name;
        if(!file.isFile())
            name = name + "/";
        try {
            return new URL("file", "", name);
        }
        catch(MalformedURLException malformedurlexception) {
            throw new IllegalArgumentException("file");
        }
    }

    private void initialize() {
        members = new HashMap();
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(remoteBean);
        }
        catch(IntrospectionException introspectionexception) {
            throw new ModelerException("rmimodeler.invalid.rmi.type:", remoteBean.getName().toString());
        }
        PropertyDescriptor properties[] = beanInfo.getPropertyDescriptors();
        log(env, "looking for JavaBean members of class: " + remoteBean.getName().toString());
        for(int i = 0; i < properties.length; i++)
            if(!(properties[i] instanceof IndexedPropertyDescriptor)) {
                Class propertyType = properties[i].getPropertyType();
                Method readMethod = properties[i].getReadMethod();
                Method writeMethod = properties[i].getWriteMethod();
                if(propertyType != null && readMethod != null && writeMethod != null) {
                    String propertyName = StringUtils.decapitalize(writeMethod.getName().substring(3));
                    Type type = findType(remoteClass, readMethod.getName());
                    if(type == null)
                        throw new ModelerException("rmimodeler.could.not.resolve.property.type", remoteClass.getName().toString() + ":" + propertyName);
                    verifyProperty(remoteClass, type, writeMethod.getName());
                    log(env, "found JavaBean property: " + propertyName);
                    MemberInfo memInfo = new MemberInfo(propertyName, type, false);
                    memInfo.setReadMethod(readMethod.getName());
                    memInfo.setWriteMethod(writeMethod.getName());
                    members.put(propertyName, memInfo);
                }
            }

    }

    private boolean verifyProperty(ClassDefinition cDef, Type type, String writeMethod) {
        if((type.getTypeMask() & 0xfe) == 0)
            return true;
        for(MemberDefinition member = cDef.getFirstMember(); member != null; member = member.getNextMember())
            if(member.isMethod() && !member.isConstructor() && !member.isInitializer() && member.isPublic() && writeMethod.equals(member.getName().toString())) {
                Type methodType = member.getType();
                Type args[] = methodType.getArgumentTypes();
                if(args.length == 1 && !args[0].equals(type))
                    throw new ModelerException("rmimodeler.property.setter.method.cant.be.overloaded", new Object[] {
                        writeMethod, type.toString(), cDef.getName().toString()
                    });
            }

        try {
            ClassDeclaration superClass = cDef.getSuperClass();
            if(superClass != null) {
                ClassDefinition superDef = superClass.getClassDefinition(env);
                verifyProperty(superDef, type, writeMethod);
            }
            ClassDeclaration superDefs[] = cDef.getInterfaces();
            for(int i = 0; i < superDefs.length; i++) {
                ClassDefinition superDef = superDefs[i].getClassDefinition(env);
                verifyProperty(superDef, type, writeMethod);
            }

        }
        catch(ClassNotFound classnotfound) {
            throw new ModelerException("rmimodeler.class.not.found", cDef.getName().toString());
        }
        return true;
    }

    private Type findType(ClassDefinition cDef, String readMethod) {
        Type type = null;
        for(MemberDefinition member = cDef.getFirstMember(); member != null; member = member.getNextMember())
            if(member.isMethod() && !member.isConstructor() && !member.isInitializer() && member.isPublic() && readMethod.equals(member.getName().toString())) {
                type = member.getType().getReturnType();
                return type;
            }

        try {
            ClassDeclaration superClass = cDef.getSuperClass();
            if(superClass != null) {
                ClassDefinition superDef = superClass.getClassDefinition(env);
                type = findType(superDef, readMethod);
                if(type != null)
                    return type;
            }
            ClassDeclaration superDefs[] = cDef.getInterfaces();
            for(int i = 0; i < superDefs.length; i++) {
                ClassDefinition superDef = superDefs[i].getClassDefinition(env);
                type = findType(superDef, readMethod);
                if(type != null)
                    return type;
            }

        }
        catch(ClassNotFound classnotfound) {
            throw new ModelerException("rmimodeler.class.not.found", cDef.getName().toString());
        }
        return null;
    }

    private static void log(com.sun.xml.rpc.processor.util.BatchEnvironment env, String msg) {
        if(env.verbose())
            System.out.println("[JavaBean: " + msg + "]");
    }
}
