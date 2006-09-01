package fedora.utilities.install;

import java.io.*;
import java.util.*;

public class OptionDefinition {

    private static Properties _PROPS;

    private String _id;
    private String _label;

    private String _description;
    private String _descriptionIfBundled;

    private String[] _validValues;
    private String[] _validValuesIfBundled;

    private String _defaultValue;

    static {
        String path = "fedora/utilities/install/OptionDefinition.properties";
        try {
            InputStream in = OptionDefinition.class.getClassLoader().
                    getResourceAsStream(path);
            _PROPS = new Properties();
            _PROPS.load(in);
        } catch (Exception e) {
            System.err.println("ERROR: Unable to load required resource: " + path);
            System.exit(1);
        }
    }

    private OptionDefinition(String id,
                             String label,
                             String description,
                             String descriptionIfBundled,
                             String[] validValues,
                             String[] validValuesIfBundled,
                             String defaultValue) {
        _id = id;
        _label = label;

        _description = description;
        _descriptionIfBundled = descriptionIfBundled;

        _validValues = validValues;
        _validValuesIfBundled = validValuesIfBundled;

        _defaultValue = defaultValue;
    }

    /**
     * Get the definition of the identified option, or <code>null</code>
     * if no such definition exists.
     */
    public static OptionDefinition get(String id) {
        
        String label = _PROPS.getProperty(id + ".label");
        String description = _PROPS.getProperty(id + ".description");
        String descriptionIfBundled = _PROPS.getProperty(id + ".descriptionIfBundled");

        String[] validValues = getArray(id + ".validValues");
        String[] validValuesIfBundled = getArray(id + ".validValuesIfBundled");

        String defaultValue = _PROPS.getProperty(id + ".defaultValue");

        return new OptionDefinition(id, label, description, 
                descriptionIfBundled, validValues, validValuesIfBundled,
                defaultValue);
    }

    private static String[] getArray(String propName) {

        String value = _PROPS.getProperty(propName);
        if (value == null) {
            return null;
        } else {
            return value.trim().split(" ");
        }
    }

    public String getId() {
        return _id;
    }

    public String getLabel() {
        return _label;
    }

    public String getDescription(boolean bundled) {
        if (bundled && _descriptionIfBundled != null) {
            return _descriptionIfBundled;
        } else {
            return _description;
        }
    }

    public String[] getValidValues(boolean bundled) {
        if (bundled && _validValuesIfBundled != null) {
            return _validValuesIfBundled;
        } else {
            return _validValues;
        }
    }

    public String getDefaultValue() {
        return _defaultValue;
    }

    public void validateValue(String value, boolean bundled) 
            throws OptionValidationException {
        if (value.length() == 0) {
            throw new OptionValidationException("Must specify a value", _id);
        }
        String[] valids = getValidValues(bundled);
        if (valids != null) {
            boolean isValid = false;
            for (int i = 0; i < valids.length; i++) {
                if (valids[i].equals(value)) {
                    isValid = true;
                }
            }
            if (!isValid) {
                throw new OptionValidationException("Not a valid value: " 
                        + value, _id);
            }
        } else {
            if (_id.equals(InstallOptions.FEDORA_HOME)) {
                File dir = new File(value);
                if (dir.isDirectory()) {
                    throw new OptionValidationException("Directory exists; delete it or choose another", _id);
                } else {
                    // must be creatable
                    boolean created = dir.mkdir();
                    if (!created) {
                        throw new OptionValidationException("Unable to create specified directory", _id);
                    } else {
                        dir.delete();
                    }
                }
            } else if (_id.equals(InstallOptions.TOMCAT_HOME)) {
                File dir = new File(value);
                if (dir.exists()) {
                    // must have webapps subdir
                    File webapps = new File(dir, "webapps");
                    if (!webapps.exists()) {
                        throw new OptionValidationException("Directory exists but does not contain a webapps subdirectory", _id);
                    }
                } else {
                    // or must be creatable
                    boolean created = dir.mkdir();
                    if (!created) {
                        throw new OptionValidationException("Unable to create specified directory", _id);
                    } else {
                        dir.delete();
                    }
                }
            } else if (_id.equals(InstallOptions.TOMCAT_SHUTDOWN_PORT)) {
                validatePort(value);
            } else if (_id.equals(InstallOptions.TOMCAT_HTTP_PORT)) {
                validatePort(value);
            } else if (_id.equals(InstallOptions.TOMCAT_SSL_PORT)) {
                validatePort(value);
            } else if (_id.equals(InstallOptions.KEYSTORE_FILE)) {
                if (!value.equals("default")) {
                    validateExistingFile(value);
                }
            } else if (_id.equals(InstallOptions.JDBC_JAR_FILE)) {
                if (!bundled) {
                    validateExistingFile(value);
                } else {
                    if (!value.equals("bundledMySQL") && !value.equals("bundledMcKoi")) {
                        File f = new File(value);
                        if (!f.exists()) {
                            throw new OptionValidationException(
                                    "No such file; must specify a file path, "
                                  + "bundledMySQL, or bundledMcKoi", _id);
                        }
                    }
                }
            }
        }
    }

    private void validateExistingFile(String val) throws OptionValidationException {
        File f = new File(val);
        if (!f.exists()) {
            throw new OptionValidationException("No such file", _id);
        }
    }

    private void validatePort(String val) throws OptionValidationException {
        try {
            int port = Integer.parseInt(val);
            if (port < 0 || port > 65535) {
                throw new OptionValidationException("Not a valid port number", _id);
            }
        } catch (NumberFormatException e) {
            throw new OptionValidationException("Not an integer", _id);
        }
    }

}
