package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)AbstractInputEventHandler.java
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
import java.awt.event.*;

/**
 * Provides a default implementation of <code>InputEventHandler</code>.
 * @version 	1.1.6, 12/28/98
 */
public class AbstractInputEventHandler implements InputEventHandler {
	/**
	 * Create a new AbstractInputEventHandler.
	 */
	public AbstractInputEventHandler() {
		super();
	}
	/**
	 * Called when a key is pressed.
	 * 
	 * @param e the KeyEvent to handle
	 */
	public void keyPressed(KeyEvent e) {
	}
	/**
	 * Called when a key is released.
	 * 
	 * @param e the KeyEvent to handle
	 */
	public void keyReleased(KeyEvent e) {
	}
	/**
	 * Called when a key is typed.
	 * 
	 * @param e the KeyEvent to handle
	 */
	public void keyTyped(KeyEvent e) {
	}
	/**
	 * Called when the mouse is clicked.
	 * 
	 * @param e the MouseEvent to handle
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2)
			mouseDoubleClicked(e);
		else
			mouseSingleClicked(e);
	}
	/**
	 * Called when the mouse is double-clicked.
	 * 
	 * @param e the MouseEvent to handle
	 */
	protected void mouseDoubleClicked(MouseEvent e) {
	}
	/**
	 * Called when the mouse is dragged.
	 * 
	 * @param e the MouseEvent to handle
	 */
	public void mouseDragged(MouseEvent e) {
	}
	/**
	 * Called when the mouse has entered.
	 * 
	 * @param e the MouseEvent to handle
	 */
	public void mouseEntered(MouseEvent e) {
	}
	/**
	 * Called when the mouse has exited.
	 * 
	 * @param e the MouseEvent to handle
	 */
	public void mouseExited(MouseEvent e) {
	}
	/**
	 * Called when the mouse is moved.
	 * 
	 * @param e the MouseEvent to handle
	 */
	public void mouseMoved(MouseEvent e) {
	}
	/**
	 * Called when the mouse is pressed.
	 * 
	 * @param e the MouseEvent to handle
	 */
	public void mousePressed(MouseEvent e) {
	}
	/**
	 * Called when the mouse is released.
	 * 
	 * @param e the MouseEvent to handle
	 */
	public void mouseReleased(MouseEvent e) {
	}
	/**
	 * Called when the mouse is single-clicked.
	 * 
	 * @param e the MouseEvent to handle
	 */
	protected void mouseSingleClicked(MouseEvent e) {
	}
}
