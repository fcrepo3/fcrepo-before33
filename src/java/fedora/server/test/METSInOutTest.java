package fedora.server.test;

import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.METSDOSerializer;
import fedora.server.storage.METSDODeserializer;

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
            if (args.length!=1) {
                throw new IOException("*One* parameter needed.");
            }
            in=new FileInputStream(new File(args[0]));
        } catch (IOException ioe) {
                System.out.println("Error: " + ioe.getMessage());
                System.out.println("Give the path to an existing METS file.");
                System.exit(0);
        }
        try {
            METSDODeserializer deser=new METSDODeserializer();
            DigitalObject obj=new BasicDigitalObject();
            System.out.println("Deserializing...");
            deser.deserialize(in, obj);
            System.out.println("Done.");
            METSDOSerializer ser=new METSDOSerializer("UTF-8");
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            System.out.println("Re-serializing...");
            ser.serialize(obj, out);
            System.out.println("Done. Here it is:");
            System.out.println(out.toString("UTF-8"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}