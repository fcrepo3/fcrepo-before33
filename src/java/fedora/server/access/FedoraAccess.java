package fedora.server.access;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */

import java.util.Calendar;
import java.util.Vector;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.Property;
import fedora.server.storage.types.MethodDef;

public interface FedoraAccess
{
  /** API-A Object Reflection Methods
   *
   */

   /**
    *
    * @param PID
    * @return
    */
   public String[] GetBehaviorDefinitions( String PID, Calendar asOfDate );

   /**
    *
    * @param bDefPID
    * @return
    */
   public MethodDef[] GetBehaviorMethods( String PID, String bDefPID, Calendar asOfDate );

   /** API-A Dissemination Method
    *
    */

    /**
     *
     * @param PID
     * @param bDefPID
     * @param method
     * @param asOfDate
     * @return
     */
    public MIMETypedStream GetDissemination ( String PID, String bDefPID,
        String method,
        Property[] userParms, Calendar asOfDate );

}