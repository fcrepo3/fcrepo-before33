package fedora.server.storage.translation;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.types.DigitalObject;

/**
 *
 * <p><b>Title:</b> DOTranslatorModule.java</p>
 * <p><b>Description:</b> </p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class DOTranslatorModule
        extends Module
        implements DOTranslator {

    private DOTranslatorImpl m_wrappedTranslator;

    public DOTranslatorModule(Map params, Server server, String role)
            throws ModuleInitializationException {
        super(params, server, role);
    }

    public void initModule()
            throws ModuleInitializationException {
        HashMap serMap=new HashMap();
        HashMap deserMap=new HashMap();
        Iterator nameIter=parameterNames();
        while (nameIter.hasNext()) {
            String paramName=(String) nameIter.next();
            if (paramName.startsWith("serializer_")) {
                String serName=paramName.substring(11);
                try {
                    DOSerializer ser=(DOSerializer) Class.forName(
                            getParameter(paramName)).newInstance();
                    serMap.put(serName, ser);
                } catch (Exception e) {
                    throw new ModuleInitializationException(
                            "Can't instantiate serializer class for format="
                            + serName + " : " +
                            e.getClass().getName() + ": " + e.getMessage(),
                            getRole());
                }
            } else if (paramName.startsWith("deserializer_")) {
                String deserName=paramName.substring(13);
                try {
                    DODeserializer deser=(DODeserializer) Class.forName(
                            getParameter(paramName)).newInstance();
                    deserMap.put(deserName, deser);
                } catch (Exception e) {
                    throw new ModuleInitializationException(
                            "Can't instantiate deserializer class for format="
                            + deserName + " : " +
                            e.getClass().getName() + ": " + e.getMessage(),
                            getRole());
                }
            }
        }
        m_wrappedTranslator=new DOTranslatorImpl(serMap, deserMap, this);
    }

    public void deserialize(InputStream in, DigitalObject out,
            String format, String encoding, int transContext)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException {
        m_wrappedTranslator.deserialize(in, out, format, encoding, transContext); 
    }

    public void serialize(DigitalObject in, OutputStream out,
			String format, String encoding, int transContext)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException {
		m_wrappedTranslator.serialize(in, out, format, encoding, transContext);
    }

}