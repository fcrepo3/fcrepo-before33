package com.rolemodelsoft.drawlet.awt;

/**
 * @(#)ImageButton.java
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
import java.awt.image.*;
import java.awt.event.*;

/**
 * A button which uses an image instead of a label.
 *
 * @version 	1.1.6, 12/30/98
 */

public class ImageButton extends Component implements MouseListener {
	/**
	 * The label (not visible) for this button.
	 */
	protected String command = getDefaultCommand();

	/**
	 * The image for this button.
	 */
	protected Image image = getDefaultImage();

	/**
	 * The actionListener member variable, for dispatching events.
	 */
	protected ActionListener actionListener = null;

	 /**
	  * Flag that is set when the mouse is pressed inside.
	  */
	protected boolean isPressed = false;

	 /**
	  * Flag that is set when the mouse is inside
	  */
	protected boolean isInside = false;

	 /**
	  * Flag that is set when the button should be highlighted.
	  */
	protected boolean isHighlighted = false;
	/**
	 * Create a new, default <code>ImageButton</code>.
	 */
	public ImageButton() {
		super();
		addMouseListener( this );
	}
	/**
	 * Create a new <code>ImageButton</code> and initalize it with the given <code>Image</code>.
	 *
	 * @param image the image to be used.
	 */
	public ImageButton( Image image ) {
		this();
		this.image = image;
		waitForImage( this, image );
	}
	/**
	 * Create a new <code>ImageButton</code> and initalize it with the given command.
	 *
	 * @param command the command that will be passed in action events.
	 */
	public ImageButton( String command ) {
		this();
		this.command = command;
	}
	/**
	 * Create a new <code>ImageButton</code> and initalize it with the
	 * given command and <code>Image</code>.
	 *
	 *
	 * @param command the command that will be passed in action events.
	 * @param image the image to be used.
	 */
	public ImageButton( String command, Image image ) {
		this();
		this.command = command;
		this.image = image;
		waitForImage( this, image );
}
	/**
	 * Add the given <code>ActionListener</code> to my set of listeners.
	 *
	 * @param listener the action listener to add. 
	 */
	public void addActionListener( ActionListener listener ) {
		actionListener = AWTEventMulticaster.add( actionListener, listener );
	}
	/**
	 * Answer the command associated with this receiver.
	 * 
	 * @return a String representing the command passed in action events generated
	 * by this button.
	 */
	public String getCommand() {
		return command;
	}
	/**
	 * Answers the default command for the receiver (intended for initialization).
	 * 
	 * @return a String representing the default command to use.
	 */
	protected String getDefaultCommand() {
		return "ImageButton";
	}
	/**
	 * Answers the default image for the receiver (intended for initialization.
	 * If this is changed to something other than null, it will also need to
	 * wait for the image.
	 * 
	 * @return the default Image for this button to use
	 */
	protected Image getDefaultImage() {
		return null;
	}
	/**
	 * Answers the current image this button is associated with.
	 * 
	 * @return the Image this button is using.
	 */
	public Image getImage() {
		return image;
	}
	/**
	 * Answer the size this <code>ImageButton</code> must be displayed at.
	 *
	 * @return an integer representing the minimum size for this image button
	 */
	public Dimension getMinimumSize() {
		return new Dimension(
			image.getWidth( this ) + 4,
			image.getHeight( this ) + 4
		);
	}
	/**
	 * Answer the size this <code>ImageButton</code> prefers to be displayed at.
	 *
	 * @return an integer representing the preferred size for this <code>ImageButton</code>.
	 */
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	/**
	 * Returns whether this button is highlighted (down) or not.
	 * 
	 * @return a boolean representing the button's current
	 * highlight state.
	 */
	public boolean isHighlighted() {
		return isHighlighted;
	}
	/**
	 * Mouse 'action' events are handled in <code>MousePressed</code> and <code>MouseReleased</code>.
	 * 
	 * @param e the event.
	 */
	public void mouseClicked( MouseEvent e ) {
	}
	/**
	 * If the mouse moves inside of the button, set the <code>isInside</code> flag.
	 * 
	 * @param e the event.
	 */
	public void mouseEntered( MouseEvent e ) {
		isInside = true;
		if ( isPressed )
			repaint();
	}
	/**
	 * If the mouse moves outside of the button, reset the <code>isInside</code> flag.
	 * 
	 * @param e the event.
	 */
	public void mouseExited( MouseEvent e ) {
		isInside = false;
		if ( isPressed )
			repaint();
	}
	/**
	 * If the mouse is pressed inside of the button, set the <code>isPressed</code> flag.
	 * 
	 * @param e the event.
	 */
	public void mousePressed( MouseEvent e ) { 
		if ( isInside ) {
			isPressed = true;
			repaint();
		}
	}
	/**
	 * If the mouse is released, check to see if it is inside of the button, and if it was
	 * originally pressed inside of the button. If it was, it was an action event, so call
	 * <code>processActionEvent()</code>. Then reset the <code>isPressed</code> flag.
	 * 
	 * @param e the event.
	 */
	public void mouseReleased( MouseEvent e ) {
		if ( isInside && isPressed ) {
			processActionEvent();
		}
		isPressed = false;
		repaint();
	}
	/**
	 * Paint the button.
	 * 
	 * @param g the graphics object to use in painting.
	 */
	public void paint( Graphics g ) {
		if ( isHighlighted() || ( isPressed && isInside ) )
			paintHighlighted( g );
		else
			paintUnhighlighted( g );
	}
	/**
	 * Paint the button highlight.
	 * 
	 * @param g the graphics object to use in painting.
	 * @param firstColor the <code>Color</code> to paint the top and left-hand sides.
	 * @param secondColor the <code>Color</code> to paint the bottom and right-hand sides.
	 */
	public void paintHighlight( Graphics g, Color firstColor, Color secondColor ) {
		int rightX = getSize().width - 1;
		int bottomY = getSize().height - 1;
		g.setColor( firstColor );
		g.drawLine( 0, 0, rightX, 0 );
		g.drawLine( 0, 0, 0, bottomY );
		g.setColor( secondColor );
		g.drawLine( 0, bottomY, rightX, bottomY );
		g.drawLine( rightX, 0, rightX, bottomY );
	}
	/**
	 * Paint the button highlighted (down).
	 * 
	 * @param g the graphics object to use in painting.
	 */
	public void paintHighlighted( Graphics g ) {
		paintHighlight( g, Color.darkGray, Color.white );
		g.drawImage( image, 3, 3, this );
	}
	/**
	 * Paint the button unhighlighted (not down).
	 * 
	 * @param g the graphics object to use in painting.
	 */
	public void paintUnhighlighted( Graphics g ) {
		paintHighlight( g, Color.white, Color.darkGray );
		g.drawImage( image, 2, 2, this );
	}
	/**
	 * Create an <code>ActionEvent</code> and pass it to everyone listening for it.
	 */
	public void processActionEvent() {
		if ( actionListener != null ) {
			actionListener.actionPerformed(
				new ActionEvent( this, ActionEvent.ACTION_PERFORMED, command )
			);
		}
	}
	/**
	 * Remove the given <code>ActionListener</code> from my set of listeners.
	 * 
	 * @param l ActionListener
	 */
	public void removeActionListener( ActionListener listener ) {
		actionListener = AWTEventMulticaster.remove( actionListener, listener );
	}
	/**
	 * Set the command associated with the receiver.
	 * 
	 * @param command the command to be passed in action events generated.
	 * by this button.
	 */
	public void setCommand( String command ) {
		this.command = command;
	}
	/**
	 * Set whether this button is highlighted (down) or not.
	 * 
	 * @param isHighlighted a boolean representing the button's
	 * new highlight state.
	 */
	public void setHighlight( boolean isHighlighted ) {
		this.isHighlighted = isHighlighted;
		repaint();
	}
	/**
	 * Set the <code>Image</code> this button is associated with.
	 * 
	 * @param image the image this button should use.
	 */
	public void setImage( Image image ) {
		this.image = image;
		waitForImage( this, image );
	}
	/**
	 * Wait for the image to be available.
	 *
	 * @param component the component the image is associated with.
	 * @param image the image to wait for.
	 */
	protected static void waitForImage( Component component, Image image ) {
		MediaTracker tracker = new MediaTracker( component );
		try {
			tracker.addImage( image, 0 );
			tracker.waitForID( 0 );
		}
		catch ( InterruptedException e ) {
			e.printStackTrace();
		}
	}
}
