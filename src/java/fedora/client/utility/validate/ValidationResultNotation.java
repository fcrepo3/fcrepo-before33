/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.utility.validate;

import fedora.client.utility.validate.ValidationResult.Level;

/**
 * A note that can be attached to a {@link ValidationResult}. Also, a
 * collection of static methods for creating such notes.
 * 
 * @author Jim Blake
 */
public class ValidationResultNotation {

    public static ValidationResultNotation noContentModel() {
        return new ValidationResultNotation(Level.INFO,
                                            "NoContentModel",
                                            "No content model.");
    }

    public static ValidationResultNotation unrecognizedContentModelUri(String uri) {
        return new ValidationResultNotation(Level.INFO,
                                            "UnrecognizedContentModelUri",
                                            "Content model URI is not recognized "
                                                    + "as an object PID: '"
                                                    + uri + "'");
    }

    public static ValidationResultNotation contentModelNotFound(String pid) {
        return new ValidationResultNotation(Level.INFO,
                                            "ContentModelNotFound",
                                            "Content model was not found, PID='"
                                                    + pid + "'");
    }

    public static ValidationResultNotation errorFetchingContentModel(String pid,
                                                                     ObjectSourceException e) {
        return new ValidationResultNotation(Level.ERROR,
                                            "ErrorFetchingContentModel",
                                            ("Attempt to fetch Content model '"
                                                    + pid
                                                    + "' produced this error '"
                                                    + e + "'"));
    }

    public static ValidationResultNotation contentModelIsMissingDs(String pid,
                                                                   String dsId) {
        return new ValidationResultNotation(Level.ERROR,
                                            "ContentModelIsMissingDs",
                                            ("Content model '"
                                                    + pid
                                                    + "' has no datastream named '"
                                                    + dsId + "'"));
    }

    private final Level level;

    private final String category;

    private final String message;

    private ValidationResultNotation(Level level,
                                     String category,
                                     String message) {
        if (level == null) {
            throw new IllegalArgumentException("level may not be null.");
        }
        this.level = level;

        this.message = message == null ? "" : message;
        this.category = category == null ? "" : category;
    }

    public Level getLevel() {
        return level;
    }

    public String getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return level + " [" + category + "] " + message;
    }

}
