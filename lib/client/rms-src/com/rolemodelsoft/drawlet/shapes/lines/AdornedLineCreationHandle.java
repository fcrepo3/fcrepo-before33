package com.rolemodelsoft.drawlet.shapes.lines;

/**
 * @(#)AdornedLineCreationHandle.java
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
 * This class provides a handle that creates adorned lines from one figure
 * to another.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class AdornedLineCreationHandle extends ConnectedLineCreationHandle {
	/**
	 * Creates a new AdornedLineCreationHandle initialized with the given Figure.
	 *
	 * @param figure the figure to which the handle is attached.
	 */
	public AdornedLineCreationHandle(Figure figure) {
		super(figure);
	}
	/**
	 * AdornedLineCreationHandle constructor comment.
	 *
	 * @param figure the figure to which the handle is attached.
	 * @param locator the locator at which to locate the handle and beginning of any lines.
	 */
	public AdornedLineCreationHandle(Figure figure, Locator locator) {
		super(figure, locator);
	}
	/**  
	 * Return a ConnectingLine of the proper type.
	 * 
	 * @return	a ConnectingLine of the proper type.
	 */
	protected ConnectingLine basicNewLine( Locator point )  {
		AdornedLine aLine = new AdornedLine( (Locator) locator.duplicate(), point, true );
		aLine.addAdornment( new Arrow( aLine ) );
		return aLine;
	}
}
