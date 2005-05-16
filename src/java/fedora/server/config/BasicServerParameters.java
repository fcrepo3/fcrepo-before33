package fedora.server.config;

/**
 * @author Edwin Shin
 */
public interface BasicServerParameters {
    public static final String CLASS                   = "fedora.server.BasicServer";
    public static final String PARAM_REPOSITORY_NAME   = "repositoryName";
    public static final String PARAM_ADMIN_EMAILS      = "adminEmailList";
    public static final String PARAM_ADMIN_USER        = "adminUsername";
    public static final String PARAM_ADMIN_PASS        = "adminPassword";
    public static final String PARAM_BE_USER           = "backendUsername";
    public static final String PARAM_BE_PASS           = "backendPassword";
    public static final String PARAM_LOG_MAX_SIZE      = "log_max_size";
    public static final String PARAM_LOG_MAX_DAYS      = "log_max_days";
    public static final String PARAM_LOG_DIR           = "log_dir";
    public static final String PARAM_LOG_LEVEL         = "log_level";
    public static final String PARAM_LOG_FLUSH         = "log_flush_threshold";
    public static final String PARAM_FILE_SYSTEM       = "file_system";
    public static final String PARAM_REGISTRY          = "registry";
    public static final String PARAM_BACKSLASH_ESCAPE  = "backslash_is_escape";
    public static final String PARAM_STORE_OBJECT      = "object_store_base";
    public static final String PARAM_STORE_TEMP        = "temp_store_base";
    public static final String PARAM_STORE_DS          = "datastream_store_base";
    public static final String PARAM_DEBUG             = "debug";
    public static final String PARAM_DS_EXPIRATION     = "datastreamExpirationLimit";
    public static final String PARAM_PORT              = "fedoraServerPort";
    public static final String PARAM_SHUTDOWN_PORT     = "fedoraShutdownPort";
    public static final String PARAM_REDIRECT_PORT     = "fedoraRedirectPort";
    public static final String PARAM_HOST              = "fedoraServerHost";
}
