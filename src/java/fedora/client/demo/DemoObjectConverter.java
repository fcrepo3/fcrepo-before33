package fedora.client.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 *
 * <p><b>Title:</b> DemoObjectConverter.java</p>
 * <p><b>Description:</b> Goes through the Fedora demo objects directory
 * and changes all occurrences of the strings "localhost" and "8080" to
 * values supplied by the user in the calling arguments. This utility is
 * used to convert the original Fedora demo objects so that they will
 * function correctly when the Fedora server is configured to run on a
 * port other than 8080 and a sername other thanlocalhost.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class DemoObjectConverter
{
  private static String fedoraHome = "";
  private static String fromHostName = "localhost";
  private static String fromPortNum = "8080";
  private static String toHostName = "localhost";
  private static String toPortNum = "8080";
  private static String fromName = "localhost:8080";
  private static String toName = "localhost:8080";

  /**
   * <p> Constructor for DemoObjectConverter. Initializes class variables for
   * hostname, port number and fedoraHome.</p>
   *
   * @param fromHostName The host name to be changed from.
   * @param fromPortNum The port number to be changed from.
   * @param toHostName The host name to be changed to.
   * @param toPortNum The port number ot be changed to.
   * @param fedoraHome The installation directory for Fedora.
   */
  public DemoObjectConverter(String fromHostName, String fromPortNum,
      String toHostName, String toPortNum, String fedoraHome)
  {
    DemoObjectConverter.toHostName = toHostName;
    DemoObjectConverter.toPortNum = toPortNum;
    DemoObjectConverter.fromHostName = fromHostName;
    DemoObjectConverter.fromPortNum = fromPortNum;
    DemoObjectConverter.fedoraHome = fedoraHome;
    DemoObjectConverter.fromName = fromHostName+":"+fromPortNum;
    DemoObjectConverter.toName = toHostName+":"+toPortNum;
  }

  /**
   * <p> Shows argument list for application.</p>
   *
   * @param errMessage The message to be included with usage information.
   */
  public static void showUsage(String errMessage) {
      System.out.println("Error: " + errMessage);
      System.out.println("");
      System.out.println("Usage: DemoObjectConverter fromHostName fromPortNum "
          + "toHostName toPortNum fedoraHome");
  }

  /**
   * <p> Converts all Fedora demo objects by substituting hostname and port
   * numer supplied in calling arguments.</p>
   *
   * @param fedoraHome The location Fedora is installed.
   */
  public static void convert(String fedoraHome)
  {
    // Define the Fedora demo directory.
    File demoDir=new File(fedoraHome, "client/demo");
    if(demoDir.exists())
    {
      getFiles(demoDir);
    } else
    {
      System.out.println("ERROR: Unable to locate Demo Object Directory: "
          + demoDir.toString());
    }
  }

  /**
   * <p> Recursively traverse the specified directory and process each file.<.p>
   *
   * @param dir The directory to be traversed.
   */
  public static void getFiles(File dir)
  {
    File[] files = dir.listFiles();
    for (int i=0; i<files.length; i++)
    {
      if(files[i].isDirectory())
      {
          // Collect files to be scanned.
          getFiles(files[i]);
      } else
      {
        // Make hostname and port number substitutions.
        substitute(files[i]);
      }
    }

  }

  /**
   * <p> Substitute the hostname and port number supplied in calling arguments
   * with strings "localhost" and "8080" found in Fedora demo objects. Replaces
   * the original file with the edited version.</p>
   *
   * @param demoObject The Fedora demo object file to be edited.
   */
  public static void substitute(File demoObject)
  {
    try
    {
      BufferedReader in=new BufferedReader(new FileReader(demoObject));
      String tempFile = demoObject.toString()+"-temp";
      OutputStream os = new FileOutputStream(new File(tempFile));
      OutputStreamWriter out = new OutputStreamWriter(os, "UTF-8");
      String nextLine="";
      String newUrlStart;
      if ((toPortNum.equals("")) || (toPortNum.equals("80")))
      {
        newUrlStart="http://" + toHostName + "/";
      } else
      {
        newUrlStart="http://" + toHostName + ":" + toPortNum + "/";
      }
      String a="http://" + fromHostName;
      String urlStartNoPort=a + "/";
      String urlStartPort80=a + ":80" + "/";
      String urlStartWithPort=a + ":" + fromPortNum + "/";
      while (nextLine!=null)
      {
        nextLine=in.readLine();
        if (nextLine!=null)
        {
          if ((fromPortNum.equals("")) || (fromPortNum.equals("80")) )
          {
            nextLine = nextLine.replaceAll(urlStartNoPort, newUrlStart);
            nextLine = nextLine.replaceAll(urlStartPort80, newUrlStart);
          } else
          {
            nextLine = nextLine.replaceAll(urlStartWithPort, newUrlStart);
          }
          out.write(nextLine+"\n");
        }
      }
      in.close();
      out.close();
      // Remove original file.
      if(demoObject.delete())
      {
        File file = new File(tempFile);

        // Rename temp file with original file name.
        if(!file.renameTo(demoObject))
        {
          System.out.println("ERROR: unable to rename file: "+demoObject);
        } else
        {
          System.out.println("Replaced File: "+demoObject);
        }
      } else
      {
        System.out.println("ERROR: Unable to delete file: "+demoObject);
      }
    } catch (IOException ioe)
    {
      System.out.println("IO ERROR: "+ioe.getMessage());
    }
  }

  public static void main(String[] args)
  {
    try
    {
      if (args.length!=5)
      {
        DemoObjectConverter.showUsage("You must provide five arguments.");
      } else
      {
        String fromHostName=args[0];
        String fromPortNum=args[1];
        String toHostName=args[2];
        String toPortNum=args[3];
        String fedoraHome=args[4];
        DemoObjectConverter doc=new DemoObjectConverter(fromHostName, fromPortNum, toHostName, toPortNum, fedoraHome);
        DemoObjectConverter.convert(fedoraHome);
      }
    } catch (Exception e)
    {
      DemoObjectConverter.showUsage(e.getClass().getName() + " - "
          + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
    }
  }
}