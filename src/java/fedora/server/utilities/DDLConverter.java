package fedora.server.utilities;

public interface DDLConverter {

    public abstract boolean supportsTableType();

    public abstract String getDDL(TableSpec tableSpec);

}

