/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.utility.validate.process;

import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import fedora.client.utility.validate.ValidationResult;
import fedora.client.utility.validate.ValidationResultNotation;
import fedora.client.utility.validate.ValidationResults;

/**
 * An implementation of {@link ValidationResults} for use with the
 * {@link ValidatorProcess}. When {@link #record(ValidationResult)} is called,
 * the result is evaluated against the current Log4J configuration. If any of
 * the notes qualify for logging, they will be preceded by an overall log
 * record.
 * 
 * @author Jim Blake
 */
public class Log4jValidationResults
        implements ValidationResults {

    public static final String LOGGING_CATEGORY_PREFIX = "Validator";

    /** These Log4J settings will be used if no others are provided. */
    private static final Properties DEFAULT_CONFIG_PROPERTIES =
            initDefaultProperties();

    /** Initialize the {@link #DEFAULT_CONFIG_PROPERTIES}. */
    private static Properties initDefaultProperties() {
        Properties props = new Properties();

        // Create an appender for the root logger.
        props.put("log4j.appender.STDOUT", "org.apache.log4j.ConsoleAppender");
        props.put("log4j.appender.STDOUT.layout",
                  "org.apache.log4j.PatternLayout");
        props.put("log4j.appender.STDOUT.layout.ConversionPattern",
                  "%d{yyyy-MM-dd' 'HH:mm:ss.SSS} %p [%c] %m%n");

        // Assign the appender to the root logger.
        props.put("log4j.rootLogger", "INFO, STDOUT");

        // Create an appender for the validation messages.
        props.put("log4j.appender.VALIDATOR",
                  "org.apache.log4j.ConsoleAppender");
        props.put("log4j.appender.VALIDATOR.layout",
                  "org.apache.log4j.PatternLayout");
        props.put("log4j.appender.VALIDATOR.layout.ConversionPattern",
                  "%p [%c] %m%n");

        // Assign the appender to the validator, with no pass-through.
        props.put("log4j.logger.Validator=INFO", "INFO, VALIDATOR");
        props.put("log4j.additivity.Validator", "false");

        return props;
    }

    private int numberOfResults;

    public Log4jValidationResults(Properties configProperties) {
        LogManager.resetConfiguration();

        if (configProperties == null || configProperties.isEmpty()) {
            PropertyConfigurator.configure(DEFAULT_CONFIG_PROPERTIES);
        } else {
            PropertyConfigurator.configure(configProperties);
        }
    }

    /**
     * This class does not maintain a collection of the {@link ValidationResult}
     * objects. Instead, it logs each as it arrives, if it is severe enough.
     */
    public void record(ValidationResult result) {
        numberOfResults++;

        getBaseLogger().debug(result.toString());
        
        for (ValidationResultNotation note : result.getNotes()) {
            Logger logger = getNoteLogger(note);
            Level level = getNoteLevel(note);
            String message = assembleMessage(result, note);
            logger.log(level, message);
        }
    }

    /**
     * Each {@link ValidationResult} was logged (or not) when it arrived, so
     * just log a summary message at the end.
     */
    public void closeResults() {
        Logger logger = getBaseLogger();
        logger.info("Validated " + numberOfResults + " objects.");
    }

    /**
     * The log message contains both the object PID and the content of the
     * notation.
     */
    private String assembleMessage(ValidationResult result,
                                   ValidationResultNotation note) {
        String pid = result.getObject().getPid();
        return "pid='" + pid + "'  " + note.getMessage();
    }

    private Level getNoteLevel(ValidationResultNotation note) {
        return Level.toLevel(note.getLevel().toString());
    }

    private Logger getNoteLogger(ValidationResultNotation note) {
        String category = LOGGING_CATEGORY_PREFIX + "." + note.getCategory();
        return Logger.getLogger(category);
    }

    private Logger getBaseLogger() {
        return Logger.getLogger(LOGGING_CATEGORY_PREFIX);
    }
}
