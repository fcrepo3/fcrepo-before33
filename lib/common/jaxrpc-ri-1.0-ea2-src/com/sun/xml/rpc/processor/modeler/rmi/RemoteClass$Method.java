// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   RemoteClass.java

package com.sun.xml.rpc.processor.modeler.rmi;

import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.io.*;
import java.security.*;
import java.util.Vector;
import sun.tools.java.*;

// Referenced classes of package com.sun.xml.rpc.processor.modeler.rmi:
//            RemoteClass

public class RemoteClass$Method
    implements Cloneable {

    private MemberDefinition memberDef;
    private long methodHash;
    private ClassDeclaration exceptions[];
    private final RemoteClass this$0; /* synthetic field */

    public MemberDefinition getMemberDefinition() {
        return memberDef;
    }

    public Identifier getName() {
        return memberDef.getName();
    }

    public Type getType() {
        return memberDef.getType();
    }

    public ClassDeclaration[] getExceptions() {
        return (ClassDeclaration[])exceptions.clone();
    }

    public long getMethodHash() {
        return methodHash;
    }

    public String toString() {
        return memberDef.toString();
    }

    public String getOperationString() {
        return memberDef.toString();
    }

    public String getNameAndDescriptor() {
        return memberDef.getName().toString() + memberDef.getType().getTypeSignature();
    }

    RemoteClass$Method(RemoteClass this$0, MemberDefinition memberDef) {
        this.this$0 = this$0;
        this.memberDef = memberDef;
        exceptions = memberDef.getExceptions(RemoteClass.access$100(this$0));
        methodHash = computeMethodHash();
    }

    protected Object clone() {
        try {
            return super.clone();
        }
        catch(CloneNotSupportedException clonenotsupportedexception) {
            throw new Error("clone failed");
        }
    }

    private RemoteClass$Method mergeWith(RemoteClass$Method other) {
        if(!getName().equals(other.getName()) || !getType().equals(other.getType()))
            throw new Error("attempt to merge method \"" + other.getNameAndDescriptor() + "\" with \"" + getNameAndDescriptor());
        Vector legalExceptions = new Vector();
        try {
            collectCompatibleExceptions(other.exceptions, exceptions, legalExceptions);
            collectCompatibleExceptions(exceptions, other.exceptions, legalExceptions);
        }
        catch(ClassNotFound e) {
            throw new ModelerException("rmimodeler.nestedRmiModelerError", new LocalizableExceptionAdapter(e));
        }
        RemoteClass$Method merged = (RemoteClass$Method)clone();
        merged.exceptions = new ClassDeclaration[legalExceptions.size()];
        legalExceptions.copyInto(merged.exceptions);
        return merged;
    }

    private void collectCompatibleExceptions(ClassDeclaration from[], ClassDeclaration with[], Vector list) throws ClassNotFound {
        for(int i = 0; i < from.length; i++) {
            ClassDefinition exceptionDef = from[i].getClassDefinition(RemoteClass.access$100(this$0));
            if(!list.contains(from[i])) {
                for(int j = 0; j < with.length; j++) {
                    if(!exceptionDef.subClassOf(RemoteClass.access$100(this$0), with[j]))
                        continue;
                    list.addElement(from[i]);
                    break;
                }

            }
        }

    }

    private long computeMethodHash() {
        long hash = 0L;
        ByteArrayOutputStream sink = new ByteArrayOutputStream(512);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            DataOutputStream out = new DataOutputStream(new DigestOutputStream(sink, md));
            String methodString = getNameAndDescriptor();
            if(RemoteClass.access$100(this$0).verbose())
                System.out.println("[string used for method hash: \"" + methodString + "\"]");
            out.writeUTF(methodString);
            out.flush();
            byte hashArray[] = md.digest();
            for(int i = 0; i < Math.min(8, hashArray.length); i++)
                hash += (long)(hashArray[i] & 0xff) << i * 8;

        }
        catch(IOException e) {
            throw new Error("unexpected exception computing intetrface hash: " + e);
        }
        catch(NoSuchAlgorithmException e) {
            throw new Error("unexpected exception computing intetrface hash: " + e);
        }
        return hash;
    }

    static RemoteClass$Method access$000(RemoteClass$Method x0, RemoteClass$Method x1) {
        return x0.mergeWith(x1);
    }
}
