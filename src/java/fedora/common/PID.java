package fedora.common;

import java.io.*;  // BufferedReader, InputStreamReader

/**
 * A persistent identifier for Fedora digital objects.
 *
 * @author cwilper@cs.cornell.edu
 */
public class PID {

    public static final int MAX_LENGTH = 64;

    private String m_normalized;

    /**
     * Construct a PID from a string, throwing a MalformedPIDException
     * if it's not well-formed.
     */
    public PID(String pidString) 
            throws MalformedPIDException {
        m_normalized = normalize(pidString);
    }

    public PID fromFilenameString(String filenameString) 
            throws MalformedPIDException {
        return new PID(decodeFilename(filenameString));
    }

    /**
     * Return the normalized form of the given pid string,
     * or throw a MalformedPIDException.
     */
    public static String normalize(String pidString) 
            throws MalformedPIDException {
        // Null & length checks
        if (pidString == null) {
            throw new MalformedPIDException("PID is null.");
        }
        if (pidString.length() > MAX_LENGTH) {
            throw new MalformedPIDException("PID length exceeds " 
                    + MAX_LENGTH + ".");
        }

        StringBuffer out = new StringBuffer();
        for (int i = 0; i < pidString.length(); i++) {
            char c = pidString.charAt(i);
           
        }
        return out.toString();
    }

    /**
     * Returns the normalized form of this PID.
     */
    public String toString() {
        return m_normalized;
    }

    /**
     * Returns a string representing this PID that can be safely used
     * as a filename on any OS.
     */
    public String toFilenameString() {
        return m_normalized;  // TODO
    }

    private static String decodeFilename(String filenameString) {
        return filenameString; // TODO
    }

    /**
     * Command-line interactive tester.
     *
     * If one arg given, prints normalized form of that PID and exits.
     * If no args, enters interactive mode.
     */
    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            System.out.println(new PID(args[0]).toString());
        } else {
            System.out.println("--------------------------------------");
            System.out.println("PID Syntax Checker - Interactive mode");
            System.out.println("--------------------------------------");
            boolean done = false;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (!done) {
                try {
                    System.out.print("Enter a PID (ENTER to exit): ");
                    String line = reader.readLine();
                    if (line.equals("")) {
                        done = true;
                    } else {
                        System.out.println("GOOD: " + new PID(line).toString());
                    }
                } catch (MalformedPIDException e) {
                    System.out.println("BAD: " + e.getMessage());
                }
            }
        }
    }

}
