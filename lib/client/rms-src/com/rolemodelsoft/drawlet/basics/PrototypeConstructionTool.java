package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)PrototypeConstructionTool.java
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
import java.awt.*;

/**
 * This tool create new Figures based on a prototypical Figure.
 *
 * @version 	1.1.6, 12/28/98
 */
 
public class PrototypeConstructionTool extends ConstructionTool {

	/**
	 * The figure we duplicate to create new ones.
	 */
	protected Figure prototype;

	/** 
	 * Constructs and initializes a new instance of a tool which constructs new
	 * figures on a DrawingCanvas based on a prototypical figure
	 *
	 * @param canvas the canvas to which we add new figures.
	 * @param prototype the figure which we duplicate to create new ones.
	 */
	public PrototypeConstructionTool(DrawingCanvas canvas, Figure prototype) {
	this.canvas = canvas;
	this.prototype = prototype;
	}
   /**
	 * Create and answer a new Figure which is a duplicate of the prototype.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return	a new Figure which is a duplicate of the prototype
	 */
	protected Figure basicNewFigure(int x, int y)  {
	Figure newFigure = (Figure)prototype.duplicate();
	newFigure.move(x,y);
	return newFigure;
	}
}
