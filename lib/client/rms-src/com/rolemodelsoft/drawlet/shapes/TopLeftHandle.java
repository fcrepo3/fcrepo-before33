package com.rolemodelsoft.drawlet.shapes;

/**
 * @(#)TopLeftHandle.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 * Copyright (c) 1996 Knowledge Systems Corporation (KSC). All Rights Reserved.
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
 * This class provides a handle that shows up at the top left of a figure
 * and reshapes the figure accordingly as it is dragged.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class TopLeftHandle extends BoundsHandle {

	/** 
	 * Constructs and initializes a new instance of a handle which can 
	 * affect the figure's bounds by changing its top-left.
	 *
	 * @param figure the figure whose bounds we may wish to change.
	 */
	public TopLeftHandle(Figure figure) {
		super(figure);
	}
	/** 
	 * Answer the default/initial locator.
	 * 
	 * @param figure the figure
	 * @return	the default/initial Locator
	 */
	protected Locator defaultLocator(Figure figure) {
		return new FigureRelativePoint(figure,0.0,0.0);
	}
	/**
	 * Called if the mouse is dragged (the mouse button is down).
	 * Reshape the figure as appropriate.
	 *
	 * @param evt the event
	 */
	public void mouseDragged(MouseEvent evt) {
		Rectangle bounds = figure.getBounds();
		int legitX = Math.min(getX(evt), figure.getRight() - 1);
		int legitY = Math.min(getY(evt), figure.getBottom() - 1);
		reshape(legitX, legitY, bounds.width - (legitX - bounds.x), bounds.height - (legitY - bounds.y));
		super.mouseDragged(evt);
	}
}
