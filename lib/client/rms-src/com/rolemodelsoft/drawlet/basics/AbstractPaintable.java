package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)AbstractPaintable.java
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

import com.rolemodelsoft.drawlet.*;
import java.awt.*;

/**
 * Provides a default implementation of <code>Paintable</code>.
 */

public class AbstractPaintable implements Paintable {
/**
 * Creates a new AbstractPaintable.
 */
public AbstractPaintable() {
	super();
}
	/** 
	 * Returns the bottommost coordinate of the receiver.
	 * 
	 * @return	an integer representing the bottommost coordinate of the receiver
	 */
	public int getBottom() {
		return getTop() + getHeight();
	}
/**
 * Answers the rectangular space covered by the receiver.
 *
 * @return a <code>Rectangle</code> representing the bounds.
 */
public java.awt.Rectangle getBounds() {
	return null;
}
	/** 
	 * Returns the height of the receiver.
	 * 
	 * @return	an integer representing the height of the receiver.
	 */
	public int getHeight() {
		return getSize().height;
	}
	/** 
	 * Returns the leftmost coordinate of the receiver.
	 * 
	 * @return	an integer representing the leftmost coordinate of the receiver
	 */
	public int getLeft() {
		return getBounds().x;
	}
	/** 
	 * Returns the rightmost coordinate of the receiver.
	 * 
	 * @return	an integer representing the rightmost coordinate of the receiver
	 */
	public int getRight() {
		return getLeft() + getWidth();
	}
	/** 
	 * Returns the current size of the receiver.
	 *
	 * @return	a Dimension corresponding to the current size of the receiver.
	 * @see #getBounds
	 */ 
	public Dimension getSize() {
		Rectangle myBounds = getBounds();
		return new Dimension(myBounds.width,myBounds.height);
	}
	/** 
	 * Returns the topmost coordinate of the receiver.
	 * 
	 * @return	an integer representing the topmost coordinate of the receiver.
	 */
	public int getTop() {
		return getBounds().y;
	}
	/** 
	 * Returns the width of the receiver.
	 * 
	 * @return	an integer representing the width of the receiver.
	 */
	public int getWidth() {
		return getSize().width;
	}
/**
 * Tells the receiver to paint itself.
 *
 * @param g the <code>Graphics</code> object in which to paint.
 */
public void paint(java.awt.Graphics g) {
}
}
