// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ExceptionModeler.java

package com.sun.xml.rpc.processor.modeler.rmi;

import com.sun.xml.rpc.processor.config.TypeMappingRegistryInfo;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.*;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.io.PrintStream;
import javax.xml.rpc.namespace.QName;
import sun.tools.java.*;

// Referenced classes of package com.sun.xml.rpc.processor.modeler.rmi:
//            RmiConstants, RmiTypeModeler

public class ExceptionModeler
    implements RmiConstants {

    public ExceptionModeler() {
    }

    public static Fault modelException(BatchEnvironment env, TypeMappingRegistryInfo typeMappingRegistry, String typeUri, String wsdlUri, ClassDeclaration exceptionCDec) {
        try {
            ClassDefinition classDef = exceptionCDec.getClassDefinition(env);
            int constructorCnt = 0;
            for(MemberDefinition member = classDef.getFirstMember(); member != null; member = member.getNextMember())
                if(member.isPublic() && !member.isStatic() && member.isConstructor())
                    constructorCnt++;

            for(MemberDefinition member = classDef.getFirstMember(); member != null && constructorCnt == 1; member = member.getNextMember())
                if(member.isMethod() && member.isPublic() && !member.isStatic() && !member.isConstructor() && !member.isInitializer() && member.getName().toString().startsWith("get") && member.getArguments() == null) {
                    Type memberType = member.getType().getReturnType();
                    String readMethod = member.getName().toString();
                    if(classDef.matchAnonConstructor(env, classDef.getName().getQualifier(), new Type[] {
    memberType
}) != null) {
                        Fault fault = new Fault(Names.stripQualifier(exceptionCDec.getName().toString()));
                        String propertyName = readMethod.substring(3);
                        com.sun.xml.rpc.processor.model.soap.SOAPType propertyType = RmiTypeModeler.modelTypeSOAP(env, typeMappingRegistry, typeUri, memberType);
                        QName faultQName = new QName(wsdlUri, fault.getName());
                        Block faultBlock = new Block(faultQName, propertyType);
                        fault.setBlock(faultBlock);
                        JavaException javaException = new JavaException(exceptionCDec.getName().toString(), propertyName, propertyType.getJavaType(), true);
                        fault.setJavaException(javaException);
                        return fault;
                    }
                }

        }
        catch(ClassNotFound classnotfound) {
            throw new ModelerException("rmimodeler.class.not.found", exceptionCDec.getName().toString());
        }
        catch(Exception e) {
            throw new ModelerException(new LocalizableExceptionAdapter(e));
        }
        throw new ModelerException("rmimodeler.invalid.exception", exceptionCDec.getName().toString());
    }

    private static void log(BatchEnvironment env, String msg) {
        if(env.verbose())
            System.out.println("[ExceptionIntrospector: " + msg + "]");
    }
}
