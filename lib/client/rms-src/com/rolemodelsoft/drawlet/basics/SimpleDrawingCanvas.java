package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)SimpleDrawingCanvas.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 * Copyright (c) 1996-1997 Knowledge Systems Corporation (KSC). All Rights Reserved.
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
import com.rolemodelsoft.drawlet.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import java.awt.datatransfer.*;
import java.beans.*;

/**
 * This provides basic functionality necessary to provide a meaningful
 * working version of a DrawingCanvas that can be tied to a Component in an AWT
 * Application.  It is expected that this would serve as the base for these sort
 * of Components, but not required.
 *
 * @version 	1.1.6, 12/28/98
 */

public class SimpleDrawingCanvas extends AbstractPaintable implements InputEventHandler, DrawingCanvas, ComponentHolder, PropertyChangeListener {

	/**
	 * A place to hold figures that are cut or copied.  
	 * Currently, this is a rather primitive approach.
	 */
	protected static Clipboard clipboard = new Clipboard( "" );

	/**
	 * The amount to add to drawings which are dynamically sized.
	 */
	protected static final int buffer = 200;
	
	/**
	 * The drawing we are displaying/manipulating.
	 */
	protected Drawing drawing;
	
	/**
	 * The Component we are associated with.
	 */
	protected Component component;
	
	/**
	 * The tool which is the first thing we look to in order to handle events.
	 */
	protected InputEventHandler tool = defaultTool();

	/**
	 * The figures which are currently selected for further operations.
	 */
	protected Vector selections = defaultSelections();

	/**
	 * The handles that may take control of events.  Typically these are
	 * attached to figures, but not always.
	 */
	protected Vector handles = defaultHandles();

	/**
	 * A mapping of figures to their associated handles.
	 * This is typically used to allow for proper clean up if figures
	 * are removed, but may be used for other purposes.
	 */
	protected Hashtable figureHandles = defaultFigureHandles();

	/**
	 * The style with which it is expected new figures will be drawn.
	 */
	protected DrawingStyle style = defaultStyle();

	/**
	 * The width of the canvas.
	 */
	protected int width = defaultWidth();

	/**
	 * The height of the canvas.
	 */
	protected int height = defaultHeight();
	/**
	 * Default constructor
	 */
	public SimpleDrawingCanvas() {
		this(defaultDrawing());
	}
	/**
	 * @param drawing the drawing to construct this canvas with
	 */
	public SimpleDrawingCanvas(Drawing drawing) {
		super();
		this.drawing = drawing;
		this.listenToDrawing();
	}
	/** 
	 * Add the figure to the contents of the canvas.
	 * Reflect the change if it's visible.
	 * Become an observer on the figure.
	 *
	 * @param figure the figure to add
	 */
	public void addFigure(Figure figure) {
		drawing.addFigure(figure);
		listenToFigure(figure);
		repaint(figure.getBounds());
	}
	/** 
	 * Add the figure to the contents of the canvas, sticking it behind 
	 * an existingFigure which is already there.
	 * Reflect the change if it's visible.
	 * Become an observer on the figure.
	 *
	 * @param figure the figure to add
	 * @param existingFigure the figure to which the new figure should be behind
	 */
	public void addFigureBehind(Figure figure, Figure existingFigure) {
		drawing.addFigureBehind(figure,existingFigure);
		listenToFigure(figure);
		repaint(figure.getBounds());
	}
	/**
	 * Add the handle to the receiver.
	 * Reflect the change if it's visible.
	 * NOTE: Although this is public, it is assumed that
	 * most handles will be added/removed automatically through
	 * the process of selection.
	 * 
	 * @param handle the handle to add
	 */
	public void addHandle(Handle handle) {
		this.handles.addElement(handle);
		repaint(handle.getBounds());
	}
	/**
	 * Add the handles to the receiver.
	 * Reflect the change if it's visible.
	 * 
	 * @param handles the array of handles to add
	 */
	protected void addHandles(Handle handles[]) {
		for (int i=0; i < handles.length; i++)
			this.handles.addElement(handles[i]);
		repaint();
	}
	/**
	 * Add the handles corresponding to the figure to the receiver.
	 * NOTE: Although this is public, it is assumed that
	 * most handles will be added/removed automatically through
	 * the process of selection, but some tools may wish to be more selective.
	 * 
	 * @param figure the figure for which handles should be added.
	 */
	public void addHandles(Figure figure) {
		Handle handles[] = figure.getHandles();
		figureHandles.put(figure, handles);
		addHandles(handles);
	}
	/**
	 * Add the handles to the receiver, associating them the given figure.
	 * NOTE: Although this is public, it is assumed that
	 * most handles will be added/removed automatically through
	 * the process of selection, but some tools may wish to be more selective.
	 * 
	 * @param figure the figure for which handles are associated.
	 * @param handles the handles to add.
	 */
	public void addHandles(Figure figure, Handle handles[]) {
		Handle oldHandles[] = (Handle[])figureHandles.get(figure);
		if (oldHandles == null)
			figureHandles.put(figure, handles);
		else {
			Handle newHandles[] = new Handle[oldHandles.length + handles.length];
			System.arraycopy(oldHandles,0,newHandles,0,oldHandles.length);
			System.arraycopy(handles,0,newHandles,oldHandles.length,handles.length);
			figureHandles.put(figure, newHandles);
		}
		addHandles(handles);
	}
	/**
	 * Add the figure to the selections.
	 * Reflect the change if it's visible.
	 * 
	 * @param figure the Figure to add
	 */
	public void addSelection(Figure figure) {
		if (!selections.contains(figure)) {
			selections.addElement(figure);
			addHandles(figure);
		}
	}
	/**
	 * Remove all the handles from the receiver.
	 * Reflect the change if it's visible.
	 */
	protected void clearHandles() {
		handles.removeAllElements();
		figureHandles.clear();
		repaint();
	}
	/**
	 * Remove all the selections of the receiver.
	 * Reflect the change if it's visible.
	 * Remove any obsolete figures which were selected.
	 */
	public void clearSelections() {
		for (Enumeration e = selections.elements(); e.hasMoreElements(); ) {
			Figure figure = (Figure)e.nextElement();
			if (figure.isObsolete())
				removeFigure(figure);
		}
		selections.removeAllElements();
		clearHandles();
	}
	/**
	 * Copy the selections.  Make sure the copies are in the same order
	 * as the originals appeared.
	 */
	public void copySelections()  {
		putToClipboard(duplicateFigures(validateOrder(selections)));
	}
	/**
	 * Cut the selections
	 */
	public void cutSelections()  {
		copySelections();
		deleteSelections();
	}
	/**
	 * Answer the default color for the Component background.
	 * 
	 * @return Color
	 */
	protected Color defaultComponentBackground() {
		if ( drawing != null && drawing.isDynamicSize() ) 
			return drawing.getStyle().getBackgroundColor() ;
		else 
			return Color.gray;
	}
	/**
	 * Answer the default/initial Drawing to use.
	 * 
	 * @return	the default/initial Drawing to use
	 */
	protected static Drawing defaultDrawing() {
		return new SimpleDrawing();
	}
	/**
	 * Answer the default/initial figureHandles to use.
	 * 
	 * @return Hashtable
	 */
	protected Hashtable defaultFigureHandles() {
		return new Hashtable();
	}
	/**
	 * Answer the default/initial handles to use.
	 * 
	 * @return Vector
	 */
	protected Vector defaultHandles() {
		return new Vector(4);
	}
	/**
	 * Answer the default/initial height to use.
	 * 
	 * @return	the default/initial height to use
	 */
	protected int defaultHeight() {
		return 200;
	}
	/**
	 * Answer the default/initial selections to use.
	 * 
	 * @return Vector
	 */
	protected Vector defaultSelections() {
		return new Vector();
	}
	/**
	 * Answer the default/initial DrawingStyle to use.
	 */
	protected DrawingStyle defaultStyle() {
		return new SimpleDrawingStyle();
	}
	/**
	 * Answer the default/initial tool to use.
	 * 
	 * @return	the default/initial InputEventHandler (tool) to use
	 */
	protected InputEventHandler defaultTool() {
		return new SelectionTool(this);
	}
	/**
	 * Answer the default/initial width to use.
	 * 
	 * @return	the default/initial width to use
	 */
	protected int defaultWidth() {
		return 200;
	}
	/**
	 * Delete the selections
	 */
	public void deleteSelections()  {
		Vector oldSelections = (Vector)selections.clone();
		clearSelections();
		for (Enumeration e = oldSelections.elements(); e.hasMoreElements();) {
			removeFigure((Figure)e.nextElement());
		}
	}
	/**
	 * Called when the Drawing's size changes.
	 */
	protected void drawingSizeChange()  {
		if ( drawing.isDynamicSize() ) {
			if ( drawing.getSize().width > getSize().width ) {
				setSize( Math.max( component.getSize().width, drawing.getSize().width + buffer ), getSize().height );
			}
			if ( drawing.getSize().height > getSize().height ) {
				setSize( getSize().height, Math.max( component.getSize().height, drawing.getSize().height + buffer ) );
			}
		} else {
			setSize( drawing.getSize() );
		}
	}
	/**
	 * Duplicate all of the figures and return a new Vector
	 *
	 * @param toCopy the vector of figures to copy
	 * @return	a new Vector containing all of the figures
	 */
	protected Vector duplicateFigures(Vector toCopy)  {
		Vector copy = new Vector(toCopy.size());
		Hashtable duplicates = new Hashtable();
		for (Enumeration e = toCopy.elements(); e.hasMoreElements();) {
			Figure original = (Figure)e.nextElement();
			Figure duplicate = (Figure)original.duplicateIn(duplicates);
			duplicates.put(original,duplicate);
			copy.addElement(duplicate);
		}
		for (Enumeration e = copy.elements(); e.hasMoreElements();) {
			Figure duplicate = (Figure)e.nextElement();
			duplicate.postDuplicate(duplicates);
		}
		return copy;
	}
	/** 
	 * Answer the figure at a given point
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the Figure at the given point; null if none found
	 */
	public Figure figureAt(int x, int y) {
		return drawing.figureAt(x,y);
	}
	/**
	 * Called when a figure changes.
	 */
	protected void figureChange(PropertyChangeEvent evt)  {
		Figure figure = (Figure)evt.getSource();
		Rectangle area = figure.getBounds();
		repaint(area);
	}
	/**
	 * Called when a figure's location changes.
	 */
	protected void figureLocationChange(PropertyChangeEvent evt)  {
		Figure figure = (Figure)evt.getSource();
		Rectangle area = figure.getBounds();
		Object newValue = evt.getNewValue();
		Object oldValue = evt.getOldValue();
		if (oldValue != null) {
			Point oldPoint = (Point)oldValue;
			area.add(oldPoint);
			area.add(oldPoint.x + area.width, oldPoint.y + area.height);
		}
		Point newPoint = (Point)newValue;
		area = new Rectangle(area.x, area.y, area.width, area.height);
		area.add(newPoint);
		area.add(newPoint.x + area.width, newPoint.y + area.height);
		repaint(area);
	}
	/** 
	 * Answer a FiguresEnumeration over the figures of the receiver.
	 * 
	 * @return	a FiguresEnumeration over the figures of the receiver
	 */
	public FigureEnumeration figures() {
		return drawing.figures();
	}
	/**
	 * Called when a figure's shape changes.
	 */
	protected void figureShapeChange(PropertyChangeEvent evt)  {
		Figure figure = (Figure)evt.getSource();
		Rectangle area = figure.getBounds();
		Object newValue = evt.getNewValue();
		Object oldValue = evt.getOldValue();
		if (oldValue != null)
			area = area.union((Rectangle)oldValue);
		area = area.union((Rectangle)newValue);
		repaint(area);
	}
	/**
	 * Called when a figure's size changes.
	 */
	protected void figureSizeChange(PropertyChangeEvent evt)  {
		Figure figure = (Figure)evt.getSource();
		Rectangle area = figure.getBounds();
		Dimension newSize = (Dimension)evt.getNewValue();
		Dimension oldSize = (Dimension)evt.getOldValue();
		if (newSize.width > area.width) 
			area = new Rectangle(area.x, area.y, newSize.width, newSize.height);
		if (newSize.height > area.height)
			area = new Rectangle(area.x, area.y, area.width, oldSize.height);
		repaint(area);
	}
	/** 
	 * Returns the current bounds of the receiver.
	 * 
	 * @return	a Rectangle representing the current bounds
	 */
	public Rectangle getBounds() {
		if ( drawing.isDynamicSize() ) {
			if ( width < component.getSize().width ) width = component.getSize().width;
			if ( height < component.getSize().height ) height = component.getSize().height;
		}
		return new Rectangle( 0, 0, width, height );
	}
	/**
	 * Answers the Clipboard for this canvas.
	 *
	 * @return the Clipboard for this canvas.
	 */
	protected Clipboard getClipboard() {
		return clipboard;
	}
	/**
	 * Answers the Component we are drawing/displaying.
	 *
	 * @return the Component we are drawing/displaying.
	 */
	public Component getComponent() {
		return component;
	}
	/**
	 * Answer a Figure (or null) created based on the string (e.g. from the clipboard)
	 * 
	 * @param string the string to create the new figure from
	 * @return	a new Figure created from the string
	 */
	protected Figure getFigureFromString(String string)  {
		Figure newFigure = new com.rolemodelsoft.drawlet.text.TextLabel(string);
		newFigure.setStyle(this.getStyle());
		return newFigure;
	}
	/**
	 * Answer the stuff on the clipboard
	 * 
	 * @return	the clipboard as a Vector
	 */
	protected Vector getFromClipboard()  {
		return getFromClipboard(getClipboard());

	}
	/**
	 * Answer the stuff on the clipboard
	 *
	 * @param clipboard the Clipboard to get the data from.
	 * @return	the clipboard as a Vector
	 */
	protected Vector getFromClipboard(Clipboard clipboard)  {
		Transferable transfer = clipboard.getContents(this);
		if (transfer == null)  {
			System.out.println("nothing to paste");
			Toolkit.getDefaultToolkit().beep();
			return new Vector();
		}
		try {
			if (transfer.isDataFlavorSupported(FigureTransfer.figuresFlavor))
				return (Vector)transfer.getTransferData(FigureTransfer.figuresFlavor);
			else 
			 if (transfer.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String string = (String)transfer.getTransferData(DataFlavor.stringFlavor);
				Figure label = getFigureFromString(string);
				Vector container = new Vector(1);
				container.addElement(label);
				return container;
			}
		} catch (UnsupportedFlavorException e) {
			System.out.println("Problem pasting: " + e);
		} catch (java.io.IOException e) {
			System.out.println("Problem pasting: " + e);
		}
		Toolkit.getDefaultToolkit().beep();
		return new Vector();

	}
	/**
	 * Answer the stuff on the system clipboard
	 * 
	 * @return	the clipboard as a Vector
	 */
	protected Vector getFromSystemClipboard()  {
		return getFromClipboard(getSystemClipboard());
	}
	/**
	 * Answer the Color to use in displaying handles.
	 * 
	 * @return	the Color to use when displaying handles
	 */
	protected Color getHandleColor() {
		return style.getForegroundColor();
	}
	/** 
	 * Answer the handles of the receiver.  The returned array 
	 * and its contents should be treated as read-only.
	 * 
	 * @return	an array of Handles representing the handles of the receiver;
	 * should be treated as read-only
	 */
	public Handle[] getHandles() {
		Handle myHandles[] = new Handle[handles.size()];
		handles.copyInto(myHandles);
		return myHandles;
	}
	/** 
	 * Answer the proper locator to be used for the given coordinates.
	 * This method adjusts depending on whether the drawing is dynamically
	 * sized or not.
	 *
	 * @param x the x coordinate to return a Locator for.
	 * @param y the y coordinate to return a Locator for.
	 * @return a Locator corresponding to the given x and y coordinates.
	 */
	public Locator getLocator( int x, int y ) {
		if ( drawing.isDynamicSize() ) {
			return new DrawingPoint( x, y );
		}
		int locX = Math.min( x, width - 1 );
		locX = Math.max( locX, 0 );
		int locY = Math.min( y, height - 1 );
		locY = Math.max( locY, 0 );
		return new DrawingPoint( locX, locY );
	}
	/** 
	 * Answer the selections of the receiver.  The returned array 
	 * and its contents should be treated as read-only.
	 * 
	 * @return	an array of Figures representing the selections of the receiver;
	 * should be treated as read-only
	 */
	public Figure[] getSelections() {
		Figure mySelections[] = new Figure[selections.size()];
		selections.copyInto(mySelections);
		return mySelections;
	}
	/** 
	 * Returns the current size of the receiver.
	 * 
	 * @return	a Dimension representing the current size
	 */
	public Dimension getSize() {
		return new Dimension( getBounds().width, getBounds().height );
	}
	/** 
	 * Answer the style which defines how to paint on the canvas.
	 * 
	 * @return	the DrawingStyle which defines how to paint on the canvas
	 */
	public DrawingStyle getStyle() {
		return style;
	}
	/**
	 * Answer the system Clipboard.
	 *
	 * @return the Clipboard for the system. At this point (i.e. in Java 1.1) this is basically only good
	 * for strings.
	 */
	protected Clipboard getSystemClipboard() {
		return Toolkit.getDefaultToolkit().getSystemClipboard();
	}
	/**
	 * Answer the active tool
	 * 
	 * @return	the active InputEventHandler (tool)
	 */
	public InputEventHandler getTool() {
		if (tool == null) return defaultTool();
		return tool;
	}
	/** 
	 * Answer the handle at a given point
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the Handle at the given point; null if none found
	 */
	public Handle handleAt(int x, int y) {
		for (Enumeration e = new ReverseVectorEnumerator(handles); e.hasMoreElements();) {
			Handle handle = (Handle)e.nextElement();
			if (handle.contains(x,y)) return handle;
		}
		return null;
	}
	/** 
	 * Stop listening to all of the figures of the drawing.
	 */
	protected void ignoreDrawing() {
		for (FigureEnumeration e = figures(); e.hasMoreElements();) {
			ignoreFigure(e.nextElement());
		}
		drawing.removePropertyChangeListener( this );
	}
	/** 
	 * Stop listening to the specified figure.
	 * 
	 * @param figure the figure to stop listening to
	 */
	protected void ignoreFigure(Figure figure) {
		figure.removePropertyChangeListener(this);
	}
	/**
	 * Invoked when a key has been pressed.
	 * 
	 * @param e the event
	 */
	public void keyPressed(KeyEvent e) {
		getTool().keyPressed(e);
	}
	/**
	 * Invoked when a key has been released.
	 * 
	 * @param e the event
	 */
	public void keyReleased(KeyEvent e) {
		getTool().keyReleased(e);
	}
	/**
	 * Invoked when a key has been typed.
	 * This event occurs when a key press is followed by a key release.
	 * 
	 * @param e the event
	 */
	public void keyTyped(KeyEvent e) {
		getTool().keyTyped(e);
		if (!e.isConsumed())
			processKey(e);
	}
	/** 
	 * Begin listening to all of the figures of the drawing.
	 */
	protected void listenToDrawing() {
		for (FigureEnumeration e = figures(); e.hasMoreElements();) {
			listenToFigure(e.nextElement());
		}
		drawing.addPropertyChangeListener( this );
	}
	/** 
	 * Begin listening to the specified figure.
	 * 
	 * @param figure the figure to begin listening to
	 */
	protected void listenToFigure(Figure figure) {
		figure.addPropertyChangeListener(this);
	}
	/** 
	 * Returns the minimum size of the receiver.
	 * 
	 * @return	a Dimension representing the minimum size.
	 */
	protected Dimension minimumSize() {
		Dimension compSize, size = null;
		compSize = component.getSize();
		size.width = Math.max( compSize.width, drawing.getSize().width );
		size.height = Math.max( compSize.height, drawing.getSize().height );		
		return size;
	}
	/**
	 * Invoked when the mouse has been clicked on a component.
	 * 
	 * @param e the event
	 */
	public void mouseClicked(MouseEvent e) {
		getTool().mouseClicked(e);
	}
	/**
	 * Invoked when a mouse button is pressed on a component and then 
	 * dragged.  Mouse drag events will continue to be delivered to
	 * the component where the first originated until the mouse button is
	 * released (regardless of whether the mouse position is within the
	 * bounds of the component).
	 * 
	 * @param e the event
	 */
	public void mouseDragged(MouseEvent e) {
		getTool().mouseDragged(e);
	}
	/**
	 * Invoked when the mouse enters a component.
	 * 
	 * @param e the event
	 */
	public void mouseEntered(MouseEvent e) {
		getTool().mouseEntered(e);
	}
	/**
	 * Invoked when the mouse exits a component.
	 * 
	 * @param e the event
	 */
	public void mouseExited(MouseEvent e) {
		getTool().mouseExited(e);
	}
	/**
	 * Invoked when the mouse button has been moved on a component
	 * (with no buttons no down).
	 * 
	 * @param e the event
	 */
	public void mouseMoved(MouseEvent e) {
		getTool().mouseMoved(e);
	}
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 * 
	 * @param e the event
	 */
	public void mousePressed(MouseEvent e) {
		getComponent().requestFocus();
		getTool().mousePressed(e);
	}
	/**
	 * Invoked when a mouse button has been released on a component.
	 * 
	 * @param e the event
	 */
	public void mouseReleased(MouseEvent e) {
		getTool().mouseReleased(e);
	}
	/** 
	 * Move the figure behind an existingFigure if it is not already there.
	 * Reflect the change if it's visible.
	 * 
	 * @param figure the figure to move
	 * @param existingFigure the figure to which the new figure should be behind
	 */
	public void moveFigureBehind(Figure figure, Figure existingFigure) {
		try {
			drawing.moveFigureBehind(figure,existingFigure);
			repaint(figure.getBounds());
		} catch (IllegalArgumentException e) {}; // ignore for now
	}
	/** 
	 * Move the figure in front of an existingFigure if it is not already there.
	 * Reflect the change if it's visible.
	 * 
	 * @param figure the figure to move
	 * @param existingFigure the figure to which the new figure should be in front
	 */
	public void moveFigureInFront(Figure figure, Figure existingFigure) {
		try {
			drawing.moveFigureInFront(figure,existingFigure);
			repaint(figure.getBounds());
		} catch (IllegalArgumentException e) {}; // ignore for now
	}
	/** 
	 * Move the figure behind all other figures.
	 * Reflect the change if it's visible.
	 * 
	 * @param figure the figure to move
	 */
	public void moveFigureToBack(Figure figure) {
		try {
			drawing.moveFigureToBack(figure);
			repaint(figure.getBounds());
		} catch (IllegalArgumentException e) {}; // ignore for now
	}
	/** 
	 * Move the figure in front of all other figures.
	 * Reflect the change if it's visible.
	 * 
	 * @param figure the figure to move
	 */
	public void moveFigureToFront(Figure figure) {
		try {
			drawing.moveFigureToFront(figure);
			repaint(figure.getBounds());
		} catch (IllegalArgumentException e) {}; // ignore for now
	}
	/** 
	 * Move the selected figures behind all other figures.
	 * Reflect the change if it's visible.
	 */
	public void moveSelectionsToBack() {
		for (Enumeration e = new ReverseVectorEnumerator(selections); e.hasMoreElements(); )
			moveFigureToBack((Figure)e.nextElement());
	}
	/** 
	 * Move the selected figures in front of all other figures.
	 * Reflect the change if it's visible.
	 */
	public void moveSelectionsToFront() {
		for (Enumeration e = selections.elements(); e.hasMoreElements(); )
			moveFigureToFront((Figure)e.nextElement());
	}
	/** 
	 * Answer the figure at a given point excluding the identified figure
	 *
	 * @param figure the figure to exclude from the search
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the Figure at the given point, excluding the identified figure;
	 * null if none found
	 */
	public Figure otherFigureAt(Figure excludedFigure, int x, int y) {
		return drawing.otherFigureAt(excludedFigure, x, y);
	}
	/** 
	 * Paints the canvas.
	 *
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g) {
		g.setClip( getBounds() );
		paintBackground(g);
		paintForeground(g);
		paintHandles(g);
	}
	/** 
	 * Paints the background of the canvas.
	 *
	 * @param g the specified Graphics window
	 */
	protected void paintBackground(Graphics g) {
		g.setColor(getStyle().getBackgroundColor());
		g.fillRect(0,0,getSize().width,getSize().height);
	}
	/** 
	 * Paints the foreground of the canvas.
	 *
	 * @param g the specified Graphics window
	 */
	protected void paintForeground(Graphics g) {
		g.setColor(getStyle().getForegroundColor());
		drawing.paint(g);
	}
	/** 
	 * Paints the handles on the canvas.
	 * Don't bother asking handles to paint themselves if they
	 * are not in the clipped region.
	 *
	 * @param g the specified Graphics window
	 */
	protected void paintHandles(Graphics g) {
		Rectangle clip = g.getClipBounds();
		g.setColor(getHandleColor());
		if (clip == null || clip.height == -1 || clip.width == -1) {
			for (Enumeration e = handles.elements() ; e.hasMoreElements() ;) {
 	  	 		((Handle)e.nextElement()).paint(g);
			}
		} else {
			for (Enumeration e = handles.elements() ; e.hasMoreElements() ;) {
				Handle h = (Handle)e.nextElement();
		 		if (h.intersects(clip))
					h.paint(g);
			}
		}
	}
	/**
	 * Paste from the clipboard.
	 * NOTE: Currently multiple pastes of the same thing shows up in the same place.
	 * Need to review entire approach to copy/cut/paste based on 1.1 model.
	 */
	public void paste()  {
		clearSelections();
		Vector duplicates = duplicateFigures(getFromClipboard());
		for (Enumeration e = duplicates.elements(); e.hasMoreElements();) {
			Figure duplicate = (Figure)e.nextElement();
			duplicate.translate(5,5);
			addFigure(duplicate);
			addSelection(duplicate);
		}
	}
	/**
	 * Paste from the system clipboard.
	 * NOTE: Currently multiple pastes of the same thing shows up in the same place.
	 * Need to review entire approach to copy/cut/paste based on 1.1 model.
	 */
	public void pasteFromSystem()  {
		clearSelections();
		Vector duplicates = duplicateFigures(getFromSystemClipboard());
		for (Enumeration e = duplicates.elements(); e.hasMoreElements();) {
			Figure duplicate = (Figure)e.nextElement();
			duplicate.translate(5,5);
			addFigure(duplicate);
			addSelection(duplicate);
		}
	}
	/**
	 * Called if a character is typed.  Handle special keys to perform
	 * certain operations.
	 * 
	 * @param evt the event
	 * @see #keyTyped
	 */
	protected void processKey(KeyEvent evt) {
		char key = evt.getKeyChar();
		switch (key) {
			case '\b' :
			case 127 : // delete key
				deleteSelections();
				evt.consume();
				return;
			case 2 : // Ctrl-b
				moveSelectionsToBack();
				evt.consume();
				return;
			case 3 : // Ctrl-c
				copySelections();
				evt.consume();
				return;
			case 6 : // Ctrl-f
				moveSelectionsToFront();
				evt.consume();
				return;
			case 24 : // Ctrl-x
				cutSelections();
				evt.consume();
				return;
			case 22 : // Ctrl-v
				paste();
				evt.consume();
				return;
		}
		return;
	}
	/**
	 * Called when a property changes.
	 * 
	 * @param evt the event
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof Figure) {
			String propertyName = evt.getPropertyName();
			if (propertyName == Figure.SHAPE_PROPERTY) {
				figureShapeChange(evt);
			} else if (propertyName == Figure.LOCATION_PROPERTY) {
				figureLocationChange(evt);
			} else if (propertyName == Figure.SIZE_PROPERTY) {
				figureSizeChange(evt);
			} else {
				figureChange(evt);
			}
			if ( drawing.isDynamicSize() ) drawingSizeChange();
		} else if ( evt.getSource().equals( drawing ) ) {
			if ( evt.getPropertyName() == Drawing.SIZE_PROPERTY ) {
				drawingSizeChange();
			}
		}
	}
	/**
	 * Add the stuff to the clipboard
	 * 
	 * @param stuff the vector to add to the clipboard
	 */
	protected void putToClipboard(Vector stuff)  {
		// clipboard = stuff;

		Clipboard clipboard = getClipboard();
		FigureTransfer transfer = new FigureTransfer(stuff);
		clipboard.setContents(transfer,transfer);

		// If it has a string, put it to the System clipboard, too.
		if ( stuff.size() == 1 && stuff.firstElement() instanceof StringHolder ) {
			clipboard = getSystemClipboard();
			StringSelection stringSelection = new StringSelection( ((StringHolder)stuff.firstElement()).getString() );
			clipboard.setContents( stringSelection, stringSelection );
		}
	}
	/** 
	 * Remove the figure.
	 * Reflect the change if it's visible.
	 * Remove the receiver as an Observer of
	 * the figure and disconnect the figure completely.
	 * 
	 * @param figure the figure to remove
	 */
	public void removeFigure(Figure figure) {
		drawing.removeFigure(figure);
		ignoreFigure(figure);
		repaint(figure.getBounds());
		figure.disconnect(); // any other clean up that needs to happen
	}
	/**
	 * Remove the handle from the receiver.
	 * Reflect the change if it's visible.
	 * NOTE: Although this is public, it is assumed that
	 * most handles will be added/removed automatically through
	 * the process of selection.  Since it is public, don't assume
	 * the handle asked for is actually present.
	 * 
	 * @param handle the handle to remove
	 */
	public void removeHandle(Handle handle) {
		if (this.handles.removeElement(handle))
			repaint(handle.getBounds());
	}
	/**
	 * Remove the handles to the receiver.
	 * Reflect the change if it's visible.
	 * 
	 * @param handles the array of handles to be removed
	 */
	protected void removeHandles(Handle handles[]) {
		for (int i=0; i < handles.length; i++)
			this.handles.removeElement(handles[i]);
		repaint();
	}
	/**
	 * Remove the handles corresponding to the figure to the receiver
	 * NOTE: Although this is public, it is assumed that
	 * most handles will be added/removed automatically through
	 * the process of selection, but some tools may wish to be more selective.
	 * 
	 * @param figure the figure for which handles should be removed.
	 */
	public void removeHandles(Figure figure) {
		Object handles = figureHandles.remove(figure);
		if (handles != null)
			removeHandles((Handle[])handles);
	}
	/**
	 * Remove the figure from the selections.
	 * Reflect the change if it's visible.
	 * If a figure is obsolete, remove it.
	 *
	 * @param figure the figure being deselected
	 */
	public void removeSelection(Figure figure) {
		removeHandles(figure);
		selections.removeElement(figure);
		if (figure.isObsolete())
			removeFigure(figure);
	}
	/** 
	 * Repaint the canvas.
	 */
	protected void repaint() {
		component.repaint();
	}
	/** 
	 * Repaints part of the canvas. This will result in a
	 * call to update as soon as possible.
	 * 
	 * @param rectangle is the region to be repainted
	 * @see #repaint
	 */
	public void repaint(Rectangle rectangle) {
		/*
		 * Note that we're fudging by a pixel in every direction to avoid
		 * differences in the way various drawing primitives determine 
		 * where to start/stop drawing
		 */
		component.repaint(rectangle.x - 1, rectangle.y - 1, rectangle.width + 2, rectangle.height + 2);
	}
	/**
	 * Make the figure the only selection
	 * Reflect the change if it's visible.
	 * 
	 * @param figure the figure being deselected
	 */
	public void select(Figure figure) {
		clearSelections();
		addSelection(figure);
	}
	/**
	 * Set the Component to draw/display.
	 *
	 * @param component the component to draw/display.
	 */
	public void setComponent(Component component) {
		this.component = component;
		drawingSizeChange();
	}
	/** 
	 * Set the drawing associated with this canvas.
	 * Reflect the change if it's visible.
	 * Become an observer on the drawing.
	 *
	 * @param newDrawing the drawing to associate with
	 */
	public void setDrawing(Drawing newDrawing) {
		if (this.drawing == newDrawing) 
			return;
		if (this.drawing != null) {
			clearSelections();
			ignoreDrawing();
		}
		this.drawing = newDrawing;
		if (this.drawing != null) {
			listenToDrawing();
			style.setBackgroundColor(newDrawing.getStyle().getBackgroundColor());
			component.setBackground(defaultComponentBackground());
		}
		repaint();
	}
	/** 
	 * Set the size of the receiver.
	 *
	 * @param width the new width of the receiver.
	 * @param width the new height of the receiver.
	 */
	public void setSize( int width, int height ) {
		this.width = width;
		this.height = height;
	}
	/** 
	 * Set the size of the receiver.
	 *
	 * @param size the new size of the receiver.
	 */
	public void setSize( Dimension size ) {
		setSize( size.width, size.height );
	}
	/** 
	 * Set the style defining how to paint on the canvas.
	 * Change the foreground and background of the component immediately.
	 *
	 * @param style the specified DrawingStyle
	 */
	public void setStyle(DrawingStyle style) {
		this.style = style;
		drawing.setStyle(style);
		component.setForeground(style.getForegroundColor());
		component.setBackground(defaultComponentBackground());
	}
	/**
	 * Set the active tool
	 * 
	 * @param newTool the tool to make active
	 */
	public void setTool(InputEventHandler newTool) {
		if (tool instanceof Handle)
			((Handle)tool).releaseControl(this);
		tool = newTool;
	}
	/**
	 * Toggle whether or not the figure is selected
	 * Reflect the change if it's visible.
	 * 
	 * @param figure the figure of interest
	 */
	public void toggleSelection(Figure figure) {
		if (selections.contains(figure)) removeSelection(figure);
		else addSelection(figure);
	}
	/**
	 * Take appropriate action when the tool has completed its task.
	 * Default behavior is to do nothing.  Subclasses may wish to give
	 * control back to another tool.
	 * 
	 * @param tool the tool which completed its task
	 */
	public void toolTaskCompleted(InputEventHandler tool) {
	}
	/**
	 * Answer a Vector containing the figures in the same order they appear in
	 * the receiver.
	 *
	 * @param unordered the Vector of figures that need to be ordered
	 * @return	a Vector containing the figures in the same order they appear
	 * in the receiver
	 */
	protected Vector validateOrder(Vector unordered)  {
		Vector copy = new Vector(unordered.size());
		int positions[] = new int[unordered.size()];
		/*
		 * Simple sort since we probably aren't dealing with a lot of elements.
		 * For each element: 
		 *	get position in receiver,
		 *	determine how many we've already seen belong in front of it,
		 *	stick it in the appropriate place
		 */
		int i = 0;
		for (Enumeration e = unordered.elements(); e. hasMoreElements(); i++) {
			Object element = e.nextElement();
			int j = 0;
			for (FigureEnumeration f = figures(); f.hasMoreElements(); j++) {
				if (f.nextElement() == element) {
					positions[i] = j;
					break;
				}
			}
			int count = 0;
			for (j=0; j < i; j++)
				if (positions[i] > positions[j]) count++;
			copy.insertElementAt(element,count);
		}
		return copy;
	}
}
