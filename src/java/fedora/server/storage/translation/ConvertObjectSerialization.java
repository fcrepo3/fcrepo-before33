/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.translation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DigitalObject;

/**
 * Utility class to convert objects from one serialization format to another.
 * 
 * @author Edwin Shin
 * @since 3.0
 * @version $Id$
 */
public class ConvertObjectSerialization {

    private DODeserializer m_deserializer;

    private DOSerializer m_serializer;

    private String m_encoding = "utf-8";

    public ConvertObjectSerialization(DODeserializer deserializer,
                                      DOSerializer serializer) {
        m_deserializer = deserializer;
        m_serializer = serializer;
    }

    private boolean convert(InputStream source, OutputStream destination) {
        DigitalObject obj = new BasicDigitalObject();
        try {
            m_deserializer
                    .deserialize(source,
                                 obj,
                                 m_encoding,
                                 DOTranslationUtility.DESERIALIZE_INSTANCE);            
            setObjectDefaults(obj);
            
            m_serializer
                    .serialize(obj,
                               destination,
                               m_encoding,
                               DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * Convert files from one format to the other.
     * Hidden directories (directories starting with a ".") are skipped.
     * 
     * @param source
     * @param destination
     * @return
     */
    public boolean convert(File source, File destination) {
        boolean result = true;
        if (source.isDirectory()) {
            if (source.getName().startsWith(".")) {
                // skip "hidden" directories
                return result;
            }
            if (destination.exists()) {
                result = result && destination.isDirectory();
            } else {
                result = result && destination.mkdirs();
            }
            File[] children = source.listFiles();
            for (File element : children) {
                result =
                        result
                                && convert(new File(source, element.getName()),
                                        new File(destination, element.getName()));
            }
            return result;
        } else {
            try {
                if (!source.getName().endsWith(".xml")) {
                    return result;
                }
                System.out.println("Converting " + source.getAbsolutePath());
                InputStream in = new FileInputStream(source);
                if (!destination.getParentFile().exists()) {
                    destination.getParentFile().mkdirs();
                }
                OutputStream out = new FileOutputStream(destination);
                result = result && convert(in, out);
                out.close();
                in.close();
                return result;
            } catch (IOException e) {
                return false;
            }
        }
    }
    
    private void setObjectDefaults(DigitalObject obj) {
        if (obj.getCreateDate() == null) obj.setCreateDate(new Date());
        if (obj.getLastModDate() == null) obj.setLastModDate(new Date());

        Iterator<String> dsIds = obj.datastreamIdIterator();
        while (dsIds.hasNext()) {
            String dsid = dsIds.next();
            List<Datastream> dsList = obj.datastreams(dsid);
            for (Datastream ds : dsList) {
                if (ds.DSCreateDT == null) {
                    ds.DSCreateDT = new Date();
                }
            }
        }
    }
    
    public static void main(String[] args) {
        String source = "src/demo-objects/foxml";
        String destination = "src/demo-objects/atom";
        
        System.setProperty("fedoraServerHost", "localhost");
        System.setProperty("fedoraServerPort", "8080");
        
        ConvertObjectSerialization converter =
                new ConvertObjectSerialization(new FOXML1_1DODeserializer(),
                                               new AtomDOSerializer());
        converter.convert(new File(source),
                          new File(destination));
    }
}
