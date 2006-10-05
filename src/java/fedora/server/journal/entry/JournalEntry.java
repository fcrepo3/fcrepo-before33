package fedora.server.journal.entry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
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
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public abstract class JournalEntry {
    protected final Map arguments = new LinkedHashMap();
    protected final String methodName;
    protected final ManagementMethod method;
    protected final JournalEntryContext context;

    protected JournalEntry(String methodName, JournalEntryContext context) {
        this.methodName = methodName;
        this.context = context;
        this.method = ManagementMethod.getInstance(methodName, this);
    }

    public JournalEntryContext getContext() {
        return context;
    }

    public String getMethodName() {
        return methodName;
    }

    public Map getArgumentsMap() {
        return new LinkedHashMap(arguments);
    }

    public void addArgument(String key, boolean value) {
        addArgument(key, Boolean.valueOf(value));
    }

    public void addArgument(String key, int value) {
        addArgument(key, new Integer(value));
    }

    public void addArgument(String key, Object value) {
        arguments.put(key, value);
    }

    /**
     * If handed an InputStream as an argument, copy it to a temp file and store
     * that File in the arguments map instead.
     */
    public void addArgument(String key, InputStream stream)
            throws JournalException {
        try {
            File tempFile = JournalHelper.copyToTempFile(stream);
            arguments.put(key, tempFile);
        } catch (IOException e) {
            throw new JournalException(e);
        }
    }

    // convenience method for setting values into the Context recovery space.
    public void setRecoveryValue(String attribute, String value) {
        this.context.setRecoveryValue(attribute, value);
    }

    // convenience method for setting values into the Context recovery space.
    public void setRecoveryValues(String attribute, String[] values) {
        this.context.setRecoveryValues(attribute, values);
    }

    //
    // Convenience methods for pulling arguments out of the argument map.
    //
    public int getIntegerArgument(String name) {
        return ((Integer) arguments.get(name)).intValue();
    }

    public boolean getBooleanArgument(String name) {
        return ((Boolean) arguments.get(name)).booleanValue();
    }

    public String getStringArgument(String name) {
        return (String) arguments.get(name);
    }

    public Date getDateArgument(String name) {
        return (Date) arguments.get(name);
    }

    public String[] getStringArrayArgument(String name) {
        return (String[]) arguments.get(name);
    }

    /**
     * If they ask for an InputStream argument, get the File from the arguments
     * map and create an InputStream on that file.
     */
    public InputStream getStreamArgument(String name) throws JournalException {
        try {
            return new FileInputStream((File) arguments.get(name));
        } catch (FileNotFoundException e) {
            throw new JournalException(e);
        }
    }

    public DSBindingMap getDSBindingMapArgument(String name) {
        return (DSBindingMap) arguments.get(name);
    }

}
