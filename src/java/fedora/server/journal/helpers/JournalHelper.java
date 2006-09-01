/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2006 by 
 * The Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */

package fedora.server.journal.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import fedora.server.journal.JournalConstants;
import fedora.server.journal.JournalException;
import fedora.server.utilities.StreamUtility;

/**
 * 
 * <p>
 * <b>Title:</b> JournalHelper.java
 * </p>
 * <p>
 * <b>Description:</b> A collection of utility methods for use in the Journal
 * classes.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */
public class JournalHelper implements JournalConstants {

    private JournalHelper() {
        // no need to instantiate this class - all methods are static.
    }

    /**
     * Copy an input stream to a temporary file, so we can hand an input stream
     * to the delegate and have another input stream for the journal.
     */
    public static File copyToTempFile(InputStream serialization)
            throws IOException, FileNotFoundException {
        File tempFile = File.createTempFile("fedora-journal-temp", ".xml");
        tempFile.deleteOnExit();
        StreamUtility.pipeStream(serialization, new FileOutputStream(tempFile),
                4096);
        return tempFile;
    }

    /**
     * Capture the full stack trace of an Exception, and return it in a String.
     */
    public static String captureStackTrace(Throwable e) {
        StringWriter buffer = new StringWriter();
        e.printStackTrace(new PrintWriter(buffer));
        return buffer.toString();
    }

    /**
     * Look in the system parameters and create an instance of the named class.
     * 
     * @param parameterName
     *            The name of the system parameter that contains the classname
     * @param argClasses
     *            What types of arguments are required by the constructor?
     * @param args
     *            Arguments to provide to the instance constructor.
     * @param parameters
     *            The system parameters
     * @return the new instance created
     */
    public static Object createInstanceAccordingToParameter(
            String parameterName, Class[] argClasses, Object[] args,
            Map parameters) throws JournalException {
        String className = (String) parameters.get(parameterName);
        if (className == null) {
            throw new JournalException("No parameter '" + parameterName + "'");
        }

        try {
            Class clazz = Class.forName(className);
            Constructor constructor = clazz.getConstructor(argClasses);
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new JournalException(e);
        }
    }

    /**
     * Format a date for the journal or the log.
     */
    public static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
        return formatter.format(date);
    }

    /**
     * Parse a date from the journal.
     */
    public static Date parseDate(String date) throws JournalException {
        try {
            SimpleDateFormat parser = new SimpleDateFormat(TIMESTAMP_FORMAT);
            return parser.parse(date);
        } catch (ParseException e) {
            throw new JournalException(e);
        }
    }
}
