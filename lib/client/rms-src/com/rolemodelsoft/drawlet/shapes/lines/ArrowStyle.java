package com.rolemodelsoft.drawlet.shapes.lines;

/**
 * @(#)ArrowStyle.java
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
import com.rolemodelsoft.drawlet.basics.*;
import java.util.*;
import java.awt.*;

/**
 * Defines how an arrow will be drawn.
 * @version 	1.1.6, 03/22/99
 */

public class ArrowStyle extends SimpleDrawingStyle {
	/**
	 * A boolean designating whether this arrow is opaque or not.
	 */
	protected boolean isOpaque = defaultIsOpaque();
	/**
	 * The locators which define the arrow head
	 */
	protected Locator[] locators;

	/**
	 * The default locators for ArrowStyles.
	 */
	protected static Locator[] defaultLocators;
/**
 * 
 */
public ArrowStyle() {
	this(getDefaultLocators());
}
/**
 * 
 */
public ArrowStyle(Locator[] locators) {
	this.locators = locators;
}
/**
 * 
 */
public ArrowStyle(Locator[] locators, boolean isOpaque) {
	this.locators = locators;
	this.isOpaque = isOpaque;
}
	/**
	 * getFillColor method comment.
	 */
	protected boolean defaultIsOpaque() {
		return true;
	}
/**
 * 
 */
protected static Locator[] getDefaultLocators() {
	if ( defaultLocators == null ) {
		defaultLocators = new Locator[] {
			new PolarCoordinate( 10, 0.5 + Math.PI ),
			new PolarCoordinate( 0, 0.0 ),
			new PolarCoordinate( 10, -0.5 + Math.PI )
		};
	}
	return defaultLocators;
}
/**
 * getLocators method comment.
 */
public Locator[] getLocators() {
	return locators;
}
/**
 * isFilled method comment.
 */
public boolean isOpaque() {
	return isOpaque;
}
/**
 * 
 */
public static void setDefaultLocators( Locator[] locators ) {
	defaultLocators = locators;
}
/**
 * setFilled method comment.
 */
public void setOpaque(boolean opaque) {
	this.isOpaque = opaque;
}
}
