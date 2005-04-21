package fedora.client.export;

import java.io.*;
import javax.swing.*;

import fedora.client.Administrator;
import fedora.client.FTypeDialog;
import fedora.client.utility.export.Export;

/**
 * <p><b>Title:</b> ExportDialog.java</p>
 * <p><b>Description: Class to initiate an interactive export dialog for
 * use by Fedora Administrator.
 * 
 * This class calls AutoExporter.class which is reponsible for making
 * the API-M SOAP calls for the export.
 * 
 */

public class ExportDialog {

    public static int ONE=0;
    public static int MULTI=1;

    // launch interactively via Administrator.java
    public ExportDialog(int kind) {
        try {
            JFileChooser browse=new JFileChooser(Administrator.getLastDir());
            browse.setDialogTitle("Export to which directory?");
            browse.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = browse.showOpenDialog(Administrator.getDesktop());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = browse.getSelectedFile();
                Administrator.setLastDir(file.getParentFile());
                ExportOptionsDialog optsDialog = new ExportOptionsDialog("Select Options for Export");
				if (optsDialog.getFormatSelection()!=null) {
	                if (kind==ONE) {
	                    String pid=JOptionPane.showInputDialog("Enter the PID of the object to export.");
	                    if (pid!=null && !pid.equals("")) {
	                        Export.one(Administrator.APIA, Administrator.APIM, pid, 
	                        		   optsDialog.getFormatSelection(), 
	                        		   optsDialog.getContextSelection(),
	                        		   file);
	                        JOptionPane.showMessageDialog(Administrator.getDesktop(),
	                            "Export succeeded.  PID='" + pid + "'.");
	                    }
	                } else {
	                    FTypeDialog dlg=new FTypeDialog();
	                    if (dlg.getResult()!=null) {
	                        String fTypes=dlg.getResult();
	                        long st=System.currentTimeMillis();
	                        String[] pids=Export.multi(Administrator.APIA, 
	                                            Administrator.APIM, 
	                                            fTypes,
												optsDialog.getFormatSelection(),
												optsDialog.getContextSelection(),
	                                            file);
	                        long et=System.currentTimeMillis();
	                        JOptionPane.showMessageDialog(Administrator.getDesktop(),
	                            "Export of " + pids.length + " objects finished.\n"
	                            + "Time elapsed: " + Export.getDuration(et-st));  
	                    }
	                }
				}
            }
        } catch (Exception e) {
            String msg=e.getMessage();
            if (msg==null) {
                msg=e.getClass().getName();
            }
        	Administrator.showErrorDialog(Administrator.getDesktop(), "Export Failure", 
        			msg, e);
        }
    }      
}