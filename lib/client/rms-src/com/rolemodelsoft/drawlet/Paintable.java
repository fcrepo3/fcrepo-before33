package com.rolemodelsoft.drawlet;

/**
 * @(#)Paintable.java
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

import java.awt.*;

/**
 * @version 	1.1.6, 12/28/98
 */

 public interface Paintable {
	/** 
	 * Returns the bottommost coordinate of the receiver.
	 *
	 * @return an integer representing the bottommost coordinate of the receiver
	 */
	public abstract int getBottom();
	/** 
	 * Returns the current bounds of the receiver.
	 * 
	 * @return	a Rectangle representing the current bounds
	 */
	public abstract Rectangle getBounds();
	/** 
	 * Returns the height of the receiver.
	 *
	 * @return an integer representing the height of the receiver
	 */
	public abstract int getHeight();
	/** 
	 * Returns the leftmost coordinate of the receiver.
	 *
	 * @return an integer representing the leftmost coordinate of the receiver
	 */
	public abstract int getLeft();
	/** 
	 * Returns the rightmost coordinate of the receiver.
	 *
	 * @return an integer representing the rightmost coordinate of the receiver
	 */
	public abstract int getRight();
	/** 
	 * Returns the current size of the receiver.
	 * @see #setSize
	 *
	 * @return a Dimension representing the current size of the receiver
	 */
	public abstract Dimension getSize();
	/** 
	 * Returns the topmost coordinate of the receiver.
	 *
	 * @return an integer representing the topmost coordinate of the receiver
	 */
	public abstract int getTop();
	/** 
	 * Returns the width of the receiver.
	 *
	 * @return an integer representing the width of the receiver
	 */
	public abstract int getWidth();
	/** 
	 * Paints the receiver.
	 * 
	 * @param g the Graphics object to use for painting
	 */
	public abstract void paint(Graphics g);
}
