package com.rolemodelsoft.drawlet;

/**
 * @(#)FigureHolder.java
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
 
/**
 * When implemented, this interface allows implementing classes to serve a Figure.
 *
 * @version 	1.1.6, 12/28/98
 */
 
public interface FigureHolder {

	/** 
	 * Answer the figure the receiver is holding.
	 *
	 * @return the Figure the receiver is holding
	 */
	public Figure getFigure();
	/** 
	 * Set the figure the receiver is holding.
	 *
	 * @param figure the Figure to hold
	 */
	public void setFigure(Figure figure);
}
