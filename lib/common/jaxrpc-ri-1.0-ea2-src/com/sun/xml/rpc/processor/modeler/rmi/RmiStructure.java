// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   RmiStructure.java

package com.sun.xml.rpc.processor.modeler.rmi;

import com.sun.xml.rpc.processor.modeler.ModelerException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import sun.tools.java.AmbiguousMember;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;
import sun.tools.javac.BatchEnvironment;

// Referenced classes of package com.sun.xml.rpc.processor.modeler.rmi:
//            MemberInfo, RmiConstants

public class RmiStructure
    implements RmiConstants {

    private com.sun.xml.rpc.processor.util.BatchEnvironment env;
    private ClassDefinition implClassDef;
    private HashMap members;
    private ClassDefinition defRemote;

    private static RmiStructure forClass(com.sun.xml.rpc.processor.util.BatchEnvironment env, ClassDefinition implClassDef) {
        RmiStructure sc = new RmiStructure(env, implClassDef);
        sc.initialize();
        return sc;
    }

    public static Map modelTypeSOAP(com.sun.xml.rpc.processor.util.BatchEnvironment env, Type type) {
        ClassDefinition classDef;
        try {
            ClassDeclaration cDec = env.getClassDeclaration(type.getClassName());
            classDef = cDec.getClassDefinition(env);
        }
        catch(ClassNotFound classnotfound) {
            throw new ModelerException("rmimodeler.class.not.found", type.getClassName().toString());
        }
        RmiStructure rt = forClass(env, classDef);
        if(rt == null)
            return null;
        else
            return rt.getMembers();
    }

    private HashMap getMembers() {
        return (HashMap)members.clone();
    }

    private RmiStructure(com.sun.xml.rpc.processor.util.BatchEnvironment env, ClassDefinition implClassDef) {
        this.env = env;
        this.implClassDef = implClassDef;
    }

    private void initialize() {
        log(env, "looking for public members of class: " + implClassDef.getName());
        try {
            defRemote = env.getClassDeclaration(RmiConstants.idRemote).getClassDefinition(env);
            if(implClassDef.matchAnonConstructor(env, implClassDef.getName().getQualifier(), new Type[0]) == null)
                throw new ModelerException("rmimodeler.no.empty.constructor", implClassDef.getName().toString());
        }
        catch(ClassNotFound e) {
            throw new ModelerException("rmimodeler.class.not.found", e.name.toString());
        }
        catch(AmbiguousMember e) {
            throw new ModelerException("rmimodeler.unexplained.error", e.getMessage());
        }
        members = new HashMap();
        Vector interfacesImplemented = new Vector();
        interfacesImplemented.addElement(implClassDef);
        try {
            if(defRemote.implementedBy(env, implClassDef.getClassDeclaration())) {
                log(env, "remote interface implemented by: " + implClassDef.getName());
                throw new ModelerException("rmimodeler.type.cannot.implement.remote", implClassDef.toString());
            }
        }
        catch(ClassNotFound e) {
            throw new ModelerException("rmimodeler.class.not.found", e.name.toString());
        }
        if(!collectMembers(implClassDef, members))
            members = new HashMap();
    }

    private boolean collectMembers(ClassDefinition interfaceDef, HashMap map) {
        for(MemberDefinition member = interfaceDef.getFirstMember(); member != null; member = member.getNextMember())
            try {
                if(member.isVariable() && (!member.isFinal() || !member.isStatic()) && (member.isPublic() && !defRemote.implementedBy(env, member.getClassDefinition().getClassDeclaration()) && !member.isTransient() && map.get(member.getName().toString()) == null)) {
                    log(env, "found public member: " + member.getName().toString());
                    map.put(member.getName().toString(), new MemberInfo(member.getName().toString(), member.getType(), true));
                }
            }
            catch(ClassNotFound classnotfound) { }

        try {
            ClassDeclaration superDefs[] = interfaceDef.getInterfaces();
            ClassDeclaration superClass = interfaceDef.getSuperClass();
            if(superClass != null) {
                ClassDefinition superDef = superClass.getClassDefinition(env);
                return collectMembers(superDef, map);
            }
        }
        catch(ClassNotFound classnotfound1) {
            throw new ModelerException("rmimodeler.class.not.found", interfaceDef.getName().toString());
        }
        return true;
    }

    private static void log(com.sun.xml.rpc.processor.util.BatchEnvironment env, String msg) {
        if(env.verbose())
            System.out.println("[RmiStructure: " + msg + "]");
    }
}
