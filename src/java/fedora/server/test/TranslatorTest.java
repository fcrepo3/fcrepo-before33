package fedora.server.test;

import fedora.server.storage.types.*;
import fedora.server.storage.*;
import fedora.server.storage.translation.*;
import fedora.server.errors.*;
import fedora.server.*;
import java.io.*;

/**
 * Tests the configured DOTranslator instance, deserializing, then
 * re-serializing and printing the bytes from the file whose name
 * is passed in.
 * <p></p>
 * Since DOTranslator is a Module, it's more appropriate to test it
 * by starting up the configured server instance.
 *
 * @author cwilper@cs.cornell.edu
 */
public class TranslatorTest {

    public static void main(String args[]) {
        FileInputStream in=null;
        try {
            if (args.length!=3) {
                throw new IOException("*Three* parameters needed, filename, format, and encoding.");
            }
            if (System.getProperty("fedora.home")==null) {
                throw new IOException("fedora.home property must be set.  Try using -Dfedora.home=path/to/fedorahome");
            }
            in=new FileInputStream(new File(args[0]));
            Server server;
            server=Server.getInstance(new File(System.getProperty("fedora.home")));
            DOTranslator trans=(DOTranslator) server.getModule("fedora.server.storage.translation.DOTranslator");
            if (trans==null) {
                throw new IOException("DOTranslator module not found via getModule");
            }
            DigitalObject obj=new BasicDigitalObject();
            System.out.println("Deserializing...");
            trans.deserialize(in, obj, args[1], args[2]);
            System.out.println("Done.");
            ByteArrayOutputStream outStream=new ByteArrayOutputStream();
            System.out.println("Re-serializing...");
            trans.serialize(obj, outStream, args[1], args[2]);
            System.out.println("Done. Here it is:");
            System.out.println(outStream.toString(args[2]));
            server.shutdown();
        } catch (Exception e) {
            System.out.println("Error: " + e.getClass().getName() + " " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }
}