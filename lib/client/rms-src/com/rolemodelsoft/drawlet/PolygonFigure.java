package com.rolemodelsoft.drawlet;

/**
 * @(#)PolygonFigure.java
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
 * whose boundaries are defined by a polygon.
 *
 * @version 	1.1.6, 12/28/98
 */
public interface PolygonFigure extends Figure {

	/**  
	 * Answer a Polygon associated with the figure
	 * 
	 * @return	the Polygon associated with the figure
	 */
	public abstract Polygon getPolygon();
	/**  
	 * Set the Polygon associated with the figure
	 * 
	 * @param polygon the Polygon
	 */
	public abstract void setPolygon(Polygon polygon);
}
