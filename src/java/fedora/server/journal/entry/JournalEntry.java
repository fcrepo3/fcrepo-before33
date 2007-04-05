/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.journal.entry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import fedora.server.journal.JournalException;
import fedora.server.journal.helpers.JournalHelper;
import fedora.server.journal.managementmethods.ManagementMethod;
import fedora.server.storage.types.DSBindingMap;

/**
 * 
 * <p>
 * <b>Title:</b> JournalEntry.java
 * </p>
 * <p>
 * <b>Description:</b> An abstract base class for the JournalEntry classes. At
 * this level, a JournalEntry is a method name, a method adapter, and a map of
 * arguments.
 * </p>
 * <p>
 * NOTE: when finished with the JournalEntry, call close(). This will release
 * any temporary files associated with the entry.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public abstract class JournalEntry {
    private boolean open = true;
    
    private final Map arguments = new LinkedHashMap();
    private final String methodName;
    private final ManagementMethod method;
    private final JournalEntryContext context;

    protected JournalEntry(String methodName, JournalEntryContext context) {
        this.methodName = methodName;
        this.context = context;
        this.method = ManagementMethod.getInstance(methodName, this);
    }

    public JournalEntryContext getContext() {
        checkOpen();
        return context;
    }

    public ManagementMethod getMethod() {
        checkOpen();
        return method;
    }
    
    public String getMethodName() {
        checkOpen();
        return methodName;
    }

    public Map getArgumentsMap() {
        checkOpen();
        return new LinkedHashMap(arguments);
    }

    public void addArgument(String key, boolean value) {
        checkOpen();
        addArgument(key, Boolean.valueOf(value));
    }

    public void addArgument(String key, int value) {
        checkOpen();
        addArgument(key, new Integer(value));
    }

    public void addArgument(String key, Object value) {
        checkOpen();
        arguments.put(key, value);
    }

    /**
     * If handed an InputStream as an argument, copy it to a temp file and
     * store that File in the arguments map instead.  If the InputStream is
     * null, store null in the arguments map.
     */
    public void addArgument(String key, InputStream stream)
            throws JournalException {
        checkOpen();
        if (stream == null) {
            arguments.put(key, null);
        } else {
            try {
                File tempFile = JournalHelper.copyToTempFile(stream);
                arguments.put(key, tempFile);
            } catch (IOException e) {
                throw new JournalException(e);
            }
        }
    }

    // convenience method for setting values into the Context recovery space.
    public void setRecoveryValue(String attribute, String value) {
        checkOpen();
        this.context.setRecoveryValue(attribute, value);
    }

    // convenience method for setting values into the Context recovery space.
    public void setRecoveryValues(String attribute, String[] values) {
        checkOpen();
        this.context.setRecoveryValues(attribute, values);
    }

    //
    // Convenience methods for pulling arguments out of the argument map.
    //
    public int getIntegerArgument(String name) {
        checkOpen();
        return ((Integer) arguments.get(name)).intValue();
    }

    public boolean getBooleanArgument(String name) {
        checkOpen();
        return ((Boolean) arguments.get(name)).booleanValue();
    }

    public String getStringArgument(String name) {
        checkOpen();
        return (String) arguments.get(name);
    }

    public Date getDateArgument(String name) {
        checkOpen();
        return (Date) arguments.get(name);
    }

    public String[] getStringArrayArgument(String name) {
        checkOpen();
        return (String[]) arguments.get(name);
    }

    /**
     * If they ask for an InputStream argument, get the File from the arguments
     * map and create an InputStream on that file.  If the value from the
     * map is null, return null.
     */
    public InputStream getStreamArgument(String name) throws JournalException {
        checkOpen();
        File file = (File) arguments.get(name);
        if (file == null) {
            return null;
        } else {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new JournalException(e);
            }
        }
    }

    public DSBindingMap getDSBindingMapArgument(String name) {
        checkOpen();
        return (DSBindingMap) arguments.get(name);
    }

    /**
     * This should be called when usage of the object is complete, to clean up
     * any temporary files that were created for the journal entry to use.
     */
    public void close() {
        checkOpen();
        
        open = false;
        
        for (Iterator args = arguments.values().iterator(); args.hasNext();) {
            Object arg = args.next();
            if (arg instanceof File) {
                File file = (File) arg;
                if (JournalHelper.isTempFile(file)) {
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }
    }

    /**
     * Every non-private method should call this first, to prevent accessing the
     * object after it has been closed.
     * 
     * @throws IllegalStateException if the open flag has been reset.
     */
    private void checkOpen() throws IllegalStateException {
        if (!open) {
            throw new IllegalStateException("JournalEntry must not be " +
                    "accessed after close() has been called");
        }
    }
}
