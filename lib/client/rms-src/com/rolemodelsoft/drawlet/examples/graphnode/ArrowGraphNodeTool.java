package com.rolemodelsoft.drawlet.examples.graphnode;

/**
 * @(#)ArrowGraphNodeTool.java
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
 * Here is a simple tool that extends the <code>SelectionTool</code>
 * to create <code>ArrowGraphNodes</code> when a doubleClick event occurs.
 */
public class ArrowGraphNodeTool extends GraphNodeTool {
/**
 * ArrowGraphNodeTool constructor comment.
 * @param canvas com.rolemodelsoft.drawlet.DrawingCanvas
 */
public ArrowGraphNodeTool(DrawingCanvas canvas) {
	super(canvas);
}
	/**
	 * Answer a new graph node
	 */
	protected GraphNode basicNewGraphNode() {
		return new ArrowGraphNode();
	}
}
