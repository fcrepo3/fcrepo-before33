package com.rolemodelsoft.drawlet;

/**
 * @(#)LineFigure.java
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
import java.awt.*;

/**
 * This interface defines protocols for Figures
 * that are essentially a line or series of line segments.
 *
 * @version 	1.1.6, 12/28/98
 */
public interface LineFigure extends Figure {

	/**
	 * Add the locator at the specified position.
	 * @param locator the new Locator to add.
	 * @param index the index of the locator desired.
	 */
	public abstract void addLocator(int index, Locator locator);
	/**
	 * Add the locator at the end.
	 * @param locator the new Locator to add.
	 */
	public abstract void addLocator(Locator locator);
	/**
	 * Answer the indexth locator.
	 * 
	 * @param index the index of the locator desired.
	 * @return	the Locator at the desired index
	 */
	public abstract Locator getLocator(int index);
	/**
	 * Answer the number of points which define the receiver.
	 * 
	 * @return	an integer representing the number of points
	 * which define the receiver
	 */
	public abstract int getNumberOfPoints();
	/**
	 * Remove the locator at the specified position.
	 *
	 * @param index the index of the locator desired.
	 */
	public abstract void removeLocator(int index);
	/**
	 * Set the locator at the specifed position.
	 * @param locator the new Locator.
	 * @param index the index of the locator desired.
	 */
	public abstract void setLocator(int index, Locator locator);
}
