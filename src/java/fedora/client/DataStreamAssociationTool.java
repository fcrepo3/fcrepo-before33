package fedora.client;

import com.rolemodelsoft.drawlet.*;
import com.rolemodelsoft.drawlet.basics.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import java.util.Enumeration;

/**
 * This provides basic functionality necessary to select a Figure(s) on a DrawingCanvas
 * and/or pass control onto Handles.  There could be other ways to provide this
 * functionality, but this seems like a safe generic way to do it.
 *
 * @version 	1.1.6, 12/28/98
 */
 
public class DataStreamAssociationTool extends CanvasTool {

	/**
	 * The x coordinate where the mouse went down.
	 * This is used to determine what to do when other events occur.
	 */
	protected int referenceX;

	/**
	 * The y coordinate where the mouse went down.
	 * This is used to determine what to do when other events occur.
	 */
	protected int referenceY;

	/**
	 * The figure where the mouse went down.
	 * This is used to determine what to do when other events occur.
	 */
	protected Figure referenceFigure;

	/** 
	 * Constructs and initializes a new instance of a tool to select figures
	 * on a DrawingCanvas
	 *
	 * @param canvas the canvas from which to select figures.
	 */
	public DataStreamAssociationTool(DrawingCanvas canvas) {
	this.canvas = canvas;
	}
	/**
	 * Select based on a box.
	 *
	 * @param e the event caused this to happen
	 * @param x the x coordinate of the mouse
	 * @param y the y coordinate of the mouse
	 * @see #mouseDragged
	 */
     /*
	public void boxSelect(MouseEvent e, int x, int y) {
		(new BoxSelectionHandle(referenceX, referenceY, x, y)).takeControl(canvas);
		e.consume();
	}

 no such thing yet

    */
    
	/**
	 * Called if the mouse is double-clicked.  Try passing it on to a handle if
	 * there is one present at the point.  Otherwise, see if there is a figure
	 * who might want to give control to an editTool.
	 *
	 * @param e the event 
	 * @see #mouseClicked
	 */
	protected void mouseDoubleClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		Handle handle = canvas.handleAt(x, y);
		if (handle != null) {
			handle.takeControl(canvas);
			handle.mouseClicked(e);
			return;
		}
		Figure figure = canvas.figureAt(x, y);
		if (figure != null) {
			referenceFigure = figure;
			Handle editTool = figure.editTool(x, y);
			if (editTool != null)
				editTool.takeControl(canvas);
			e.consume();
			return;
		}
		referenceFigure = null;
        
	}
    
	/**
	 * Called if the mouse is dragged (the mouse button is down).
	 * If we didn't put it down on top of a figure, do a box select,
	 * otherwise, move the selected figures (and handles);
	 *
	 * @param e the event
	 */
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (referenceFigure == null || canvas.getSelections().length == 0) {
// don't allow box select...yet			boxSelect(e, x, y);
			return;
		}
/* don't allow moving of figures
		Rectangle selectionBounds = canvas.getSelections()[0].getBounds();
		Figure selections[] = canvas.getSelections();
		for (int i = 1; i < selections.length; i++) {
			selectionBounds = selectionBounds.union(selections[i].getBounds());
		}

		int moveX = x - referenceX;
		int moveY = y - referenceY;

		Locator moveLocator = canvas.getLocator( selectionBounds.x + moveX, selectionBounds.y + moveY );
		moveX = moveLocator.x() - selectionBounds.x;
		moveY = moveLocator.y() - selectionBounds.y;
		
		moveLocator = canvas.getLocator( selectionBounds.x + selectionBounds.width + moveX, selectionBounds.y + selectionBounds.height + moveY );
		if ( selectionBounds.x + selectionBounds.width + moveX > canvas.getBounds().width - 1 ) {
			moveX = moveLocator.x() - selectionBounds.x - selectionBounds.width;
		}
		if ( selectionBounds.y + selectionBounds.height + moveY > canvas.getBounds().height - 1 ) {
			moveY = moveLocator.y() - selectionBounds.y - selectionBounds.height;
		}
		
		Rectangle bounds = ((Figure) canvas.getSelections()[0]).getBounds();
		Handle handles[] = canvas.getHandles();
		for (int i = 0; i < handles.length; i++) {
			bounds = bounds.union(handles[i].getBounds());
		}
		for (int i = 0; i < selections.length; i++) {
			Figure figure = selections[i];
			bounds = bounds.union(figure.getBounds());
			figure.translate(moveX, moveY);
			bounds = bounds.union(figure.getBounds());
		}
		for (int i = 0; i < handles.length; i++) {
			bounds = bounds.union(handles[i].getBounds());
		}
		canvas.repaint(bounds);
		if (moveX != 0)
			referenceX = x;
		if (moveY != 0)
			referenceY = y;
		e.consume();
        
*/
	}
    
	/**
	 * Called if the mouse is down.
	 * Keep track of where the mouse went down and then look for handles to
	 * take control, or figures to select/deselect
	 *
	 * @param e the event 
	 */
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		referenceX = x;
		referenceY = y;
		Handle handle = canvas.handleAt(x, y);
		if (handle != null) {
			handle.takeControl(canvas);
			handle.mousePressed(e);
			return;
		}
		e.consume();
		Figure figure = canvas.figureAt(x, y);
		if (figure != null) {
			referenceFigure = figure;
			if (e.isShiftDown())
				canvas.addSelection(figure);
			else {
				if (e.isControlDown())
					canvas.toggleSelection(figure);
				else
					canvas.select(figure);
			}
		} else {
			referenceFigure = null;
			if (!e.isShiftDown() && !e.isControlDown())
				canvas.clearSelections();
		}
	}
}
