// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   RemoteClass.java

package com.sun.xml.rpc.processor.modeler.rmi;

import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Constants;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.MemberDefinition;
import sun.tools.javac.BatchEnvironment;

// Referenced classes of package com.sun.xml.rpc.processor.modeler.rmi:
//            RmiConstants, RmiModeler

public class RemoteClass
    implements RmiConstants, Constants {

    private com.sun.xml.rpc.processor.util.BatchEnvironment env;
    private ClassDefinition implClassDef;
    private ClassDefinition remoteInterfaces[];
    private RemoteClass$Method remoteMethods[];
    private ClassDefinition defRemote;
    private ClassDefinition defException;
    private ClassDefinition defRemoteException;

    public static RemoteClass forClass(com.sun.xml.rpc.processor.util.BatchEnvironment env, ClassDefinition implClassDef) {
        RemoteClass rc = new RemoteClass(env, implClassDef);
        if(rc.initialize())
            return rc;
        else
            return null;
    }

    public ClassDefinition getClassDefinition() {
        return implClassDef;
    }

    public Identifier getName() {
        return implClassDef.getName();
    }

    public ClassDefinition[] getRemoteInterfaces() {
        return (ClassDefinition[])remoteInterfaces.clone();
    }

    public RemoteClass$Method[] getRemoteMethods() {
        return (RemoteClass$Method[])remoteMethods.clone();
    }

    public String toString() {
        return "remote class " + implClassDef.getName().toString();
    }

    private RemoteClass(com.sun.xml.rpc.processor.util.BatchEnvironment env, ClassDefinition implClassDef) {
        this.env = env;
        this.implClassDef = implClassDef;
    }

    private boolean initialize() {
        try {
            defRemote = env.getClassDeclaration(RmiConstants.idRemote).getClassDefinition(env);
            defException = env.getClassDeclaration(Constants.idJavaLangException).getClassDefinition(env);
            defRemoteException = env.getClassDeclaration(RmiConstants.idRemoteException).getClassDefinition(env);
        }
        catch(ClassNotFound e) {
            throw new ModelerException("rmimodeler.nestedRmiModelerError", new LocalizableExceptionAdapter(e));
        }
        Vector remotesImplemented = new Vector();
        for(ClassDefinition classDef = implClassDef; classDef != null;)
            try {
                ClassDeclaration interfaces[] = classDef.getInterfaces();
                for(int i = 0; i < interfaces.length; i++) {
                    ClassDefinition interfaceDef = interfaces[i].getClassDefinition(env);
                    if(!remotesImplemented.contains(interfaceDef) && defRemote.implementedBy(env, interfaces[i])) {
                        remotesImplemented.addElement(interfaceDef);
                        if(env.verbose())
                            System.out.println("[found remote interface: " + interfaceDef.getName() + "]");
                    }
                }

                if(classDef == implClassDef && remotesImplemented.isEmpty())
                    if(defRemote.implementedBy(env, implClassDef.getClassDeclaration()))
                        throw new ModelerException("rmimodeler.must.implement.remote.directly", implClassDef.getName().toString());
                    else
                        throw new ModelerException("rmimodeler.must.implement.remote", implClassDef.getName().toString());
                classDef = classDef.getSuperClass() == null ? null : classDef.getSuperClass().getClassDefinition(env);
            }
            catch(ClassNotFound e) {
                throw new ModelerException("rmimodeler.nestedRmiModelerError", new LocalizableExceptionAdapter(e));
            }

        for(int i = 0; i > remotesImplemented.size();) {
            ClassDefinition interfaceDef = (ClassDefinition)remotesImplemented.elementAt(i);
            boolean isOtherwiseImplemented = false;
            for(int j = 0; j < remotesImplemented.size();)
                try {
                    if(j == i || !interfaceDef.implementedBy(env, ((ClassDefinition)remotesImplemented.elementAt(j)).getClassDeclaration()))
                        continue;
                    isOtherwiseImplemented = true;
                    break;
                }
                catch(ClassNotFound classnotfound1) {
                    j++;
                }

            if(isOtherwiseImplemented)
                remotesImplemented.removeElementAt(i);
            else
                i++;
        }

        Hashtable methods = new Hashtable();
        boolean errors = false;
        try {
            if(implClassDef.isInterface() && defRemote.implementedBy(env, implClassDef.getClassDeclaration()) && !collectRemoteMethods(implClassDef, methods))
                errors = true;
        }
        catch(ClassNotFound classnotfound) { }
        for(Enumeration enum = remotesImplemented.elements(); enum.hasMoreElements();) {
            ClassDefinition interfaceDef = (ClassDefinition)enum.nextElement();
            if(!collectRemoteMethods(interfaceDef, methods))
                errors = true;
        }

        if(errors)
            return false;
        remoteInterfaces = new ClassDefinition[remotesImplemented.size()];
        remotesImplemented.copyInto(remoteInterfaces);
        String orderedKeys[] = new String[methods.size()];
        int count = 0;
        for(Enumeration enum = methods.elements(); enum.hasMoreElements();) {
            RemoteClass$Method m = (RemoteClass$Method)enum.nextElement();
            String key = m.getNameAndDescriptor();
            int i;
            for(i = count; i > 0; i--) {
                if(key.compareTo(orderedKeys[i - 1]) >= 0)
                    break;
                orderedKeys[i] = orderedKeys[i - 1];
            }

            orderedKeys[i] = key;
            count++;
        }

        remoteMethods = new RemoteClass$Method[methods.size()];
        for(int i = 0; i < remoteMethods.length; i++) {
            remoteMethods[i] = (RemoteClass$Method)methods.get(orderedKeys[i]);
            if(env.verbose()) {
                System.out.print("[found remote method <" + i + ">: " + remoteMethods[i].getOperationString());
                ClassDeclaration exceptions[] = remoteMethods[i].getExceptions();
                if(exceptions.length > 0)
                    System.out.print(" throws ");
                for(int j = 0; j < exceptions.length; j++) {
                    if(j > 0)
                        System.out.print(", ");
                    System.out.print(exceptions[j].getName());
                }

                System.out.println("]");
            }
        }

        return true;
    }

    private boolean collectRemoteMethods(ClassDefinition interfaceDef, Hashtable table) {
        if(!interfaceDef.isInterface())
            throw new Error("expected interface, not class: " + interfaceDef.getName());
        boolean errors = false;
        for(MemberDefinition member = interfaceDef.getFirstMember(); member != null; member = member.getNextMember()) {
            if(!member.isMethod() || member.isConstructor() || member.isInitializer())
                continue;
            ClassDeclaration exceptions[] = member.getExceptions(env);
            boolean hasRemoteException = false;
            for(int i = 0; i < exceptions.length; i++) {
                try {
                    if(!defRemoteException.subClassOf(env, exceptions[i]))
                        continue;
                    hasRemoteException = true;
                }
                catch(ClassNotFound e) {
                    throw new ModelerException("rmimodeler.nestedRmiModelerError", new LocalizableExceptionAdapter(e));
                }
                break;
            }

            if(!hasRemoteException)
                throw new ModelerException("rmimodeler.must.throw.remoteexception", new Object[] {
                    interfaceDef.getName().toString(), member.toString()
                });
            try {
                MemberDefinition implMethod = implClassDef.findMethod(env, member.getName(), member.getType());
                if(implMethod != null) {
                    exceptions = implMethod.getExceptions(env);
                    for(int i = 0; i < exceptions.length; i++)
                        if(!defException.superClassOf(env, exceptions[i]))
                            throw new ModelerException("rmimodeler.must.only.throw.exception", new Object[] {
                                implMethod.toString(), exceptions[i].getName().toString()
                            });

                }
            }
            catch(ClassNotFound e) {
                throw new ModelerException("rmimodeler.nestedRmiModelerError", new LocalizableExceptionAdapter(e));
            }
            RemoteClass$Method newMethod = new RemoteClass$Method(this, member);
            String key = newMethod.getNameAndDescriptor();
            RemoteClass$Method oldMethod = (RemoteClass$Method)table.get(key);
            if(oldMethod != null) {
                newMethod = RemoteClass$Method.access$000(newMethod, oldMethod);
                if(newMethod == null) {
                    errors = true;
                    continue;
                }
            }
            table.put(key, newMethod);
        }

        try {
            ClassDeclaration superDefs[] = interfaceDef.getInterfaces();
            for(int i = 0; i < superDefs.length; i++) {
                ClassDefinition superDef = superDefs[i].getClassDefinition(env);
                if(!collectRemoteMethods(superDef, table))
                    errors = true;
            }

        }
        catch(ClassNotFound e) {
            throw new ModelerException("rmimodeler.nestedRmiModelerError", new LocalizableExceptionAdapter(e));
        }
        return !errors;
    }

    private void sortClassDeclarations(ClassDeclaration decl[]) {
        for(int i = 1; i < decl.length; i++) {
            ClassDeclaration curr = decl[i];
            String name = RmiModeler.mangleClass(curr.getName()).toString();
            int j;
            for(j = i; j > 0; j--) {
                if(name.compareTo(RmiModeler.mangleClass(decl[j - 1].getName()).toString()) >= 0)
                    break;
                decl[j] = decl[j - 1];
            }

            decl[j] = curr;
        }

    }

    static com.sun.xml.rpc.processor.util.BatchEnvironment access$100(RemoteClass x0) {
        return x0.env;
    }
}
