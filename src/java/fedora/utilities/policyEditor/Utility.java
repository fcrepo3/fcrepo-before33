package fedora.utilities.policyEditor;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/*
 * Created on May 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author diglib
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Utility
{
    static XmlPullParser xr = null;
    static Object semaphore = null;

    /**
     * 
     */
    private Utility()
    {
        // TODO Auto-generated constructor stub
    }
    
    public static String camelCasify(String input)
    {
        Matcher m = Pattern.compile("[- ][a-z]").matcher(input);
        String output = "";
        int start = 0;
        while (m.find())
        {
            output = output + input.substring(start, m.start()) + 
                     input.substring(m.start(), m.end()).toUpperCase();
            start = m.end();
        }
        output = output + input.substring(start);

        output = output.replaceAll(" ", "");
        return(output);
    }

    public static Component makeLabeledTextField(String label)
    {
        JPanel panel = new JPanel()
        {
            public String toString()
            {
                return(this.getComponent(1).toString());
            }
        };
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(new JTextField(), BorderLayout.CENTER);
        return(panel);      
    }
    
    public static FedoraNode findNodeByShortName(FedoraNode node, String shortname)
    {
        if (node.getShortname().equals(shortname))
        {
            return(node);
        }
        else 
        {
            for (int i = 0 ; i < node.getChildren().length; i++)
            {
                FedoraNode match = findNodeByShortName((FedoraNode)(node.getChildren()[i]), shortname);
                if (match != null)  return(match);
            }
        }
        return(null);
    }
    
    public static String getParamFromConfig(String fedoraHome, String configDir, String role, String paramName)
    {
        File dir = new File(fedoraHome, configDir);
        File config = new File(dir, "fedora.fcfg");
        return(readParamFromFile(role, paramName, config));
    }   
               
    private static String readParamFromInputStream(String role, String paramName, InputStream stream, String name)
    {
        if (xr == null)
        {
            xr = new KXmlParser();
            semaphore = new Object();
        }
        System.out.println("Opening metadata url " + name);
        try {           
            synchronized (semaphore)
            {
                xr.setInput(new InputStreamReader(stream));
                return parseConfig(xr, role, paramName);
            }
         }
        catch (FileNotFoundException e)
        {
            System.out.println("Unable to open URL: " + name);
        }
        catch (IOException ioe)
        {
            System.out.println("Problems reading file: " + name);
        }
        catch (XmlPullParserException ioe)
        {
            System.out.println("Error parsing file: " + name);
        }
        return(null);
    }
                    
    static String readParamFromFile(String role, String paramName, File file)
    {
        try {
            return(readParamFromInputStream(role, paramName, new FileInputStream(file), file.toString()));
        } 
        catch (FileNotFoundException e)
        {
            System.out.println("Problems Opening File: " + file.toString());
            return(null);
        }
    }
   
    protected static String parseConfig(XmlPullParser xr, String role, String paramName)
                               throws XmlPullParserException, IOException
    {
        int eventType = xr.getEventType();
        boolean insideCorrectRole = false;
        String returnVal = null;
        while (eventType != XmlPullParser.END_DOCUMENT) 
        {
            if(eventType == XmlPullParser.START_DOCUMENT) 
            {
            } 
            else if(eventType == XmlPullParser.END_DOCUMENT) 
            {
            } 
            else if(eventType == XmlPullParser.START_TAG) 
            {
                if (xr.getName().equals("module"))
                {
                    String moduleRole = xr.getAttributeValue(null, "role");
                    if (role.equals(moduleRole))
                    {
                        insideCorrectRole = true;
                    }
                }
                if (insideCorrectRole && xr.getName().equals("param"))
                {
                    String moduleParamName = xr.getAttributeValue(null, "name");
                    if (paramName.equals(moduleParamName))
                    {
                        returnVal = xr.getAttributeValue(null, "value");
                    }
                }
            } 
            else if(eventType == XmlPullParser.END_TAG) 
            {
                if (xr.getName().equals("module"))
                {
                    String moduleRole = xr.getAttributeValue(null, "role");
                    if (role.equals(moduleRole))
                    {
                        insideCorrectRole = false;
                    }
                }
            } 
            else if(eventType == XmlPullParser.TEXT) 
            {
            }
            eventType = xr.next();
        }
        return(returnVal);
    }    
        
}
