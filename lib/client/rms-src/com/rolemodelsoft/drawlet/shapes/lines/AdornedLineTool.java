package com.rolemodelsoft.drawlet.shapes.lines;

/**
 * @(#)AdornedLineTool.java
 *
 * Copyright (c) 1999-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
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

/**
 * This tool produces AdornedLines
 * @version 	1.1.6, 03/22/99
 */

public class AdornedLineTool extends ConnectingLineTool {
/**
 * Creates a new AdornedLineTool initialized with the given DrawingCanvas.
 *
 * @param canvas the DrawingCanvas to create AdornedLines on.
 */
public AdornedLineTool(DrawingCanvas canvas) {
	super(canvas);
}
	/**
	 * Create and answer a new Figure.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	protected Figure basicNewFigure(int x, int y)  {
		AdornedLine line = new AdornedLine(x,y,x,y);
		line.addAdornment( new Arrow( line ) );
		return line;
	}
}
