package fedora.client.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import javax.xml.rpc.ServiceException;

import fedora.client.APIMStubFactory;
import fedora.client.APIAStubFactory;
import fedora.client.batch.BatchModifyParser;
import fedora.client.Uploader;
import fedora.server.management.FedoraAPIM;
import fedora.server.access.FedoraAPIA;
import fedora.server.utilities.StreamUtility;


/**
 *
 * <p><b>Title:</b> AutoModify.java</p>
 * <p><b>Description:</b> This is a command-line version of the Batch Modify
 * utility that's available in the admin GUI client. It processes an xml
 * input file containing modify directives enabling mass updating of existing
 * objects. The utility has six required arguments:</p>
 * <ol>
 * <li>hostName - Name of the Fedora repository server.</li>
 * <li>portNum - port number o fthe Fedora server.</li>
 * <li>username - username of the Fedora server admin user</li>
 * <li>password - password of the Fedora server admin user</li>
 * <li>directivesFilePath - absolute file path of the input file containing
 *                          modify directives. Note that his file should
 *                          should validate against the batchModify schema.</li>
 * <li>logFilePath - absolute file path of the log file; an xml file providing
 *                   a history of the transactions processed.</li>
 * </ol>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class AutoModify
{

  private static String s_rootName = null;
  private static PrintStream s_log = null;
  private static FedoraAPIM s_APIM = null;
  private static FedoraAPIA s_APIA = null;
  private static Uploader s_UPLOADER = null;

  /**
   * <p> Constructor for the class.</p>
   *
   * @param host - Hostname of the Fedora server.
   * @param port - Port number of the Fedora server.
   * @param user - username of the Fedora server admin user.
   * @param pass - password of the Fedora server admin user.
   * @throws MalformedURLException - If the URL generated from host and port
   *                                 is invalid.
   * @throws ServiceException - If unable to connect via SOAP to the Fedora
   *                            API-M web service.
   * @throws IOException - If an error occurs in creating an instance of the
   *                       Uploader.
   */
  public AutoModify(String host, int port, String user, String pass)
      throws MalformedURLException, ServiceException, IOException
  {

    AutoModify.s_APIM=APIMStubFactory.getStub(host, port, user, pass);
    AutoModify.s_APIA=APIAStubFactory.getStub(host, port, user, pass);
    AutoModify.s_UPLOADER = new Uploader(host, port, user, pass);

  }

  /**
   * <p>Processes the modify directives.</p>
   *
   * @param directivesFilePath - The absolute file path of the file containing
   *                             the modify directives.
   * @param logFilePath - the absolute file path of the log file.
   */
  public void modify(String directivesFilePath, String logFilePath, boolean validateOnly)
  {
      modify(s_APIM, s_UPLOADER, directivesFilePath, logFilePath, validateOnly);
  }

  /**
   * <p>Process the modify directives.</p>
   *
   * @param APIM - An instance of FedoraAPIM.
   * @param UPLOADER - An instance of the Uploader.
   * @param directivesFilePath - The absolute file path of the file containing
   *                             the modify directives.
   * @param logFilePath - the absolute file path of the log file.
   */
    public static void modify(FedoraAPIM APIM, Uploader UPLOADER,
        String directivesFilePath, String logFilePath, boolean validateOnly)
    {

      InputStream in = null;
      BatchModifyParser bmp = null;
      BatchModifyValidator bmv = null;
      long st=System.currentTimeMillis();
      long et=0;
    try
    {
        in = new FileInputStream(directivesFilePath);
        if (validateOnly) {
            openLog(logFilePath, "validate-modify-directives");
            bmv = new BatchModifyValidator(in, s_log);
        } else {
            openLog(logFilePath, "modify-batch");
            bmp = new BatchModifyParser(s_UPLOADER, s_APIM, s_APIA, in, s_log);
        }

    } catch (Exception e)
    {
      System.out.println(e.getClass().getName() + " - "
            + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));

    }  finally
    {
      try
      {
        if (in != null)
        {
          in.close();
        }
        if (s_log!=null)
        {
          et=System.currentTimeMillis();
          if (bmp!=null) {
          if(bmp.getFailedCount()==-1)
          {
            System.out.println("\n\n"
                + bmp.getSucceededCount() + " modify directives successfully processed.\n"
                + "Parser error encountered.\n"
                + "An unknown number of modify directives were not processed.\n"
                + "See log file for details of those directives processed before the error.\n"
                + "Time elapsed: " + getDuration(et-st));
            s_log.println("  <summary>");
            s_log.println("    "+StreamUtility.enc(bmp.getSucceededCount()
                + " modify directives successfully processed.\n"
                + "    Parser error encountered.\n"
                + "    An unknown number of modify directives were not processed.\n"
                + "    Time elapsed: " + getDuration(et-st)));
            s_log.println("  </summary>");
          } else
          {
            System.out.println("\n\n"
                + bmp.getSucceededCount() + " modify directives successfully processed.\n"
                + bmp.getFailedCount() + " modify directives failed.\n"
                + "See log file for details.\n"
                + "Time elapsed: " + getDuration(et-st));
            s_log.println("  <summary>");
            s_log.println("    "+StreamUtility.enc(bmp.getSucceededCount()
                + " modify directives successfully processed.\n    "
                + bmp.getFailedCount() + " modify directives failed.\n"
                + "    Time elapsed: " + getDuration(et-st)));
            s_log.println("  </summary>");
          }
          } else if (bmv!=null) {
              et=System.currentTimeMillis();
              if (bmv.isValid()) {
                  System.out.println("Modify Directives File in \n"
                      +directivesFilePath
                      + "\n is Valid !"
                      + "\nTime elapsed: " + getDuration(et-st));
                  s_log.println("  <summary>");
                  s_log.println("    Modify Directives File: \n    "
                      +directivesFilePath
                      + "\n    is Valid !"
                      + "\n    Time elapsed: " + getDuration(et-st));
                  s_log.println("  </summary>");
                  closeLog();
                  return;
              } else {
                  System.out.println(bmv.getErrorCount()
                      +" XML validation Errors found in Modify Directives file.\n"
                      + "See log file for details.\n"
                      + "Time elapsed: " + getDuration(et-st));
                  s_log.println("  <summary>");
                  s_log.println("    "+StreamUtility.enc(bmv.getErrorCount()
                      + " XML validation Errors found in Modify Directives file.\n"
                      + "    See log file for details.\n"
                      + "    Time elapsed: " + getDuration(et-st)));
                  s_log.println("  </summary>");
              }
          }
          closeLog();
          System.out.println(
              "A detailed log file was created at\n"
              + logFilePath + "\n\n");
        }
      } catch (Exception e)
      {
        System.out.println(e.getClass().getName() + " - "
            + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
      }
    }
  }

  /**
   * <p>Convert the duration time from milliseconds to standard hours, minutes,
   * and seconds format.</p>
   *
   * @param millis - The time interval to convert in miliseconds.
   * @return A string with the converted time.
   */
  private static String getDuration(long millis)
  {
      long tsec=millis/1000;
      long h=tsec/60/60;
      long m=(tsec - (h*60*60))/60;
      long s=(tsec - (h*60*60) - (m*60));
      StringBuffer out=new StringBuffer();
      if (h>0)
      {
          out.append(h + " hour");
          if (h>1) out.append('s');
      }
      if (m>0)
      {
          if (h>0) out.append(", ");
          out.append(m + " minute");
          if (m>1) out.append('s');
      }
      if (s>0 || (h==0 && m==0))
      {
          if (h>0 || m>0) out.append(", ");
          out.append(s + " second");
          if (s!=1) out.append('s');
      }
      return out.toString();
  }

  /**
   * <p>Initializes the log file for writing.</p>
   *
   * @param outFile - The absolute file path of the log file.
   * @param rootName - The name of the root element for the xml log file.
   * @throws Exception - If any type of error occurs in trying to open the
   *                     log file for writing.
   */
    private static void openLog(String outFile, String rootName) throws Exception
    {
        s_rootName = rootName;
        s_log=new PrintStream(new FileOutputStream(outFile), true, "UTF-8");
        s_log.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        s_log.println("<" + s_rootName + ">");
    }

    /**
     * <p>Closes the log file.</p>
     *
     * @throws Exception - If any type of error occurs in closing the log file.
     */
    private static void closeLog() throws Exception
    {
        s_log.println("</" + s_rootName + ">");
        s_log.close();
        s_log=null;
    }

    /**
     * <p>Displays the command-line syntax.</p>
     *
     * @param errMessage - The error message to be displayed.
     */
    public static void showUsage(String errMessage)
    {
        System.out.println("Error: " + errMessage);
        System.out.println("");
        System.out.println("Usage: AutoModify host port username password "
            + "directives-filepath log-filepath validate-only-option");
    }


    public static void main(String[] args)
    {

      String logFilePath = null;
      String directivesFilePath = null;
      String hostName = null;
      String username = null;
      String password = null;
      int portNum = 8080;
      boolean validateOnly = true;

        try
        {
            if (args.length!=7)
            {
                AutoModify.showUsage("You must provide seven arguments.");
            } else
            {
                hostName = args[0];
                portNum = Integer.parseInt(args[1]);
                username = args[2];
                password = args[3];
                directivesFilePath = args[4];
                logFilePath = args[5];
                validateOnly = new Boolean(args[6]).booleanValue();
                if (new File(directivesFilePath).exists())
                {
                  System.out.println("\nCONNECTING to Fedora server....");
                  AutoModify am = new AutoModify(hostName, portNum, username, password);
                  if(validateOnly) {
                      System.out.println("\n----- VALIDATING DIRECTIVES FILE ONLY -----\n");
                  } else {
                      System.out.println("\n----- PROCESSING DIRECTIVES FILE -----\n");
                  }
                  am.modify(directivesFilePath, logFilePath, validateOnly);
                } else
                {
                  AutoModify.showUsage("Directives input file does not exist: "
                      + directivesFilePath + " .");
                }
            }
        } catch (Exception e)
        {
            AutoModify.showUsage(e.getClass().getName() + " - "
                + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
        }
    }

}