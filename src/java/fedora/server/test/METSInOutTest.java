package fedora.server.test;

import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.translation.DOTranslatorImpl;
import fedora.server.storage.translation.METSLikeDODeserializer;
import fedora.server.storage.translation.METSLikeDOSerializer;
import fedora.server.validation.DOValidatorImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * <p><b>Title:</b> METSInOutTest.java</p>
 * <p><b>Description:</b> Tests the METS deserializer and serializer by opening
 * a METS file (supplied at command-line), deserializing it, re-serializing it,
 * and sending it to STDOUT.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version 1.0
 */
public class METSInOutTest {

    public static void main(String args[]) {
        FileInputStream in=null;
        try {
            if (args.length<1) {
                throw new IOException("At least one parameter needed.");
            }
            in=new FileInputStream(new File(args[0]));
        } catch (IOException ioe) {
                System.out.println("Error: " + ioe.getMessage());
                System.out.println("Give the path to an existing METS file, and optionally, the level of validation to perform on the re-serialized version.");
                System.exit(0);
        }
        try {
            METSLikeDODeserializer deser=new METSLikeDODeserializer();
            METSLikeDOSerializer ser=new METSLikeDOSerializer();
            HashMap desers=new HashMap();
            HashMap sers=new HashMap();
            desers.put("mets11fedora1", deser);
            sers.put("mets11fedora1", ser);
            DOTranslatorImpl trans=new DOTranslatorImpl(sers, desers, null);
            DigitalObject obj=new BasicDigitalObject();
            System.out.println("Deserializing...");
            trans.deserialize(in, obj, "mets11fedora1", "UTF-8");
            System.out.println("Done.");
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            System.out.println("Re-serializing...");
            trans.serialize(obj, out, "mets11fedora1", "UTF-8");
            System.out.println("Done.");
            if (args.length>1) {
                ByteArrayInputStream newIn=new ByteArrayInputStream(out.toByteArray());
                DOValidatorImpl v=new DOValidatorImpl(
                        null, "http://www.cs.cornell.edu/payette/mellon/fedora/mets-fedora-ext.xsd",
                        "dist/server/schematron/preprocessor.xslt",
                        "dist/server/schematron/fedoraRules.xml", null);
                if (args[1].equals("1")) {
                    v.validate(newIn, 1, "ingest");
                    System.out.println("Level 1 validation: PASSED!");
                } else {
                    if (args[1].equals("2")) {
                        v.validate(newIn, 2, "ingest");
                        System.out.println("Level 2 validation: PASSED!");
                    } else {
                        System.out.println("Unrecognized validation level, '" + args[1] + "'");
                    }
                }
            } else {
                System.out.println("Here it is:");
                System.out.println(out.toString("UTF-8"));
            }

        } catch (Exception e) {
            System.out.println("Error: (" + e.getClass().getName() + "):" + e.getMessage());
            e.printStackTrace();
        }
    }
}