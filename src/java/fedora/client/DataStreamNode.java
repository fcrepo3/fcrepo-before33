package fedora.client;

import com.rolemodelsoft.drawlet.*;
import com.rolemodelsoft.drawlet.basics.*;
import com.rolemodelsoft.drawlet.text.*;
import com.rolemodelsoft.drawlet.shapes.lines.*;
import com.rolemodelsoft.drawlet.shapes.rectangles.*;
import com.rolemodelsoft.drawlet.util.*;
import java.awt.*;

/**
 * Based on arrowgraphnode
 * @version 	1.1.6, 12/29/98
 */
 
public class DataStreamNode extends RectangleShape implements LabelHolder {
//	 static final long serialVersionUID = -5689584309732244534L;

    protected int kind;
    
    public static int METADATA=0;
    public static int CONTENT=1;
    
    public DataStreamNode(int kind) {
        super();
        this.kind=kind;
    }
	
	/**
	 * the label associated with this graphnode.
	 */
	protected String string = defaultString();

	/**
	 * the color of the text in the graphnode.
	 */
	protected Color textColor = defaultTextColor();

	/**
	 * the renderer responsible for drawing the label.
	 */
	transient protected StringRenderer renderer;
	/**
	 * Answer a new line creation handle
	 */
	protected ConnectedLineCreationHandle basicNewLineCreationHandle() {
        Locator where;
        if (kind==METADATA) {
            where=new FigureRelativePoint(this,1.0,0.5);
        } else {
            where=new FigureRelativePoint(this,0,0.5);
        }
//		return new ConnectedLineCreationHandle(this);
		return new AdornedLineCreationHandle(this, where);
	}
	/**
	 * Denote that the shape changed.
	 * 
	 * @param rect the old bounds.
	 */
	protected void changedShape(Rectangle rect) {
		renderer = null;
		super.changedShape(rect);
	}
	/**
	 * Denote that size changed.
	 * 
	 * @param dimension the old dimensions.
	 */
	protected void changedSize(Dimension dimension) {
		renderer = null;
		super.changedSize(dimension);
	}
	/**
	 * Answer the default/initial value for height
	 * 
	 * @return	an integer representing the default/initial value for height
	 */
	protected int defaultHeight() {
		return 30;
	}
	/**
	 * Answer the default/initial value for string
	 * 
	 * @return	a String representing the default/inital value for string
	 */
	protected String defaultString() {
		return "datastream";
	}
	/**
	 * Answer the default/initial value for textColor
	 * 
	 * @return	a Color representing the default/initial value for textColor
	 */
	protected Color defaultTextColor() {
		return Color.black;
	}
	/**
	 * Answer the default/initial value for width
	 * 
	 * @return	an integer representing the default/initial value for width
	 */
	protected int defaultWidth() {
		return 140;
	}
	/** 
	 * Answers a Handle that will provide 
	 * editing capabilities on the receiver.
	 *
	 * @param x the x coordinate to potentially begin editing
	 * @param y the y coordinate to potentially begin editing
	 * @return	a Handle that will provide editing capabilities on
	 * the receiver
	 */
	public Handle editTool(int x, int y)  {
		return new LabelEditHandle(this);
	}
	/** 
	 * Answer the handles associated with the receiver.
	 * 
	 * @return	an array of the Handles associated with the receiver
	 */
	public Handle[] getHandles() {
    
    /* don't show resize handles
		Handle superHandles[] = super.getHandles();
		Handle handles[] = new Handle[superHandles.length + 1];
		System.arraycopy(superHandles,0,handles,0,superHandles.length);
		handles[superHandles.length] = basicNewLineCreationHandle();
    */
        
        Handle handles[]=new Handle[1];
        handles[0]=basicNewLineCreationHandle();
		return handles;
	}
	/** 
	 * Returns the current bounds of the label.
	 * 
	 * @return	a Rectangle representing the current bounds of the label
	 */
	public Rectangle getLabelBounds()  {
		return new Rectangle(x + 5, y + 2, width - 10, height - 4);
	}
	/** 
	 * Answer the renderer to paint the label.
	 * 
	 * @return	the StringRender to paint the label
	 */
	protected StringRenderer getRenderer()  {
		if (renderer == null) {
			Rectangle myLabelBounds = getLabelBounds();
			renderer = new BasicStringComposer(string, myLabelBounds.width, myLabelBounds.height);
		}
		return renderer;
	}
	/** 
	 * Answer the string the figure paints.
	 * 
	 * @return	the String the figure paints
	 */
	public String getString()  {
		return string;
	}
	/** 
	 * Answer the style which defines how to paint the figure.
	 * 
	 * @return	the DrawingStyle which defines how to paint the figure
	 */
	public DrawingStyle getStyle()  {
		DrawingStyle style = super.getStyle();
		style.setTextColor(textColor);
		return style;
	}
	/**
	 * Answer the Color to use when drawing text.
	 * 
	 * @return	the Color to use when drawing text
	 */
	public Color getTextColor() {
		return textColor;
	}
	/** 
	 * Paints the figure.
	 *
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g)  {
		super.paint(g);
		//g.drawRect(getLabelBounds().x, getLabelBounds().y, getLabelBounds().width, getLabelBounds().height);
		paintText(g);
	}
	/** 
	 * Paints the text.
	 *
	 * @param g the specified Graphics window
	 */
	public void paintText(Graphics original)  {
		Graphics g = original.create();
		g.setColor(textColor);
		Rectangle myLabelBounds = getLabelBounds();
		g.clipRect(myLabelBounds.x, myLabelBounds.y, myLabelBounds.width, myLabelBounds.height);
		getRenderer().paint(g, myLabelBounds.x, myLabelBounds.y);
	}
	/** 
	 * Answers a Locator corresponding to a significant point on the receiver 
	 * that will serve as a connection to the other figure.
	 * By default, make it the middle of the receiver.
	 * Subclasses may wish to do something more meaningful.
	 *
	 * @param x the x coordinate of the requested locator
	 * @param y the y coordinate of the requested locator
	 */
	public Locator requestConnection(Figure requestor, int x, int y) {
	if (requestor == this)
		return null;
    DataStreamNode r=(DataStreamNode)requestor;
    if (r.kind==this.kind)
        return null;  // can't link from same type
    if (r.kind==METADATA) {
	    return new FigureRelativePoint(this,0,0.5);
    } else {
	    return new FigureRelativePoint(this,1.0,0.5);
    }
	}
	/**
	 * Flush caches with respect to determining size.  This is a hook method.
	 * Subclasses may wish to override.
	 */
	protected void resetSizeCache() {
		renderer = null;
	}
	/** 
	 * Set the string the figure paints.
	 *
	 * @param newString the string to paint
	 */
	public void setString(String newString) {
		String oldString = string;
		string = newString;
		renderer = null;
		firePropertyChange(STRING_PROPERTY, oldString, newString);
	}
	/** 
	 * Set the style defining how to paint the figure.
	 * 
	 * @param style the specified DrawingStyle
	 */
	public void setStyle(DrawingStyle style)  {
		super.setStyle(style);
		if (style != null) {
			setTextColor(style.getTextColor());
		}
	}
	/**
	 * Set the Color to use when drawing text.
	 * 
	 * @param color the color
	 */
	public void setTextColor(Color color) {
		Color oldColor = textColor;
		textColor = color;
		firePropertyChange(TEXT_COLOR_PROPERTY, oldColor, color);
	}
}

