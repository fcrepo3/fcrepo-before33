package fedora.server.storage;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */
import java.util.Date;
import java.io.InputStream;
import fedora.server.management.*;
import fedora.server.behavior.*;

public abstract class ObjectReader
{

  public ObjectReader(String objectPID)
  {
  }

  /**
   * Methods that pertain to the digital object as a whole
   */
    public abstract String GetObjectPID();

    public abstract InputStream GetObjectXML();

  /**
   * Methods that pertain to the digital object components
   */
    public abstract Datastream[] GetDatastreams();

    public abstract Datastream[] GetDatastreams(Date versDateTime);

    public abstract Datastream GetDatastream(String datastreamID);

    public abstract Datastream GetDatastream(String datastreamID, Date versDateTime);

    public abstract Disseminator[] GetDisseminators();

    public abstract Disseminator[] GetDisseminators(Date versDateTime);

    public abstract Disseminator GetDisseminator(String disseminatorID);

    public abstract Disseminator GetDisseminator(String disseminatorID, Date versDateTime);

    public abstract SystemMetadata GetSystemMetadata();

  /**
   *  Methods to obtain information stored in the Behavior Definition and
   *  Behavior Mechanism objects to which the digital object's disseminators
   *  refer.
   */

    // Need to revisit whether return value is bDef's PID or PID+versionID
    public abstract String[] GetBdefIDs();

    // Need to revisit whether return value is bMech's PID or PID+versionID
    public abstract String[] GetBmechIDs();

    // Need to revisit whether parm value is bDef's PID or PID+versionID
    public abstract MethodDef[] GetMethods(String BdefID);

    // Overloaded method: returns InputStream as alternative
    public abstract InputStream GetMethodsWSDL(String BdefID);

}