package com.rolemodelsoft.drawlet.shapes;

/**
 * @(#)RightHandle.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 * Copyright (c) 1997 Knowledge Systems Corporation (KSC). All Rights Reserved.
 *
 * Permission to use, copy, demonstrate, or modify this software
 * and its documentation for NON-COMMERCIAL or NON-PRODUCTION USE ONLY and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies and all terms of license agreed to when downloading 
 * this software are strictly followed.
 *
 * RMS AND KSC MAKE NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. NEITHER RMS NOR KSC SHALL BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
 
import com.rolemodelsoft.drawlet.*;
import com.rolemodelsoft.drawlet.basics.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * This class provides a handle that shows up at the right of a figure
 * and resizes the figure accordingly as it is dragged.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class RightHandle extends BoundsHandle {

	/** 
	 * Constructs and initializes a new instance of a handle which can 
	 * affect the figure's bounds by changing its right.
	 *
	 * @param figure the figure whose bounds we may wish to change.
	 */
	public RightHandle(Figure figure) {
		super(figure);
	}
	/** 
	 * Answer the default/initial locator.
	 * 
	 * @param figure the figure
	 * @return	the default/initial Locator
	 */
	protected Locator defaultLocator(Figure figure) {
		return new FigureRelativePoint(figure,1.0,0.5);
	}
	/**
	 * Called if the mouse is dragged (the mouse button is down).
	 * Resize the figure as appropriate.
	 *
	 * @param evt the event
	 */
	public void mouseDragged(MouseEvent evt) {
		Rectangle bounds = figure.getBounds();	
		resize(Math.max(getX(evt) - bounds.x, 1), bounds.height);
		super.mouseDragged(evt);
	}
}
