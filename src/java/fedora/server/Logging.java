package fedora.server;

/**
 *
 * <p><b>Title:</b> Logging.java</p>
 * <p><b>Description:</b> A class that has methods for logging.</p>
 *
 * <p>The methods starting with <i>logging</i> can be used to quickly
 * check whether a certain type of logging is enabled.  This
 * helps in situations where the process of building the log
 * message (before calling <i>logXXX</i>) takes enough time that
 * it should not run unless necessary.</p>
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
public interface Logging {

    public abstract void logSevere(String message);
    public abstract void logWarning(String message);
    public abstract void logInfo(String message);
    public abstract void logConfig(String message);
    public abstract void logFine(String message);
    public abstract void logFiner(String message);
    public abstract void logFinest(String message);

    public abstract boolean loggingSevere();
    public abstract boolean loggingWarning();
    public abstract boolean loggingInfo();
    public abstract boolean loggingConfig();
    public abstract boolean loggingFine();
    public abstract boolean loggingFiner();
    public abstract boolean loggingFinest();

}