package fedora.client.batch;

import java.awt.Font;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.PrintStream;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import fedora.client.Administrator;
import fedora.client.batch.BatchModifyParser;
import fedora.client.batch.BatchModThread;
import fedora.server.utilities.StreamUtility;
import fedora.server.management.FedoraAPIM;

/**
 *
 * <p><b>Title:</b> BatchModify.java</p>
 * <p><b>Description:</b> A GUI interface for entering info required to perform
 * a batch modify that consists of a file containing the modify directives to
 * be processed. A log file is generated and saved in the client logs directory
 * detailing the events that occured during processing.
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author rlw@virginia.edu
 * @version $Id $
 */

public class BatchModify
{

private static String s_rootName = null;
private static String s_logPath = null;
private static PrintStream s_log = null;
private static FedoraAPIM APIM = null;

  public BatchModify(FedoraAPIM APIM)
  {
    this.APIM = APIM;
    InputStream in = null;

    try
    {
        JFileChooser browse=new JFileChooser(Administrator.getLastDir());
        browse.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = browse.showOpenDialog(Administrator.getDesktop());
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
          File file = browse.getSelectedFile();
          int n = JOptionPane.showConfirmDialog(Administrator.getDesktop(),
              "Process modify directives in file: "+file.getAbsolutePath()+" ?",
              "Run Batch Modify?",
              JOptionPane.YES_NO_OPTION);
          if (n==JOptionPane.YES_OPTION)
          {
            Administrator.setLastDir(file);
            openLog("modify-batch");
            long st=System.currentTimeMillis();
            long et=System.currentTimeMillis();
            in = new FileInputStream(file);
            //Parser deactivated for now.
            //BatchModifyParser bmp = new BatchModifyParser(APIM, in, s_log);
            JOptionPane.showMessageDialog(Administrator.getDesktop(),
                //bmp.getSucceededCount() + " objects successfully ingested.\n"
                "0 modify directives successfully processed.\n"
                //+ bmp.getFailedCount() + " objects failed.\n"
                + "0 modify directives failed.\n"
                + "Time elapsed: " + getDuration(et-st));
            //   Details are in File->Advanced->STDOUT/STDERR window.");
          }
      }
    } catch (Exception e)
    {
      JOptionPane.showMessageDialog(Administrator.getDesktop(),
          e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    } finally
    {
      try
      {
        System.out.println("exited while loop");
        if (in != null)
        {
          in.close();
        }
        if (s_log!=null)
        {
          closeLog();
          int n = JOptionPane.showConfirmDialog(Administrator.getDesktop(),
              "A detailed log file was created at\n"
              + s_logPath + "\n\n"
              + "View it now?",
              "View Ingest Log?",
              JOptionPane.YES_NO_OPTION);
          if (n==JOptionPane.YES_OPTION)
          {
            JTextComponent textEditor=new JTextArea();
            textEditor.setFont(new Font("monospaced", Font.PLAIN, 12));
            textEditor.setText(fileAsString(s_logPath));
            textEditor.setCaretPosition(0);
            textEditor.setEditable(false);
            JInternalFrame viewFrame=new JInternalFrame("Viewing " + s_logPath, true, true, true, true);
            viewFrame.setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/standard/general/Edit16.gif")));
            viewFrame.getContentPane().add(new JScrollPane(textEditor));
            viewFrame.setSize(720,520);
            viewFrame.setVisible(true);
            Administrator.getDesktop().add(viewFrame);
            s_log=null;
            try
            {
              viewFrame.setSelected(true);
            } catch (java.beans.PropertyVetoException pve)
            {
              // ignore
            }
          }
        }
      } catch (Exception e)
      {
        JOptionPane.showMessageDialog(Administrator.getDesktop(),
            e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private static String getDuration(long millis) {
      long tsec=millis/1000;
      long h=tsec/60/60;
      long m=(tsec - (h*60*60))/60;
      long s=(tsec - (h*60*60) - (m*60));
      StringBuffer out=new StringBuffer();
      if (h>0) {
          out.append(h + " hour");
          if (h>1) out.append('s');
      }
      if (m>0) {
          if (h>0) out.append(", ");
          out.append(m + " minute");
          if (m>1) out.append('s');
      }
      if (s>0 || (h==0 && m==0)) {
          if (h>0 || m>0) out.append(", ");
          out.append(s + " second");
          if (s!=1) out.append('s');
      }
      return out.toString();
  }


    private static void openLog(String rootName) throws Exception {
        s_rootName=rootName;
        String fileName=s_rootName + "-" + System.currentTimeMillis() + ".xml";
        File outFile;
        String fedoraHome=System.getProperty("fedora.home");
        if (fedoraHome=="") {
            // to current dir
            outFile=new File(fileName);
        } else {
            // to client/log
            File logDir=new File(new File(new File(fedoraHome), "client"), "logs");
            if (!logDir.exists()) {
                logDir.mkdir();
            }
            outFile=new File(logDir, fileName);

        }
        s_logPath=outFile.getPath();
        s_log=new PrintStream(new FileOutputStream(outFile), true, "UTF-8");
        s_log.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        s_log.println("<" + s_rootName + ">");
    }

    private static void closeLog() throws Exception {
        s_log.println("</" + s_rootName + ">");
        s_log.close();
    }

    private static String fileAsString(String path)
            throws Exception {
        StringBuffer buffer = new StringBuffer();
        InputStream fis=new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        Reader in = new BufferedReader(isr);
        int ch;
        while ((ch = in.read()) > -1) {
            buffer.append((char)ch);
        }
        in.close();
        return buffer.toString();
    }

}