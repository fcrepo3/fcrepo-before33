package fedora.server.storage;

/**
 * <p>Title: DOReader.java</p>
 * <p>Description: Interface for reading Fedora digital objects</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import fedora.server.storage.types.*;
import java.util.Date;
import java.io.InputStream;

public interface DOReader
{

  /**
   * Methods that pertain to the digital object as a whole
   */

    public String GetObjectXML();

    public String ExportObject();

  /**
   * Methods that pertain to getting the digital object components
   */

    public String GetObjectPID();

    public String GetObjectLabel();

    public String[] ListDatastreamIDs(String state);

    public Datastream[] GetDatastreams(Date versDateTime);

    public Datastream GetDatastream(String datastreamID, Date versDateTime);

    public Disseminator[] GetDisseminators(Date versDateTime);

    public String[] ListDisseminatorIDs(String state);

    public Disseminator GetDisseminator(String disseminatorID, Date versDateTime);

  /**
   *  Methods to obtain information stored in the Behavior Definition and
   *  Behavior Mechanism objects to which the digital object's disseminators
   *  refer.
   */

    // Returns PIDs of Behavior Definitions to which object subscribes
    public String[] GetBehaviorDefs(Date versDateTime);

    // Returns list of methods that Behavior Mechanism implements for a BDef
    public MethodDef[] GetBMechMethods(String bDefPID, Date versDateTime);

    // Overloaded method: returns InputStream as alternative
    public InputStream GetBMechMethodsWSDL(String bDefPID, Date versDateTime);

    public DSBindingMapAugmented[] GetDSBindingMaps(Date versDateTime);

}