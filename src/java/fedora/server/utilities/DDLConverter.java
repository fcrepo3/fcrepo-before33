package fedora.server.utilities;

import java.util.List;

/**
 * Interface for a converter of TableSpec objects to RDBMS-specific DDL
 * code.
 * <p></p>
 * Implementations of this class must be thread-safe.  That is, one
 * instance can be used simultanously without problems.  This typically
 * just means that no varying fields should be used.
 * <p></p>
 * Implementations must also have a public no-arg constructor.
 */
public interface DDLConverter {

    public abstract boolean supportsTableType();

    public abstract List getDDL(TableSpec tableSpec);

}

