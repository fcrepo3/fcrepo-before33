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
import fedora.server.errors.ServerException;
import java.util.Date;
import java.io.InputStream;

public interface DOReader
{

  /**
   * Methods that pertain to the digital object as a whole
   */

    public InputStream GetObjectXML() throws ServerException;

    public InputStream ExportObject() throws ServerException;

  /**
   * Methods that pertain to getting the digital object components
   */

    public String GetObjectPID() throws ServerException;

    public String GetObjectLabel() throws ServerException;

    public String GetObjectState() throws ServerException;

    public String[] ListDatastreamIDs(String state) throws ServerException;

    public Datastream[] GetDatastreams(Date versDateTime) throws ServerException;

    public Datastream GetDatastream(String datastreamID, Date versDateTime) throws ServerException;

    public Disseminator[] GetDisseminators(Date versDateTime) throws ServerException;

    public String[] ListDisseminatorIDs(String state) throws ServerException;

    public Disseminator GetDisseminator(String disseminatorID, Date versDateTime) throws ServerException;

  /**
   *  Methods to obtain information stored in the Behavior Definition and
   *  Behavior Mechanism objects to which the digital object's disseminators
   *  refer.
   */

    // Returns PIDs of Behavior Definitions to which object subscribes
    public String[] GetBehaviorDefs(Date versDateTime) throws ServerException;

    // Returns list of methods that Behavior Mechanism implements for a BDef
    public MethodDef[] GetBMechMethods(String bDefPID, Date versDateTime) throws ServerException;

    // Overloaded method: returns InputStream as alternative
    public InputStream GetBMechMethodsWSDL(String bDefPID, Date versDateTime) throws ServerException;

}