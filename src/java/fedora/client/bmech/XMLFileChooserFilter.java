package fedora.client.bmech;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 *
 * <p><b>Title:</b> XMLFileChooserFilter.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class XMLFileChooserFilter extends FileFilter
{
    public XMLFileChooserFilter()
    {
    }
    //Accept all directories and all xml files
    public boolean accept(File f)
    {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals("xml")) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription()
    {
        return "XML Files";
    }

    public String getExtension(File f)
    {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1)
        {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
  }