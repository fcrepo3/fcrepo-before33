/*
 * @(#) SwingHelpUtilities.java 1.2 - last change made 05/04/01
 *
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */

package javax.help;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.plaf.ComponentUI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Provides a number of utility functions:
 *
 * Support for Beans, mapping from a Bean class to its HelpSet and to
 * its ID.
 * Support for LAF changes.
 * Support for finding localized resources.
 *
 * This class has no public constructor.
 *
 * @author Eduardo Pelegri-Llopart
 * @author Roger D. Brinkley
 * @version	1.2	05/04/01
 */

public class SwingHelpUtilities implements PropertyChangeListener {

    private static UIDefaults uiDefaults = null;
    private static SwingHelpUtilities myLAFListener = new SwingHelpUtilities();
    static {
        installUIDefaults();
    }
    
    //=========
    /**
     * LAF support
     */

    /**
     * The PropertyChange method is used to track changes to LookAndFeel
     * via the "lookAndFeel" property.
     */

    public void propertyChange(PropertyChangeEvent event) {
	String changeName = event.getPropertyName();
	if (changeName.equals("lookAndFeel")) {
	    installLookAndFeelDefaults();
        }
    }

    /**
     * Installs UIDefaults for Help components and installs "lookAndFeel"
     * property change listener.
     */
    static void installUIDefaults() {
        UIDefaults table = UIManager.getLookAndFeelDefaults();
        if (uiDefaults != table) {
            uiDefaults = table;
            UIManager.removePropertyChangeListener(myLAFListener);
            installLookAndFeelDefaults();
            UIManager.addPropertyChangeListener(myLAFListener);
        }
    }

    /**
     * Adds look and feel constants for Help components into UIDefaults table.
     */
    static void installLookAndFeelDefaults() {
        LookAndFeel lnf = UIManager.getLookAndFeel();
        UIDefaults table = UIManager.getLookAndFeelDefaults();

	debug("installLookAndFeelDefaults - " + lnf);

        if ((lnf != null) && (table != null)) {
	    if (lnf.getID().equals("Motif")) {
		installMotifDefaults(table);
	    } else if (lnf.getID().equals("Windows")) {
		installWindowsDefaults(table);
	    } else {
		// Default
		installMetalDefaults(table);
	    }
	}
	debug ("verifing UIDefaults; HelpUI=" + table.getString("HelpUI"));

    }

    /**
     * Dynamically invoke a cursor factory to get a cursor.
     */
    private static Object createIcon(String factoryName,
				     String method) {
	ClassLoader loader = HelpUtilities.class.getClassLoader();
	try {
	    Class types[] = new Class[0];
	    Object args[] = new Object[0];
	    Class klass;

	    if (loader == null) {
		klass = Class.forName(factoryName);
	    } else {
		klass = loader.loadClass(factoryName);
	    }
	    Method m = klass.getMethod(method, types);
	    Object back = m.invoke(null, args);
	    return back;
	} catch (Exception ex) {
	    return null;
	}
    }

    static Object basicOnItemCursor = new UIDefaults.LazyValue() {
	public Object createValue(UIDefaults table) {
	    return createIcon("javax.help.plaf.basic.BasicCursorFactory",
			      "getOnItemCursor");
	}
    };

    /**
     * The basic LAF does what we need.
     */
    static private void installBasicDefaults(UIDefaults table) {
	String basicPackageName = "javax.help.plaf.basic.";

	Object[] uiDefaults = {
	                "HelpUI", basicPackageName + "BasicHelpUI",
	    "HelpTOCNavigatorUI", basicPackageName + "BasicTOCNavigatorUI",
	  "HelpIndexNavigatorUI", basicPackageName + "BasicIndexNavigatorUI",
	 "HelpSearchNavigatorUI", basicPackageName + "BasicSearchNavigatorUI",
	   "HelpContentViewerUI", basicPackageName + "BasicContentViewerUI",
	      "HelpOnItemCursor", basicOnItemCursor
	};

	table.putDefaults(uiDefaults);
    }

    /**
     * If we had any LAF-specific classes, they would be invoked from here.
     */
    static private void installMetalDefaults(UIDefaults table) {
	installBasicDefaults(table);
    }

    static private void installWindowsDefaults(UIDefaults table) {
	installBasicDefaults(table);
    }

    static private void installMotifDefaults(UIDefaults table) {
	installBasicDefaults(table);
    }

    /**
     * Create an Icon from a given resource.
     * 
     * This works uisng getResourceAsStream() because several browsers do not
     * correctly implement getResource().
     *
     * This method may change...
     */

    public static ImageIcon getImageIcon(final Class baseClass,
					 final String image) {
	if (image == null) {
	    return null;
	}
	final byte[][] buffer = new byte[1][];
	try {
	    InputStream resource = baseClass.getResourceAsStream(image);
	    if (resource == null) {
		return null; 
	    }
	    BufferedInputStream in = new BufferedInputStream(resource);
	    ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
	    buffer[0] = new byte[1024];
	    int n;
	    while ((n = in.read(buffer[0])) > 0) {
		out.write(buffer[0], 0, n);
	    }
	    in.close();
	    out.flush();
	    buffer[0] = out.toByteArray();
	} catch (IOException ioe) {
	    System.err.println(ioe.toString());
	    return null;
	}
	if (buffer[0] == null) {
	    System.err.println(baseClass.getName() + "/" + 
			       image + " not found.");
	    return null;
	}
	if (buffer[0].length == 0) {
	    System.err.println("warning: " + image + 
			       " is zero-length");
	    return null;
	}
	
	return new ImageIcon(buffer[0]);
    }

    /**
     * Debug support
     */

    private static final boolean debug = false;
    private static void debug(Object msg1) {
	if (debug) {
	    System.err.println("GUIHelpUtilities: "+msg1);
	}
    }
}
