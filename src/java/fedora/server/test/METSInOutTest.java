package fedora.server.test;

import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.METSDOSerializer;
import fedora.server.storage.METSDODeserializer;
import fedora.server.validation.DOValidatorImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Tests the METS deserializer and serializer by opening a METS
 * file (supplied at command-line), deserializing it, re-serializing it, 
 * and sending it to STDOUT.
 *
 * @author cwilper@cs.cornell.edu
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
            METSDODeserializer deser=new METSDODeserializer("UTF-8", false, METSDODeserializer.QUERY_NEVER);
            DigitalObject obj=new BasicDigitalObject();
            System.out.println("Deserializing...");
            deser.deserialize(in, obj, "UTF-8");
            System.out.println("Done.");
            METSDOSerializer ser=new METSDOSerializer("UTF-8");
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            System.out.println("Re-serializing...");
            ser.serialize(obj, out, "UTF-8");
            System.out.println("Done.");
            if (args.length>1) {
                ByteArrayInputStream newIn=new ByteArrayInputStream(out.toByteArray());
                DOValidatorImpl v=new DOValidatorImpl(null, null, "dist/server/xsd/fedora-mets.xsd",
                        "dist/server/schematron/preprocessor.xslt",
                        "dist/server/schematron/fedoraRules.xml",
                        "dist/server/schematron/fedoraValidator.xslt", null);
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
        }
    }
}