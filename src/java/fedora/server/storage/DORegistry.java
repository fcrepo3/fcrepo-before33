package fedora.server.storage;

// import fedora.server.ParameterizedComponent;

import java.net.URL;
import java.util.Iterator;

public abstract class DORegistry {
//        extends ParameterizedComponent {

    public abstract URL add();

    public abstract URL get(String PID);

    public abstract void remove();

    public abstract Iterator ids();

}