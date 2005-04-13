package fedora.client.batch;

import java.util.Properties;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JOptionPane;

import fedora.client.Administrator;

/**
 *
 * <p><b>Title:</b> AutoBatchBuild.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class AutoBatchBuild {
    private Properties batchProperties = new Properties();

    public AutoBatchBuild(String objectTemplate, String objectSpecificDir,
        String objectDir, String logFile, String logFormat, String objectFormat) throws Exception {

        this.batchProperties.setProperty("template", objectTemplate);
        this.batchProperties.setProperty("merge-objects", "yes");
        this.batchProperties.setProperty("specifics", objectSpecificDir);
        this.batchProperties.setProperty("objects", objectDir);
        this.batchProperties.setProperty("pids-format", logFormat);
        this.batchProperties.setProperty("ingested-pids", logFile);
        this.batchProperties.setProperty("object-format", objectFormat);

        BatchTool batchTool = new BatchTool(this.batchProperties, null, null);
        batchTool.prep();
        batchTool.process();
    }

    public static final void main(String[] args) throws Exception {
        boolean errors = false;
        String objectFormat = null;
        if (args.length == 5) {
            if (!new File(args[0]).exists() && !new File(args[0]).isFile()) {
                System.out.println("Specified object template file path: \""
                                   + args[0] + "\" does not exist.");
                errors = true;
            }
            if (!new File(args[1]).isDirectory()) {
                System.out.println("Specified object specific directory: \""
                                   + args[1] + "\" is not directory.");
                errors = true;
            }
            if (!new File(args[2]).isDirectory()) {
                System.out.println("Specified object directory: \""
                                   + args[2] + "\" is not a directory.");
                errors = true;
            }
            if (!args[4].equals("xml") && !args[4].equals("text")) {
                System.out.println("Format for log file must must be either: \""
                                   + "\"xml\"  or  \"txt\"");
                errors = true;
            } 
      	    // Verify format of template file to see if it is a METS or FOXML template
      	    BufferedReader br = new BufferedReader(new FileReader(args[0]));
      	    String line;
      	    while ((line=br.readLine()) != null) {
      	        System.out.println(line);
      	        if(line.indexOf("<foxml:")!=-1) {
      	            objectFormat = "foxml1.0";
      	        		break;
      	        }
      	        if(line.indexOf("<METS:")!=-1) {
      	            objectFormat = "metslikefedora1";
      	        		break;
      	        }      	        
      	    }
      	    br.close();
      	    br=null;
      	    
      	    if (objectFormat==null) {
      	        errors = true;
      	    }
            
            if (!errors) {
                System.out.println("\n*** Format of template files is: "+objectFormat+" . Generated objects will be in "+objectFormat+" format.\n");
                AutoBatchBuild autoBatch = new AutoBatchBuild(args[0], args[1], args[2], args[3], args[4], objectFormat);
            }
        } else {
            
            if (objectFormat==null && args.length==5) {
                System.out.println("\nUnknown format for template file.\n"
                    + "Template file must either be METS or FOXML.\n");
            } else {
		            System.out.println("\n**** Wrong Number of Arguments *****\n");
		            System.out.println("AutoBatchBuild requires 5 arguments.");
		            System.out.println("merge-objects=yes");
		            System.out.println("(1) - full path to object template file");
		            System.out.println("(2) - full path to object specific directory");
		            System.out.println("(3) - full path to object directory");
		            System.out.println("(4) - full path to log file");
		            System.out.println("(5) - format of log file (xml or text)");
            }
        }
    }
}