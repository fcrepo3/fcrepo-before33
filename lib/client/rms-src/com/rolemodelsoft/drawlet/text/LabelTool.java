package com.rolemodelsoft.drawlet.text;

/**
 * @(#)LabelTool.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 * Copyright (c) 1997 Knowledge Systems Corporation (KSC). All Rights Reserved.
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
import java.awt.*;

/**
 * This tool produces text labels.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class LabelTool extends ConstructionTool {

	/** 
	 * Constructs and initializes a new instance of a tool to create labels
	 * on a DrawingCanvas
	 *
	 * @param canvas the canvas on which to place labels.
	 */
	public LabelTool(DrawingCanvas canvas) {
		this.canvas = canvas;
	}
   /**
	 * Create and answer a new Figure.
	 *
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @return a new Figure.
	 */
	protected Figure basicNewFigure(int x, int y)  {
		Figure newFigure = new TextLabel(new Font("Courier",Font.BOLD,24));
		newFigure.move(x,y);
		return newFigure;
	}
}
