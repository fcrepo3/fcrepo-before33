package fedora.access;

import java.io.InputStream;
import java.util.Date;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */

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
   public ArrayofString GetBehaviorDefinitions( String PID, Date asOfDate );

   /**
    *
    * @param bDefPID
    * @return
    */
   public MIMEStream GetMethods( String PID, String bDefPID, Date asOfDate );

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
    public MIMEStream GetDissemination ( String PID, String bDefPID,
        String method, String[][] userParms, Date asOfDate );

}