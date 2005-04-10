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
import fedora.server.utilities.StreamUtility;


/**
 *
 * <p><b>Title:</b> BatchModifyValidate.java</p>
 * <p><b>Description:</b> A GUI interface for entering info required to validate
 * a file of modify directives against the batchModify.xsd XML Schema. The
 * batch modify utility has validation turned on so pre-validation is not
 * necessary. It is provided as a means to pre-validate a modify directives
 * file prior to running  it through the batch modify utility.
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

public class BatchModifyValidate
{

    private static String s_rootName = null;
    private static String s_logPath = null;
    private static PrintStream s_log = null;

    /**
     * <p>Constructor for the class.</p>
     */
    public BatchModifyValidate() {
        InputStream in = null;
        BatchModifyValidator bmv = null;
        File file = null;
        long st=System.currentTimeMillis();
        long et=0;

        try {
            JFileChooser browse=new JFileChooser(Administrator.getLastDir());
            browse.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int returnVal = browse.showOpenDialog(Administrator.getDesktop());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = browse.getSelectedFile();
                int n = JOptionPane.showConfirmDialog(Administrator.getDesktop(),
                    "Validate the Modify Directives in file: \n" + file.getAbsoluteFile()+" ?\n",
                    "Validate Modify Directives File",
                    JOptionPane.YES_NO_OPTION);
                if (n==JOptionPane.YES_OPTION) {
                    Administrator.setLastDir(file);
                    openLog("validate-modify-directives");
                    in = new FileInputStream(file);
                    bmv = new BatchModifyValidator(in, s_log);
                }
            }
        } catch (Exception e) {
        	Administrator.showErrorDialog(Administrator.getDesktop(), "Error Parsing Directives File.", 
        			e.getClass().getName()
	                + " - " + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()), e);        	
        } finally {
            try {
                if (in != null)
                    in.close();
                if (s_log!=null && bmv!=null) {
                    et=System.currentTimeMillis();
                    if (bmv.isValid()) {
                        JOptionPane.showMessageDialog(Administrator.getDesktop(),
                            "Modify Directives file: \n"+file.getAbsoluteFile()
                            + "\nis Valid !"
                            + "\nTime elapsed: " + getDuration(et-st),
                            "Directives File Valid",
                            JOptionPane.INFORMATION_MESSAGE);
                        closeLog();
                        return;
                    }
                    JOptionPane.showMessageDialog(Administrator.getDesktop(),
                            +bmv.getErrorCount()+" XML validation errors found in Modify Directives file.\n"
                            + "See log file for details.\n"
                            + "Time elapsed: " + getDuration(et-st));
                    s_log.println("  <summary>");
                    s_log.println("    "+StreamUtility.enc(bmv.getErrorCount()
                            + " XML validation errors found in Modify Directives file.\n"
                            + "    See log file for details.\n"
                            + "    Time elapsed: " + getDuration(et-st)));
                    s_log.println("  </summary>");
                    closeLog();
                    int n = JOptionPane.showConfirmDialog(Administrator.getDesktop(),
                        "A detailed log file was created at\n"
                        + s_logPath + "\n\n"
                        + "View it now?",
                        "View Validation Log?",
                        JOptionPane.YES_NO_OPTION);
                    if (n==JOptionPane.YES_OPTION) {
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
                        try {
                            viewFrame.setSelected(true);
                        } catch (java.beans.PropertyVetoException pve) {
                            // ignore
                        }
                    }
                }
            } catch (Exception e) {
            	e.printStackTrace();
            	Administrator.showErrorDialog(Administrator.getDesktop(), "Error", 
            			e.getClass().getName()
	                    + " - " + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()), e);            	
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

    /**
     * <p>Initializes the log file for writing.</p>
     *
     * @param rootName - The name of the root element for the xml log file.
     * @throws Exception - If any type of error occurs in trying to open the
     *                     log file for writing.
     */
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

    /**
     * <p>Closes the log file.</p>
     *
     * @throws Exception - If any type of error occurs in closing the log file.
     */
    private static void closeLog() throws Exception {
        s_log.println("</" + s_rootName + ">");
        s_log.close();
    }

    /**
     * <p>Converts file into string.</p>
     *
     * @param path - The absolute file path of the file.
     * @return - The contents of the file as a string.
     * @throws Exception - If any type of error occurs during the conversion.
     */
    private static String fileAsString(String path) throws Exception {
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






