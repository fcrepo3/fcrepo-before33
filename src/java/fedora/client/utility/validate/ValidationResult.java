/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.utility.validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * The accumulated result of validating on a digital object. This result
 * contains a reference to the object and may also contain (0 or more)
 * "notations" that describe the results of the validation.
 * </p>
 * <p>
 * Each notation has a {@link Level} associated with it, and if any of these
 * levels are {@link Level#ERROR ERROR}, the entire result is considered to be
 * a "failure".
 * </p>
 * 
 * @author Jim Blake
 */
public class ValidationResult {

    public enum Level {
        INFO, WARN, ERROR
    };

    private final ValidationObject object;

    private final List<ValidationResultNotation> notes =
            new ArrayList<ValidationResultNotation>();

    public ValidationResult(ValidationObject object) {
        if (object == null) {
            throw new IllegalArgumentException("object may not be null.");
        }
        this.object = object;
    }

    public void addNote(ValidationResultNotation note) {
        notes.add(note);
    }

    public ValidationObject getObject() {
        return object;
    }

    public Collection<ValidationResultNotation> getNotes() {
        return new ArrayList<ValidationResultNotation>(notes);
    }

    /**
     * What's the highest severity level of any of the notations on this result?
     */
    public Level getSeverityLevel() {
        Level severity = Level.INFO;
        for (ValidationResultNotation note : notes) {
            Level noteLevel = note.getLevel();
            if (noteLevel.compareTo(severity) > 0) {
                severity = noteLevel;
            }
        }
        return severity;
    }

    @Override
    public String toString() {
        return "pid='" + object.getPid() + "', " + " Notes=" + notes;
    }
}
