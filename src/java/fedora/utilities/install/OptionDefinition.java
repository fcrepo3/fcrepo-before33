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
        throw new OptionValidationException("not implemented", getId());
    }

}
