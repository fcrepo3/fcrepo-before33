package com.rolemodelsoft.drawlet;

/**
 * @(#)Locator.java
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
import com.rolemodelsoft.drawlet.util.Duplicatable;
import java.io.Serializable;

/**
 * This interface defines protocols for objects that provide
 * 2-D coordinates.
 *
 * @version 	1.1.6, 12/28/98
 */
public interface Locator extends Duplicatable, Serializable {

	/** 
	 * Answer the radius of the Locator (as a PolarCoordinate).
	 * 
	 * @return	an integer representing the radius
	 */
	public abstract int r();
	/** 
	 * Answer the angle in radians of the Locator (as a PolarCoordinate).
	 * 
	 * @return double representing theta
	 */
	public abstract double theta();
	/** 
	 * Answer the x coordinate.
	 * 
	 * @return	an integer representing the x coordinate
	 */
	public abstract int x();
	/** 
	 * Answer the y coordinate.
	 * 
	 * @return	an integer representing the y coordinate
	 */
	public abstract int y();
}
