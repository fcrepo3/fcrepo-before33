package fedora.client;

import com.rolemodelsoft.drawlet.jfc.JDrawingCanvasComponent;
import com.rolemodelsoft.drawlet.Drawing;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyListener;
import java.awt.Rectangle;
import javax.swing.Scrollable;
import javax.swing.JScrollPane;

public class ScrollableJDrawingCanvasComponent
        extends JDrawingCanvasComponent 
        implements Scrollable {
        
    private SimpleJDrawingCanvas m_canvas;

    public ScrollableJDrawingCanvasComponent(Drawing d) {
        super();
        m_canvas=new SimpleJDrawingCanvas(d);
        setCanvas(m_canvas);
		if (m_canvas instanceof MouseListener)
			this.addMouseListener((MouseListener)m_canvas);
		if (m_canvas instanceof MouseMotionListener)
			this.addMouseMotionListener((MouseMotionListener)m_canvas);
		if (m_canvas instanceof KeyListener)
			this.addKeyListener((KeyListener)m_canvas);
    }
    
    public SimpleJDrawingCanvas getSimpleJDrawingCanvas() {
        return m_canvas;
    }

    public Dimension getPreferredScrollableViewportSize() {
        Rectangle r=m_canvas.getBounds();
        return new Dimension(r.width, r.height);
    }
    
    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                      int orientation,
                                      int direction) {
        return 1;
    }
    
    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                       int orientation,
                                       int direction) {
        return 1;
    }
    
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }


}