package com.rolemodelsoft.drawlet;

/**
 * @(#)SequenceOfFigures.java
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
import java.awt.*;
import java.io.Serializable;

/**
 * A generic SequenceOfFigures from which Figures can be inserted, moved, and/or deleted.
 *
 * @version 	1.1.6, 12/28/98
 */
public interface SequenceOfFigures extends Serializable {

	/** 
	 * Add the figure to the receiver.
	 * 
	 * @param figure the figure to add
	 */
	public abstract void addFigure(Figure figure);
	/** 
	 * Add the figure to the receiver, sticking it behind 
	 * an existingFigure which is already there.
	 * 
	 * @param figure the figure to add
	 * @param existingFigure the figure to which the new figure should be behind
	 */
	public abstract void addFigureBehind(Figure figure, Figure existingFigure);
	/** 
	 * Answer the figure at a given point
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return	the Figure at the given point
	 */
	public abstract Figure figureAt(int x, int y);
	/** 
	 * Answer a FigureEnumeration over the figures of the receiver.
	 * 
	 * @return	a FigureEnumeration over the figures of the receiver
	 */
	public abstract FigureEnumeration figures();
	/** 
	 * Move the figure behind an existingFigure if it is not already there.
	 * Reflect the change if it's visible.
	 * 
	 * @param figure the figure to move
	 * @param existingFigure the figure to which the new figure should be behind
	 */
	public abstract void moveFigureBehind(Figure figure, Figure existingFigure);
	/** 
	 * Move the figure in front of an existingFigure if it is not already there.
	 * Reflect the change if it's visible.
	 * 
	 * @param figure the figure to move
	 * @param existingFigure the figure to which the new figure should be in front
	 */
	public abstract void moveFigureInFront(Figure figure, Figure existingFigure);
	/** 
	 * Move the figure behind all other figures.
	 * Reflect the change if it's visible.
	 * 
	 * @param figure the figure to add
	 */
	public abstract void moveFigureToBack(Figure figure);
	/** 
	 * Move the figure in front of all other figures.
	 * Reflect the change if it's visible.
	 * 
	 * @param figure the figure to add
	 */
	public abstract void moveFigureToFront(Figure figure);
	/** 
	 * Answer the figure at a given point excluding the identified figure
	 * 
	 * @param figure the figure to exclude from the search
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return	the Figure at the given point excluding the identified Figure
	 */
	public abstract Figure otherFigureAt(Figure excludedFigure, int x, int y);
	/** 
	 * Remove the figure.
	 * Implementors may choose to tell the figure to dispose of itself completely...
	 * It is recommended that this occur unless implementors have a reason for using
	 * a different strategy.
	 * 
	 * @param figure the figure to remove
	 */
	public abstract void removeFigure(Figure figure);
}
