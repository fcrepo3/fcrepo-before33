/*
 * @(#) JHSecondaryViewerBeanInfo.java 1.6 - last change made 01/29/99
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

package com.sun.java.help.impl;
import java.beans.*;

/**
 * This class provides information about getter/setter methods within 
 * JHSecondaryWindow. It is usefull for reflection.
 * @see JHSecondaryViewer
 *
 * @author Roger D. Brinkley
 * @version	1.6	01/29/99
 */
public class JHSecondaryViewerBeanInfo extends SimpleBeanInfo {

    public JHSecondaryViewerBeanInfo() {
    }
    
    public PropertyDescriptor[] getPropertyDescriptors() {
	PropertyDescriptor back[] = new PropertyDescriptor[15];
	try {
	    back[0] = new PropertyDescriptor("content", JHSecondaryViewer.class);
	    back[1] = new PropertyDescriptor("id", JHSecondaryViewer.class);
	    back[2] = new PropertyDescriptor("viewerName", JHSecondaryViewer.class);
	    back[3] = new PropertyDescriptor("viewerActivator", JHSecondaryViewer.class);
	    back[4] = new PropertyDescriptor("viewerStyle", JHSecondaryViewer.class);
	    back[5] = new PropertyDescriptor("viewerLocation", JHSecondaryViewer.class);
	    back[6] = new PropertyDescriptor("viewerSize", JHSecondaryViewer.class);
	    back[7] = new PropertyDescriptor("iconByName", JHSecondaryViewer.class);
	    back[8] = new PropertyDescriptor("iconByID", JHSecondaryViewer.class);
	    back[9] = new PropertyDescriptor("text", JHSecondaryViewer.class);
	    back[10] = new PropertyDescriptor("textFontFamily", JHSecondaryViewer.class);
	    back[11] = new PropertyDescriptor("textFontSize", JHSecondaryViewer.class);
	    back[12] = new PropertyDescriptor("textFontWeight", JHSecondaryViewer.class);
	    back[13] = new PropertyDescriptor("textFontStyle", JHSecondaryViewer.class);
	    back[14] = new PropertyDescriptor("textColor", JHSecondaryViewer.class);
	    return back;
	} catch (Exception ex) {
	    return null;
	}
    }
}
