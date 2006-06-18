package fedora.utilities.install;

/**
 * Signals that an option's value was not valid.
 */
public class OptionValidationException extends Exception {

    private String _id;

    public OptionValidationException(String msg, String optionId) {
        super(msg);
        _id = optionId;
    }

    public OptionValidationException(String msg, String optionId, Throwable cause) {
        super(msg, cause);
        _id = optionId;
    }

    public String getOptionId() {
        return _id;
    }

}
