/*
 * @(#) BasicCursorFactory.java 1.8 - last change made 04/05/01
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
 * 
 */

package javax.help.plaf.basic;

import java.io.*;
import javax.swing.ImageIcon;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

/**
 * Factory object that can vend cursors appropriate for the basic L & F.
 * <p>
 *
 * @version 1.8	04/05/01
 * @author Roger D. Brinkley
 */
public class BasicCursorFactory {
    private static Cursor onItemCursor;

    private static BasicCursorFactory theFactory;

    /**
     * Returns the OnItem cursor.
     */
    public static Cursor getOnItemCursor() {
	debug ("getOnItemCursor");
	if (theFactory == null) {
	    theFactory = new BasicCursorFactory();
	}
	if (onItemCursor == null) {
	    onItemCursor = theFactory.createCursor("OnItemCursor");
	}
	return onItemCursor;
    }

    private Cursor createCursor(String name) {
 	String gifFile = null;
	String hotspot = null;
	ImageIcon icon;
	Point point;

	debug("CreateCursor for " + name);

	// Get the Property file
	InputStream is = getClass().getResourceAsStream("images/" + name + ".properties");
	if (is == null) {
	    debug(getClass().getName() + "/" + 
			       "images/" + name + ".properties" + " not found.");
	    return null;
	}
	try {
	    ResourceBundle resource = new PropertyResourceBundle(is);
	    gifFile = resource.getString("Cursor.File");
	    hotspot = resource.getString("Cursor.HotSpot");
	} catch (MissingResourceException e) {
	    debug(getClass().getName() + "/" + 
			       "images/" + name + ".properties" + " invalid.");
	    return null;
	} catch (IOException e2) {
	    debug(getClass().getName() + "/" + 
			       "images/" + name + ".properties" + " invalid.");
	    return null;
	}

	// Create the icon
	byte[] buffer = null;
	try {
	    /* Copies resource into a byte array.  This is
	     * necessary because several browsers consider
	     * Class.getResource a security risk because it
	     * can be used to load additional classes.
	     * Class.getResourceAsStream returns raw
	     * bytes, which JH can convert to an image.
	     */
	    InputStream resource = 
		getClass().getResourceAsStream(gifFile);
	    if (resource == null) {
		debug(getClass().getName() + "/" + 
				   gifFile + " not found.");
		return null; 
	    }
	    BufferedInputStream in = 
		new BufferedInputStream(resource);
	    ByteArrayOutputStream out = 
		new ByteArrayOutputStream(1024);
	    buffer = new byte[1024];
	    int n;
	    while ((n = in.read(buffer)) > 0) {
		out.write(buffer, 0, n);
	    }
	    in.close();
	    out.flush();
	    
	    buffer = out.toByteArray();
	    if (buffer.length == 0) {
		debug("warning: " + gifFile + 
				   " is zero-length");
		return null;
	    }
	} catch (IOException ioe) {
	    debug(ioe.toString());
	    return null;
	}

	icon = new ImageIcon(buffer);

	// create the point
	int k = hotspot.indexOf(',');
	point = new Point(Integer.parseInt(hotspot.substring(0,k)),
			  Integer.parseInt(hotspot.substring(k+1)));
	
	debug ("Toolkit fetching cursor");
	try {
	    return Toolkit.getDefaultToolkit().createCustomCursor 
		(icon.getImage(), point, name);
	} catch (NoSuchMethodError err) {
	    //	    return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	    return null;
	}
    }

    /**
     * For printf debugging.
     */
    private static final boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("BasicCursorFactory: " + str);
        }
    }

}
