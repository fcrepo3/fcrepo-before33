package fedora.utilities.install;

import java.io.*;
import java.util.*;

public class Installer {

    private Distribution _dist;
    private InstallOptions _opts;

    public Installer(Distribution dist,
                     InstallOptions opts) {

        _dist = dist;
        _opts = opts;
    }

    /**
     * Install the distribution based on the options.
     */
    public void install() throws InstallationFailedException {

        throw new InstallationFailedException("not implemented");
    }

    /**
     * Command-line entry point.
     */
    public static void main(String[] args) {

        try {
            Distribution dist = new ClassLoaderDistribution();
            InstallOptions opts = null;

            if (args.length == 0) {
                opts = new InstallOptions(dist.isBundled());
            } else if (args.length == 1) {
                Properties props = loadProperties(new File(args[0]));
                opts = new InstallOptions(props, dist.isBundled());
            } else {
                System.err.println("ERROR: Too many arguments.");
                System.err.println("Usage: java -jar fedora-install.jar [options-file]");
                System.exit(1);
            }

            new Installer(dist, opts).install();

        } catch (Exception e) {
            printException(e);
            System.exit(1);
        }
    }

    /**
     * Load properties from the given file.
     */
    private static Properties loadProperties(File f) throws IOException {
        Properties props = new Properties();
        FileInputStream in = new FileInputStream(f);
        try {
            props.load(in);
            return props;
        } finally {
            try { in.close(); } catch (IOException e) { }
        }
    }

    /**
     * Print a message appropriate for the given exception
     * in as human-readable way as possible.
     */
    private static void printException(Exception e) {

        if (e instanceof InstallationCancelledException) {
            System.out.println("Installation cancelled.");
            return;
        }

        boolean recognized = false;
        String msg = "ERROR: ";
        if (e instanceof InstallationFailedException) {
            msg += "Installation failed: " + e.getMessage();
            recognized = true;
        } else if (e instanceof OptionValidationException) {
            OptionValidationException ove = (OptionValidationException) e;
            msg += "Bad value for '" + ove.getOptionId() + "': " + e.getMessage();
            recognized = true;
        }
        
        if (recognized) {
            System.err.println(msg);
            if (e.getCause() != null) {
                System.err.println("Caused by: ");
                e.getCause().printStackTrace(System.err);
            }
        } else {
            System.err.println(msg + "Unexpected error; installation aborted.");
            e.printStackTrace();
        }
    }

}
