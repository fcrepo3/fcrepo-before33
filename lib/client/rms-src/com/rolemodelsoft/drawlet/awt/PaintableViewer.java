package com.rolemodelsoft.drawlet.awt;

/**
 * @(#)PaintableViewer.java
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
import com.rolemodelsoft.drawlet.*;

/**
 * A component which can show any Paintable.
 *
 * @version 	1.1.6, 12/30/98
 */
public class PaintableViewer extends Component {
	protected Paintable paintable;
	/**
	 * Create a <code>PaintableViewer</code>.
	 */
	public PaintableViewer() {
		super();
	}
	/**
	 * Create a <code>PaintableViewer</code> initially showing the given <code>Paintable</code>.
	 *
	 * @param paintable the thing to view
	 */
	public PaintableViewer(Paintable paintable) {
		super();
		this.paintable = paintable;
	}
	/**
	 * Answer the size the receiver must be displayed at.
	 *
	 * @return an integer representing the minimum size for the receiver.
	 */
	public Dimension getMinimumSize() {
		if (paintable == null)
			return super.getMinimumSize();
		else {
			Rectangle rect = (new Rectangle(0,0)).union(paintable.getBounds());
			rect.grow(10, 10);
			return rect.getSize();
		}
	}
	/**
	 * @return the paintable assocatied with the receiver.
	 */
	public Paintable getPaintable() {
		return paintable;
	}
	/**
	 * Answer the size the receiver prefers to be displayed at.
	 *
	 * @return an integer representing the preferred size for the receiver
	 */
	public Dimension getPreferredSize() {
		if (paintable == null)
			return super.getPreferredSize();
		else {
			Rectangle rect = (new Rectangle(0,0)).union(paintable.getBounds());
			rect.grow(10, 10);
			return rect.getSize();
		}
	}
	/**
	 * Paint the thing we're viewing.
	 * 
	 * @param g the graphics object to use in painting.
	 */
	public void paint( Graphics g ) {
		paintBackground(g);
		if (paintable != null)
			paintable.paint(g);
	}
	/**
	 * Paint the background of the component.
	 * 
	 * @param g the graphics object to use in painting.
	 */
	public void paintBackground( Graphics g ) {
		Dimension dimension = getSize();
		g.setColor( getBackground() );
		g.fillRect( 0, 0, dimension.width, dimension.height );
	}
	/**
	 * Set the thing we are viewing and repaint.
	 *
	 * @param paintable the <code>Paintable</code> to be associated with this viewer.
	 */
	public void setPaintable(Paintable paintable) {
		this.paintable = paintable;
		repaint();
	}
}
