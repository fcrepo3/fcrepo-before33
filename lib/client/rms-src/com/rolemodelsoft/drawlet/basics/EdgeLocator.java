package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)EdgeLocator.java
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
/**
 * This class implements Locator by providing x and y coordinates that are
 * at the intersection of the figure and the line defined by the 2 locators.
 */
public class EdgeLocator extends AbstractLocator implements RelativeLocator {
	/**
	 * The locator that is assumed to be inside a figure.
	 */
	protected Locator inside;

	/**
	 * The locator that is assumed to be outside a figure.
	 */
	protected Locator outside;
	
	/**
	 * The figure to which the line is connected.
	 */	
	protected Figure figure;
	protected int x, y, startX, startY, endX, endY;
/**
 * Creates a new EdgeLocator initialized with the given values.
 *
 * @param inside a Locator representing the end of a line inside a figure.
 * @param outside a Locator representing the end of a line outside a figure.
 * @param figure the figure at the inside Locator.
 */
public EdgeLocator(Locator inside, Locator outside) {
	this.inside = inside;
	this.outside = outside;
	this.figure = figureFromLocator(inside);
}
	/**
	 * 
	 */
	protected void computeIntersection() {

		//do a binary search.
		//check to see if the second point is contained in the figure
		//if it is then create a point between the end and middle and check
		//to see if it is contained.
		//if it is then it is the new end point repeat the step above
		//if it is not then set it as the new start point and create a middle point and check it.
		//continue until start and end are the same.

		//start points
		int startX = inside.x();
		int startY = inside.y();

		//end points
		int endX = outside.x();
		int endY = outside.y();

		if (this.startX == startX && this.startY == startY && this.endX == endX && this.endY == endY)
			return;
		else {
			this.startX = startX;
			this.startY = startY;
			this.endX = endX;
			this.endY = endY;
		}
		
		computeMiddlePoint(startX, startY, endX, endY);

		while (!differenceIsOneOrNone(startX, endX) || !differenceIsOneOrNone(startY, endY)) {
			if (!figure.contains(x, y)) {
				endX = x;
				endY = y;
			} else {
				startX = x;
				startY = y;
			}
			computeMiddlePoint(startX, startY, endX, endY);
		}
	}
/**
 * 
 * @return com.rolemodelsoft.drawlet.basics.DrawingPoint
 */
protected void computeMiddlePoint(int startX, int startY, int endX, int endY) {
	x = (startX + endX)/2;
	y = (startY + endY)/2;
}
/**
 * 
 */
protected boolean differenceIsOneOrNone(int x, int y) {
	return (x==y || x+1==y || x-1==y);
}
	/**
	 * Answer the figure, if any, associated with the locator.
	 * This can be a useful utility to determine whether or not a particular
	 * locator is tied to some figure.
	 *
	 * @param aLocator the Locator to mine for figures
	 * @return	the Figure associated with the locator, or null if none
	 */
	public static Figure figureFromLocator(Locator aLocator) {
		if (aLocator instanceof FigureHolder) {
			return ((FigureHolder)aLocator).getFigure();
		}
		if (aLocator instanceof RelativeLocator) {
			return figureFromLocator(((RelativeLocator)aLocator).getBase());
		}
		return null;
	}
	/** 
	 * Answer the base concrete locator of the receiver.
	 * 
	 * @return	the base concrete Locator of the receiver
	 */
	public Locator getBase() {
		return inside;
	}
/** 
 * Answer the x coordinate.
 * 
 * @return	an integer representing the x coordinate
 */
public int x() {
	computeIntersection();
	return x;
}
/** 
 * Answer the y coordinate.
 * 
 * @return	an integer representing the y coordinate
 */
public int y() {
	computeIntersection();
	return y;
}
}
