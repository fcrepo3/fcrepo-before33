package fedora.client.console;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * <p><b>Title:</b> Console.java</p>
 * <p><b>Description:</b> </p>
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