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

public class ColorButton extends Component implements MouseListener {
	/**
	 * The label (not visible) for this button.
	 */
	protected String command = getDefaultCommand();

	/**
	 * The image for this button.
	 */
	protected Color color = getDefaultColor();

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
	  * Flag indicating the current value of highlighting.
	  */
	protected boolean isHighlighted = false;
	/**
	 * Create a new, default <code>ColorButton</code>
	 */
	public ColorButton() {
		super();
		addMouseListener( this );
	}
	/**
	 * Create a new <code>ColorButton</code> initialized with the given color.
	 *
	 * @param color the color to be used.
	 */
	public ColorButton( Color color ) {
		this();
		this.color = color;
	}
	/**
	 * Create a new <code>ColorButton</code> initialized with the given command.
	 *
	 * @param command the command that will be passed in action events.
	 */
	public ColorButton( String command ) {
		this();
		this.command = command;
	}
	/**
	 * Create a new <code>ColorButton</code> initialized with the given command and color.
	 *
	 * @param command the command that will be passed in action events.
	 * @param color the color to be used.
	 */
	public ColorButton( String command, Color color ) {
		this();
		this.command = command;
		this.color = color;
}
	/**
	 * Add the given <code>ActionListener</code> to my set of listeners.
	 *
	 * @param listener the <code>ActionListener</code> to add. 
	 */
	public void addActionListener( ActionListener listener ) {
		actionListener = AWTEventMulticaster.add( actionListener, listener );
	}
	/**
	 * Answers the <code>Color</code> this button represents and displays.
	 * 
	 * @return the Color this button holds.
	 */
	public Color getColor() {
		return color;
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
	 * Answers the default color for the receiver (intended for initialization).
	 * 
	 * @return the default Color for this button to use
	 */
	protected Color getDefaultColor() {
		return Color.white;
	}
	/**
	 * Answers the default command for the receiver (intended for initialization).
	 * 
	 * @return a String representing the default command to use.
	 */
	protected String getDefaultCommand() {
		return "ColorButton";
	}
	/**
	 * Answer the size this color button must be displayed at.
	 *
	 * @return an integer representing the minimum size for this color button
	 */
	public Dimension getMinimumSize() {
		return new Dimension( 16, 16 );
	}
	/**
	 * Answer the size this color button prefers to be displayed at.
	 *
	 * @return an integer representing the preferred size for this color button
	 */
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	/**
	 * Answer whether or not the receiver is to be drawn highlighted.
	 * 
	 * @return	a boolean value of <code>true</code> if the receiver is to be drawn highlighted;
	 *			or <code>false</code> otherwise.
	 */
	public boolean isHighlighted() {
		return isHighlighted;
	}
	/**
	 * Mouse events are handled in <code>MousePressed</code> and <code>MouseReleased</code>.
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
	}
	/**
	 * If the mouse moves outside of the button, reset the <code>isInside</code> flag.
	 * 
	 * @param e the event.
	 */
	public void mouseExited( MouseEvent e ) {
		isInside = false;
	}
	/**
	 * If the mouse is pressed inside of the button, set the <code>isPressed</code> flag.
	 * 
	 * @param e the event.
	 */
	public void mousePressed( MouseEvent e ) {
		if ( isInside ) {
			isPressed = true;
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
	}
	/**
	 * Paint the button.
	 * 
	 * @param g the graphics object to use in painting.
	 */
	public void paint( Graphics g ) {
		if ( isHighlighted() )
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
		if ( color == null ) {
			g.setColor( Color.black );
			paintLabel( g, 1 );
		} else {
			g.setColor( color );
			g.fillRect( 3, 3, getSize().width - 4, getSize().height - 4 );
		}
	}
	/**
	 * Paint the label form, offsetting it by the given amount.
	 * 
	 * @param g the graphics object to use in painting.
	 * @param moveBy an integer representing the amount to offset the label by.
	 */
	public void paintLabel(Graphics g, int moveBy) {
		Dimension dimension = getSize();
		FontMetrics metrics = g.getFontMetrics();
		int width = metrics.stringWidth(command);
		int height = metrics.getHeight();
		g.drawString(command, ( (dimension.width - width) / 2 ) + moveBy, ( (dimension.height - height) / 2 + metrics.getAscent() ) + moveBy );
	}
	/**
	 * Paint the button unhighlighted (not down).
	 * 
	 * @param g the graphics object to use in painting.
	 */
	public void paintUnhighlighted( Graphics g ) {
		paintHighlight( g, Color.white, Color.darkGray );
		if ( color == null ) {
			g.setColor( Color.black );
			paintLabel( g, 0 );
		} else {
			g.setColor( color );
			g.fillRect( 2, 2, getSize().width - 4, getSize().height - 4 );
		}
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
	 * @param listener the <code>ActionListener</code> to remove. 
	 */
	public void removeActionListener( ActionListener listener ) {
		actionListener = AWTEventMulticaster.remove( actionListener, listener );
	}
	/**
	 * Set the <code>Color</code> this button is associated with.
	 * 
	 * @param color the color this button should use.
	 */
	public void setColor( Color color ) {
		this.color = color;
	}
	/**
	 * Set the command associated with this receiver.
	 * 
	 * @param command the command to be passed in action events generated.
	 * by this button.
	 */
	public void setCommand( String command ) {
		this.command = command;
	}
	/**
	 * Set whether or not the receiver is to be drawn highlighted.
	 * 
	 * @param highlighted <code>true</code> if the button should be drawn highlighted;
	 * <code>false</code> otherwise.
	 */
	public void setHighlighted( boolean highlighted ) {
		this.isHighlighted = highlighted;
		repaint();
	}
}
