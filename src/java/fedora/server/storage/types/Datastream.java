package fedora.server.storage.types;

import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ServerInitializationException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.ValidationException;
import fedora.server.storage.DOManager;
import fedora.server.utilities.StreamUtility;
import fedora.server.utilities.StringUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.xml.sax.SAXException;


/**
 *
 * <p><b>Title:</b> Datastream.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class Datastream
{
  public final static String CHECKSUMTYPE_DISABLED = "DISABLED";
  public final static String CHECKSUM_IOEXCEPTION = "ExceptionReadingStream";
  
  public boolean isNew=false;
  
  public String DatastreamID;
  
  public String[] DatastreamAltIDs = new String[0];
 
  public String DSFormatURI;
  
  public String DSMIME;
  
  /** Datastream Control Group:
   *  This indicates the nature of the repository's control over the
   *  datastream content.  Values are:
   *  <p>
   *  R = Redirected.
   *      The datastream resides on an external server and is referenced
   *      by a URL.  When a dissemination request for the *datastream*
   *      comes through, Fedora sends an HTTP redirect to the client,
   *      thereby causing the client to directly access the datastream
   *      from its external location.  This is useful in cases where
   *      the datastream is really some sort of streaming media that
   *      cannot be piped through Fedora, or the datastream is an
   *      HTML document with relative hyperlinks to the server on
   *      which is is normally hosted.
   *  E = External Referenced.  The datastream content is external to the repository
   *      and referenced by URL.  The content is not under the direct
   *      custodianship of the repository.  The URL is considered public
   *      so the repository does not worry about whether it exposes the
   *      datastream location to collaborating services.
   *  M = Managed Content.  The datastream content is stored and managed
   *      by the repository.  The content is considered under the direct
   *      custodianship of the repository.  The repository does not expose
   *      the underlying storage location to collaborating services and
   *      it mediates all access to the content by collaborating services.
   *  X = Inline XML Metadata.  The datastream content is user-defined
   *      XML metadata that is stored within the digital object XML file itself.
   *      As such, it is intrinsically bound to the digital object, and by
   *      implication, it is stored and managed by the repository.  The content
   *      considered under the custodianship of the repository.
   */
  public String DSControlGrp;

  /** Info Type: DATA or one of the METS MDType values */
  /** Used to maintain backwards compatibility with METS-Fedora */
  public String DSInfoType;

  public String DSState;
  
  public boolean DSVersionable;
  
  // Version-level attributes:
  public String DSVersionID;
  public String DSLabel;
  public Date DSCreateDT;
  public long DSSize;
  public String DSLocation;
  public String DSLocationType;
  public String DSChecksumType;
  public String DSChecksum;
  public static String defaultChecksumType = null;
  public static String checksumErrorAction = null;
  public static File checksumErrorFile = null;

  public Datastream() {
  } 

  public InputStream getContentStream()
          throws StreamIOException
  {
      return null;
  }
  
  public static String getDefaultChecksumType()
  {
      if (defaultChecksumType != null) return(defaultChecksumType);
      defaultChecksumType = "DISABLED";
      try 
      {
          System.out.println("Getting Server");
          Server server = Server.getInstance(new File(System.getProperty("fedora.home")), false);
          System.out.println("Got Server");
          String auto = server.getParameter("autoChecksum");
          System.out.println("Got Parameter: autoChecksum = "+ auto);
          if (auto.equalsIgnoreCase("true"))
          {
              defaultChecksumType = server.getParameter("checksumAlgorithm");
          }
          else
          {
              defaultChecksumType = "DISABLED";
          }
      }
      catch (Exception e)
      {
          System.out.println("Exception in getting default checksum type");
          e.printStackTrace();
          // IGNORE  
      }
      return(defaultChecksumType);
  }
  
  public static void LogChecksumMismatch(DigitalObject obj, Datastream ds, String checksumSupplied) throws ValidationException
  {
      if (checksumErrorAction == null)
      {
          try 
          {
              System.out.println("Getting Server");
              Server server = Server.getInstance(new File(System.getProperty("fedora.home")), false);
              System.out.println("Got Server");
              String action = server.getParameter("checksumMismatchAction");
              System.out.println("Got Parameter: checksumMismatchAction = "+ action);
              if (action.equalsIgnoreCase("EXCEPTION"))
              {
                  checksumErrorAction = action;
              }
              else if (action.startsWith("LOG_TO:"))
              {
                  checksumErrorAction = action.substring(7);
                  checksumErrorFile = new File(checksumErrorAction);
                  if (!checksumErrorFile.canWrite())
                  {
                      checksumErrorAction = "EXCEPTION";
                      checksumErrorFile = null;
                  }
              }
          }
          catch (Exception e)
          {
              System.out.println("Exception in getting checksum mismatch action");
              checksumErrorAction = "EXCEPTION";
              e.printStackTrace();
              // IGNORE  
          }
      }
      if (checksumErrorAction.equals("EXCEPTION"))
      {
          throw new ValidationException("Checksum Mismatch: " + checksumSupplied + " != " + ds.DSChecksum);
      }
      else
      {
        try
        {
            OutputStream os = new FileOutputStream(checksumErrorFile, true);
            PrintWriter out = new PrintWriter(os);
            out.println("Checksum Mismatch on datastream: " +ds.DatastreamID + " of object " + obj.getPid() +
                        "  " + checksumSupplied + " != " + ds.DSChecksum);
            out.close();
        }
        catch (FileNotFoundException e)
        {
            throw new ValidationException("Checksum Mismatch: " + checksumSupplied + " != " + ds.DSChecksum);
        }      
      }
  }
  
  public String getChecksumType()
  {
      if (DSChecksumType == null || DSChecksumType.equals("") || DSChecksumType.equals("none"))
      {
          DSChecksumType = getDefaultChecksumType(); 
      }
      return(DSChecksumType);     
  }
  
  public String getChecksum()
  {
      if (DSChecksum == null || DSChecksum.equals("none"))
      {
          DSChecksum = computeChecksum(getChecksumType());          
      }
      System.out.println("Checksum = " + DSChecksum);
      return(DSChecksum);
  }
  
  public String setChecksum(String csType)
  {
      if (csType != null)  DSChecksumType = csType;
      System.out.println("setting ChecksumType to "+ DSChecksumType);
      DSChecksum = computeChecksum(DSChecksumType);
      return(DSChecksum);
  }
  
  public boolean compareChecksum()
  {
      if (DSChecksum == null || DSChecksum.equals("none"))
      {
          return(false);         
      }
      if (DSChecksumType == null || DSChecksumType.equals("") || DSChecksumType.equals("none"))
      {
          return(false);
      }
      String curChecksum = computeChecksum(DSChecksumType);
      if (curChecksum.equals(DSChecksum)) return(true);
      return(false);
  }
  
  private String computeChecksum(String csType)
  {
      String checksum = "none";
      if (csType.equals(CHECKSUMTYPE_DISABLED))
      {
          checksum = "none";
          return(checksum);
      }
      try
      {
          MessageDigest md = MessageDigest.getInstance(csType);
          System.out.println("Classname = " + this.getClass().getName());
          System.out.println("location = " + this.DSLocation);
          InputStream is = getContentStream();          
          if (is != null)
          {
              byte buffer[] = new byte[5000];
              int numread;
              System.out.println("Reading content...");
              while ((numread = is.read(buffer, 0, 5000)) > 0)
              {
                  md.update(buffer, 0, numread);
              }  
              System.out.println("...Done reading content");
              //checksum = StreamUtility.encodeBase64(md.digest());
              checksum = StringUtility.byteArraytoHexString(md.digest());
          }
      }
      catch (NoSuchAlgorithmException e)
      {
          checksum = "none";
      }
      catch (StreamIOException e)
      {
          // TODO Auto-generated catch block
          checksum = CHECKSUM_IOEXCEPTION;
          e.printStackTrace();
      }
      catch (IOException e)
      {
          // TODO Auto-generated catch block
          checksum = CHECKSUM_IOEXCEPTION;
          e.printStackTrace();
      }
      return(checksum);      
  }
 
  // Get a complete copy of this datastream
  public Datastream copy() {
      Datastream ds = new Datastream();
      copy(ds);
      return ds;
  }

  // Copy this instance into target
  public void copy(Datastream target) {

      target.isNew = isNew;
      target.DatastreamID = DatastreamID;
      if (DatastreamAltIDs != null) {
          target.DatastreamAltIDs = new String[DatastreamAltIDs.length];
          for (int i = 0; i < DatastreamAltIDs.length; i++) {
              target.DatastreamAltIDs[i] = DatastreamAltIDs[i];
          }
      }
      target.DSFormatURI = DSFormatURI;
      target.DSMIME = DSMIME; 
      target.DSControlGrp = DSControlGrp;
      target.DSInfoType = DSInfoType;
      target.DSState = DSState;
      target.DSVersionable = DSVersionable;
      target.DSVersionID = DSVersionID;
      target.DSLabel = DSLabel;
      target.DSCreateDT = DSCreateDT;
      target.DSSize = DSSize;
      target.DSLocation = DSLocation;
      target.DSLocationType = DSLocationType;
  }

}
