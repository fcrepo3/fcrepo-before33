package com.rolemodelsoft.drawlet.shapes.lines;

/**
 * @(#)LineFigureRelativePoint.java
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
 
import com.rolemodelsoft.drawlet.*;
import com.rolemodelsoft.drawlet.basics.*;
import com.rolemodelsoft.drawlet.shapes.*;
import java.awt.*;
import java.util.Hashtable;

/**
 * This class implements Locator by providing x and y coordinates that are
 * relative to a point on a segment of LineFigure.  
 * Although it is assumed that the normal use will have
 * these locators on the LineFigure, that is not necessary.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class LineFigureRelativePoint extends FigureRelativePoint {
	static final long serialVersionUID = 8456398037474191179L;
	
	/**
	 * The 1-based index of the segment within the figure from which 
	 * we will define our relative position.
	 */
	protected int segment = 1;
	/** 
	 * Constructs and initializes a new instance of a locator which is
	 * relative to a point on a line
	 *
	 * @param figure the figure to which the instance will be relative
	 */
	public LineFigureRelativePoint(LineFigure figure) {
		super(figure);
	}
	/** 
	 * Constructs and initializes a new instance of a locator which is
	 * relative to a point on a line
	 *
	 * @param figure the figure to which the instance will be relative
	 * @param relativeX the percentage of the length of the default segment at which to determine X
	 * @param relativeY the percentage of the length of the default segment at which to determine Y
	 */
	public LineFigureRelativePoint(LineFigure figure, double relativeX, double relativeY) {
		this(figure);
		this.relativeX = relativeX;
		this.relativeY = relativeY;
	}
	/** 
	 * Constructs and initializes a new instance of a locator which is
	 * relative to a point on a line
	 *
	 * @param figure the figure to which the instance will be relative
	 * @param relativeX the percentage of the length of the default segment at which to determine X
	 * @param relativeY the percentage of the length of the default segment at which to determine Y
	 * @param offsetX the offset (added to relative X) in the x direction
	 * @param offsetY the offset (added to relative Y) in the y direction
	 */
	public LineFigureRelativePoint(LineFigure figure, double relativeX, double relativeY, int offsetX, int offsetY) {
		this(figure,relativeX,relativeY);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}
	/** 
	 * Constructs and initializes a new instance of a locator which is
	 * relative to a point on a line
	 *
	 * @param figure the figure to which the instance will be relative
	 * @param segment the 1-based index of the specific segment which will be the basis of our relative location
	 * @param relativeX the percentage of the length of the segment at which to determine X
	 * @param relativeY the percentage of the length of the segment at which to determine Y
	 */
	public LineFigureRelativePoint(LineFigure figure, int segment, double relativeX, double relativeY) {
		this(figure,relativeX,relativeY);
		this.segment = segment;
	}
	/** 
	 * Constructs and initializes a new instance of a locator which is
	 * relative to a point on a line
	 *
	 * @param figure the figure to which the instance will be relative
	 * @param segment the 1-based index of the specific segment which will be the basis of our relative location
	 * @param relativeX the percentage of the length of the segment at which to determine X
	 * @param relativeY the percentage of the length of the segment at which to determine Y
	 * @param offsetX the offset (added to relative X) in the x direction
	 * @param offsetY the offset (added to relative Y) in the y direction
	 */
	public LineFigureRelativePoint(LineFigure figure, int segment, double relativeX, double relativeY, int offsetX, int offsetY) {
		this(figure,relativeX,relativeY,offsetX,offsetY);
		this.segment = segment;
	}
	/** 
	 * Constructs and initializes a new instance of a locator which is
	 * relative to a point on a line
	 *
	 * @param figure the figure to which the instance will be relative
	 * @param offsetX the offset (added to default relative X) in the x direction
	 * @param offsetY the offset (added to default relative Y) in the y direction
	 */
	public LineFigureRelativePoint(LineFigure figure, int offsetX, int offsetY) {
		this(figure);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}
	/** 
	 * Constructs and initializes a new instance of a locator which is
	 * relative to a point on a line
	 *
	 * @param figure the figure to which the instance will be relative
	 * @param segment the 1-based index of the specific segment which will be the basis of our relative location
	 * @param offsetX the offset (added to default relative X) in the x direction
	 * @param offsetY the offset (added to default relative Y) in the y direction
	 */
	public LineFigureRelativePoint(LineFigure figure, int segment, int offsetX, int offsetY) {
		this(figure,offsetX,offsetY);
		this.segment = segment;
	}
	/** 
	 * Answer the default/initial value of relativeX.
	 * 
	 * @return	a double representing the default/initial value of relativeX
	 */
	protected double defaultRelativeX() {
		return 0.5;
	}
	/** 
	 * Answer the default/initial value of relativeY.
	 * 
	 * @return	a double representing the default/initial value of relativeY
	 */
	protected double defaultRelativeY() {
		return 0.5;
	}
	/** 
	 * Answer the x coordinate.
	 * 
	 * @return	an integer representing the x coordinate
	 */
	public int x()  {
		LineFigure myFigure = (LineFigure)figure;
		Locator begin = myFigure.getLocator(segment - 1);
		Locator end = myFigure.getLocator(segment);
		return (int)Math.rint((double)(end.x() - begin.x()) * relativeX) + begin.x() + offsetX;
	}
	/** 
	 * Answer the y coordinate.
	 * 
	 * @return	an integer representing the y coordinate
	 */
	public int y()  {
		LineFigure myFigure = (LineFigure)figure;
		Locator begin = myFigure.getLocator(segment - 1);
		Locator end = myFigure.getLocator(segment);
		return (int)Math.rint((double)(end.y() - begin.y()) * relativeY) + begin.y() + offsetY;
	}
}
