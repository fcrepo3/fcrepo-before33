package fedora.client.console;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * <p><b>Title:</b> Console.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public interface Console {

    /** Gets an object that fulfills the command. */
    public Object getInvocationTarget(ConsoleCommand cmd)
            throws InvocationTargetException;

    /** Sends the given text to the console. */
    public void print(String output);

    /** Clears the console. */
    public void clear();

    /** Tells the console whether it should look busy or not. */
    public void setBusy(boolean busy);

    /** Checks whether the console is busy. */
    public boolean isBusy();

}