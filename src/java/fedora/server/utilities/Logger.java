package fedora.server.utilities;

import java.io.File;

import fedora.server.Logging;
import fedora.server.Server;
import fedora.server.errors.InitializationException;

/*
* <p><b>Title: </b>Logger.java</p>
* <p><b>Description: </b>A logging utility to facilitate writing to the Fedora
* server logs.</p>
* <p>
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
* @author rlw@virginia.edu
* @version $Id$
*/
public class Logger implements Logging {


    /** Instance of the Fedora server. */
    private static Server s_server = null;


    /**
     * Constructor for Logger class.
     *
     * @throws InitializationException If unable to get an instance of the server.
     */
    public Logger() throws InitializationException {
        s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
    }

    /**
     * Returns an instance of the Fedora server.
     */
    private Server getServer() {
        return s_server;
    }

    /**
     * Logs a SEVERE message, indicating that the server is inoperable or
     * unable to start.
     *
     * @param message The message.
     */
    public final void logSevere(String message) {
        StringBuffer m=new StringBuffer();
        m.append(getClass().getName());
        m.append(": ");
        m.append(message);
        getServer().logSevere(m.toString());
    }

    public final boolean loggingSevere() {
        return getServer().loggingSevere();
    }

    /**
     * Logs a WARNING message, indicating that an undesired (but non-fatal)
     * condition occured.
     *
     * @param message The message.
     */
    public final void logWarning(String message) {
        try {
            StringBuffer m=new StringBuffer();
            m.append(getClass().getName());
            m.append(": ");
            m.append(message);
            getServer().logWarning(m.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final boolean loggingWarning() {
        return getServer().loggingWarning();
    }

    /**
     * Logs an INFO message, indicating that something relatively uncommon and
     * interesting happened, like server or module startup or shutdown, or
     * a periodic job.
     *
     * @param message The message.
     */
    public final void logInfo(String message) {
        StringBuffer m=new StringBuffer();
        m.append(getClass().getName());
        m.append(": ");
        m.append(message);
        getServer().logInfo(m.toString());
    }

    public final boolean loggingInfo() {
        return getServer().loggingInfo();
    }

    /**
     * Logs a CONFIG message, indicating what occurred during the server's
     * (or a module's) configuration phase.
     *
     * @param message The message.
     */
    public final void logConfig(String message) {
        StringBuffer m=new StringBuffer();
        m.append(getClass().getName());
        m.append(": ");
        m.append(message);
        getServer().logConfig(m.toString());
    }

    public final boolean loggingConfig() {
        return getServer().loggingConfig();
    }

    /**
     * Logs a FINE message, indicating basic information about a request to
     * the server (like hostname, operation name, and success or failure).
     *
     * @param message The message.
     */
    public final void logFine(String message) {
        StringBuffer m=new StringBuffer();
        m.append(getClass().getName());
        m.append(": ");
        m.append(message);
        getServer().logFine(m.toString());
    }

    public final boolean loggingFine() {
        return getServer().loggingFine();
    }

    /**
     * Logs a FINER message, indicating detailed information about a request
     * to the server (like the full request, full response, and timing
     * information).
     *
     * @param message The message.
     */
    public final void logFiner(String message) {
        StringBuffer m=new StringBuffer();
        m.append(getClass().getName());
        m.append(": ");
        m.append(message);
        getServer().logFiner(m.toString());
    }

    public final boolean loggingFiner() {
        return getServer().loggingFiner();
    }

    /**
     * Logs a FINEST message, indicating method entry/exit or extremely
     * verbose information intended to aid in debugging.
     *
     * @param message The message.
     */
    public final void logFinest(String message) {
        StringBuffer m=new StringBuffer();
        m.append(getClass().getName());
        m.append(": ");
        m.append(message);
        getServer().logFinest(m.toString());
    }

    public final boolean loggingFinest() {
        return getServer().loggingFinest();
    }

}