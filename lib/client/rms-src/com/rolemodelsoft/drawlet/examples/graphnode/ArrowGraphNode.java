package com.rolemodelsoft.drawlet.examples.graphnode;

/**
 * @(#)ArrowGraphNode.java
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
import com.rolemodelsoft.drawlet.basics.*;
import com.rolemodelsoft.drawlet.shapes.lines.*;

/**
 * Here is an example of a <code>GraphNode</code> with <code>Arrow</code>s.
 */
public class ArrowGraphNode extends GraphNode {
	static final long serialVersionUID = -451845920984253071L;
	/**
	 * Answer a new line creation handle
	 */
	protected ConnectedLineCreationHandle basicNewLineCreationHandle() {
		return new AdornedLineCreationHandle(this);
	}
	/** 
	 * Answers a Locator corresponding to a significant point on the receiver 
	 * that will serve as a connection to the other figure.
	 * By default, make it the middle of the receiver.
	 * Subclasses may wish to do something more meaningful.
	 *
	 * @param x the x coordinate of the requested locator
	 * @param y the y coordinate of the requested locator
	 */
	public Locator requestConnection(Figure requestor, int x, int y) {
	if (requestor == this)
		return null;
	return new FigureRelativePoint(this,0.5,0.5);
	}
}
