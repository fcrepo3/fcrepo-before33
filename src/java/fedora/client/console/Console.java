package fedora.client.console;

import java.lang.reflect.InvocationTargetException;

public interface Console {

    /** Gets an object that fulfills the command. */
    public Object getInvocationTarget(ConsoleCommand cmd)
            throws InvocationTargetException;
    
    /** Sends the given text to the console. */
    public void print(String output);
    
    /** Clears the console. */
    public void clear();

}