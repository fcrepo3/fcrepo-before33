package com.rolemodelsoft.drawlet;

/**
 * @(#)RelatedLocationListener.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 *
 * Permission to use, copy, demonstrate, or modify this software
 * and its documentation for NON-COMMERCIAL or NON-PRODUCTION USE ONLY and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies and all terms of license agreed to when downloading 
 * this software are strictly followed.
 *
 * RMS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. RMS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

import java.beans.PropertyChangeEvent;

/**
 * This interface defines the methods that an object desiring to relate itself to the location
 * of another object (through listening to that object) should answer.
 *
 * @version 	1.1.6, 12/28/98
 */

 public interface RelatedLocationListener {
	/**
	 * Called by subjects when their location changes.
	 *
	 * @param event PropertyChangeEvent
	 */
	void locationChanged(PropertyChangeEvent event);
	/**
	 * Called by subjects when their relationship to the receiver has changed.
	 *
	 * @param event PropertyChangeEvent
	 */
	void relationChanged(PropertyChangeEvent event);
	/**
	 * Called by subjects when their shape changes.
	 *
	 * @param event PropertyChangeEvent
	 */
	void shapeChanged(PropertyChangeEvent event);
	/**
	 * Called by subjects when their size changes.
	 *
	 * @param event PropertyChangeEvent
	 */
	void sizeChanged(PropertyChangeEvent event);
}
