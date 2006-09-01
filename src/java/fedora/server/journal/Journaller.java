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

package fedora.server.journal;

import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import fedora.server.Context;
import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;
import fedora.server.errors.ServerException;
import fedora.server.management.Management;
import fedora.server.management.ManagementDelegate;
import fedora.server.storage.types.DSBindingMap;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.Disseminator;
import fedora.server.storage.types.Property;

/**
 * <p>
 * <b>Title:</b> Journaller.java
 * </p>
 * <p>
 * <b>Description:</b> A Management module that decorates a ManagementDelegate
 * module with code that either creates a Journal or consumes a Journal,
 * depending on the startup parameters.</code>
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class Journaller extends Module implements Management, JournalConstants {
    private JournalWorker worker;

    private boolean inRecoveryMode;

    private ServerInterface serverInterface;

    public Journaller(Map moduleParameters, Server server, String role)
            throws ModuleInitializationException {
        super(moduleParameters, server, role);
    }

    /**
     * Augment the parameters with values obtained from System Properties, and
     * create the proper worker (JournalCreator or JournalConsumer) for the
     * current mode.
     */
    public void initModule() throws ModuleInitializationException {
        Map parameters = getParameters();
        copyPropertiesOverParameters(parameters);
        parseParameters(parameters);
        this.serverInterface = new ServerWrapper(getServer());

        if (this.inRecoveryMode) {
            worker = new JournalConsumer(parameters, getRole(),
                    this.serverInterface);
        } else {
            worker = new JournalCreator(parameters, getRole(),
                    this.serverInterface);
        }
        logInfo("Journal worker module is: " + worker.toString());
    }

    /**
     * Get the ManagementDelegate module and pass it to the worker.
     */
    public void postInitModule() throws ModuleInitializationException {
        ManagementDelegate delegate = this.serverInterface
                .getManagementDelegate();
        if (delegate == null) {
            throw new ModuleInitializationException(
                    "Can't get a ManagementDelegate from Server.getModule()",
                    getRole());
        }
        worker.setManagementDelegate(delegate);
    }

    /**
     * Tell the worker to shut down.
     */
    public void shutdownModule() throws ModuleShutdownException {
        worker.shutdown();
    }

    /**
     * Augment, and perhaps override, the server parameters, using any System
     * Property whose name begins with "fedora.journal.". So, for example, a
     * System Property of "fedora.journal.mode" will override a server parameter
     * of "mode".
     */
    private void copyPropertiesOverParameters(Map parameters) {
        Map properties = System.getProperties();
        for (Iterator keys = properties.keySet().iterator(); keys.hasNext();) {
            String key = (String) keys.next();
            if (key.startsWith(SYSTEM_PROPERTY_PREFIX)) {
                parameters.put(key.substring(SYSTEM_PROPERTY_PREFIX.length()),
                        properties.get(key));
            }
        }
    }

    /**
     * Check the parameters for required values and for acceptable values.
     * 
     * At this point, the only parameter we care about is "mode".
     */
    private void parseParameters(Map parameters)
            throws ModuleInitializationException {
        logInfo("Parameters: " + parameters);

        String mode = (String) parameters.get(PARAMETER_JOURNAL_MODE);
        if (mode == null) {
            this.inRecoveryMode = false;
        } else if (mode.equals(VALUE_JOURNAL_MODE_NORMAL)) {
            this.inRecoveryMode = false;
        } else if (mode.equals(VALUE_JOURNAL_MODE_RECOVER)) {
            this.inRecoveryMode = true;
        } else {
            throw new ModuleInitializationException("'"
                    + PARAMETER_JOURNAL_MODE + "' parameter must be '"
                    + VALUE_JOURNAL_MODE_NORMAL + "'(default) or '"
                    + VALUE_JOURNAL_MODE_RECOVER + "'", getRole());
        }
    }

    //
    // -------------------------------------------------------------------------
    // 
    // Delegate all of the "Management" methods to the worker.
    // 
    // -------------------------------------------------------------------------
    //

    /**
     * Delegate to the JournalWorker.
     */
    public String ingestObject(Context context, InputStream serialization,
            String logMessage, String format, String encoding, boolean newPid)
            throws ServerException {
        return worker.ingestObject(context, serialization, logMessage, format,
                encoding, newPid);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Date modifyObject(Context context, String pid, String state,
            String label, String logMessage) throws ServerException {
        return worker.modifyObject(context, pid, state, label, logMessage);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Property[] getObjectProperties(Context context, String pid)
            throws ServerException {
        return worker.getObjectProperties(context, pid);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public InputStream getObjectXML(Context context, String pid, String encoding)
            throws ServerException {
        return worker.getObjectXML(context, pid, encoding);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public InputStream exportObject(Context context, String pid, String format,
            String exportContext, String encoding) throws ServerException {
        return worker.exportObject(context, pid, format, exportContext,
                encoding);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Date purgeObject(Context context, String pid, String logMessage,
            boolean force) throws ServerException {
        return worker.purgeObject(context, pid, logMessage, force);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public String addDatastream(Context context, String pid, String dsID,
            String[] altIDs, String dsLabel, boolean versionable,
            String MIMEType, String formatURI, String location,
            String controlGroup, String dsState, String logMessage)
            throws ServerException {
        return worker.addDatastream(context, pid, dsID, altIDs, dsLabel,
                versionable, MIMEType, formatURI, location, controlGroup,
                dsState, logMessage);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Date modifyDatastreamByReference(Context context, String pid,
            String datastreamID, String[] altIDs, String dsLabel,
            boolean versionable, String mimeType, String formatURI,
            String dsLocation, String dsState, String logMessage, boolean force)
            throws ServerException {
        return worker.modifyDatastreamByReference(context, pid, datastreamID,
                altIDs, dsLabel, versionable, mimeType, formatURI, dsLocation,
                dsState, logMessage, force);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Date modifyDatastreamByValue(Context context, String pid,
            String datastreamID, String[] altIDs, String dsLabel,
            boolean versionable, String mimeType, String formatURI,
            InputStream dsContent, String dsState, String logMessage,
            boolean force) throws ServerException {
        return worker.modifyDatastreamByValue(context, pid, datastreamID,
                altIDs, dsLabel, versionable, mimeType, formatURI, dsContent,
                dsState, logMessage, force);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Date[] purgeDatastream(Context context, String pid,
            String datastreamID, Date endDT, String logMessage, boolean force)
            throws ServerException {
        return worker.purgeDatastream(context, pid, datastreamID, endDT,
                logMessage, force);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Datastream getDatastream(Context context, String pid,
            String datastreamID, Date asOfDateTime) throws ServerException {
        return worker.getDatastream(context, pid, datastreamID, asOfDateTime);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Datastream[] getDatastreams(Context context, String pid,
            Date asOfDateTime, String dsState) throws ServerException {
        return worker.getDatastreams(context, pid, asOfDateTime, dsState);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Datastream[] getDatastreamHistory(Context context, String pid,
            String datastreamID) throws ServerException {
        return worker.getDatastreamHistory(context, pid, datastreamID);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public String addDisseminator(Context context, String pid, String bDefPID,
            String bMechPid, String dissLabel, DSBindingMap bindingMap,
            String dissState, String logMessage) throws ServerException {
        return worker.addDisseminator(context, pid, bDefPID, bMechPid,
                dissLabel, bindingMap, dissState, logMessage);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Date modifyDisseminator(Context context, String pid,
            String disseminatorID, String bMechPid, String dissLabel,
            DSBindingMap bindingMap, String dissState, String logMessage,
            boolean force) throws ServerException {
        return worker.modifyDisseminator(context, pid, disseminatorID,
                bMechPid, dissLabel, bindingMap, dissState, logMessage, force);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Date[] purgeDisseminator(Context context, String pid,
            String disseminatorID, Date endDT, String logMessage)
            throws ServerException {
        return worker.purgeDisseminator(context, pid, disseminatorID, endDT,
                logMessage);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Disseminator getDisseminator(Context context, String pid,
            String disseminatorID, Date asOfDateTime) throws ServerException {
        return worker.getDisseminator(context, pid, disseminatorID,
                asOfDateTime);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Disseminator[] getDisseminators(Context context, String pid,
            Date asOfDateTime, String dissState) throws ServerException {
        return worker.getDisseminators(context, pid, asOfDateTime, dissState);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Disseminator[] getDisseminatorHistory(Context context, String pid,
            String disseminatorID) throws ServerException {
        return worker.getDisseminatorHistory(context, pid, disseminatorID);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public String putTempStream(Context context, InputStream in)
            throws ServerException {
        return worker.putTempStream(context, in);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public InputStream getTempStream(String id) throws ServerException {
        return worker.getTempStream(id);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Date setDatastreamState(Context context, String pid, String dsID,
            String dsState, String logMessage) throws ServerException {
        return worker.setDatastreamState(context, pid, dsID, dsState,
                logMessage);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public Date setDisseminatorState(Context context, String pid, String dsID,
            String dsState, String logMessage) throws ServerException {
        return worker.setDisseminatorState(context, pid, dsID, dsState,
                logMessage);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public String[] getNextPID(Context context, int numPIDs, String namespace)
            throws ServerException {
        return worker.getNextPID(context, numPIDs, namespace);
    }

    /**
     * Delegate to the JournalWorker.
     */
    public boolean adminPing(Context context) throws ServerException {
        return worker.adminPing(context);
    }

}
