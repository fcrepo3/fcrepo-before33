package edu.cornell.dlrg.swing.jhelp;

import javax.help.*;
import javax.help.plaf.HelpContentViewerUI;
import javax.help.event.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TextUI;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import javax.help.Map.ID;

/**
 * Based on SimpleContentViewerUI in javax.help.plaf.basic,
 * which comes with jhelp.  But this version doesn't call
 * <code>setBackingStoreEnable(true)</code>, which is a
 * deprecated method (as of java1.4) anyway.
 * <p>
 * This simple modification has the net effect of allowing
 * images displayed in help content windows to not be mangled
 * (actually, they mysteriously disappear) when the scrollbar
 * is first used.
 * <p>
 * @author Chris Wilper
 */

public class SimpleContentViewerUI extends HelpContentViewerUI
   implements HelpModelListener, TextHelpModelListener, HyperlinkListener, PropertyChangeListener, Serializable
{
    // Suggestion: Refactor the class and remove the menu bar and the
    //		HTML viewer into subclasses. This would allow easier 
    //		replacement of both components to
    // 
    // cwilper:note ^^^ haha, looks like they were thinking about making
    // replacement easier at some point... ahh well.
    protected JHelpContentViewer theViewer;

    private static Dimension PREF_SIZE = new Dimension(200, 300);
    private static Dimension MIN_SIZE = new Dimension(80, 80);

    private JEditorPane html;
    private JViewport vp;

    private Hashtable registry;
    private boolean loadingURL;
    private TextHelpModelEvent pendingHighlightsEvent;

    public static ComponentUI createUI(JComponent x) {
	debug("createUI");
        return new SimpleContentViewerUI((JHelpContentViewer) x);
    }

    public SimpleContentViewerUI(JHelpContentViewer b) {
	debug("createUI - sort of");
    }
    
    public void setEditorKit(String type, EditorKit kit) {
	debug("setEditorKit("+type+", "+kit+")");
	if (registry == null) {
	    registry = new Hashtable(3);
	}
	registry.put(type, kit);
	if (html != null) {
	    debug("  type: "+type);
	    debug("  kit: "+kit);
	    html.setEditorKitForContentType(type, kit);
	    if (debug)
		debug("  kit got: "+html.getEditorKitForContentType(type));
	}
    }

    /**
     * Subclass of JEditorPane that uses the JHelpContentViewer type registry.
     */

    public class JHEditorPane extends JEditorPane {
	private Hashtable typeHandlers;

	public EditorKit getEditorKitForContentType(String type) {
	    if (typeHandlers == null) {
		typeHandlers = new Hashtable(3);
	    }
	    EditorKit k = (EditorKit) typeHandlers.get(type);
	    if (k == null) {
		k = theViewer.createEditorKitForContentType(type);
		if (k != null) {
		    setEditorKitForContentType(type, k);
		    typeHandlers.put(type, k);
		}
	    }
	    if (k == null) {
		k = super.getEditorKitForContentType(type);
		if (k != null) {
		    typeHandlers.put(type, k);
		}
	    }
	    return k;
	}

    }



    public void installUI(JComponent c) {
	debug("installUI");
	theViewer = (JHelpContentViewer)c;
	theViewer.setLayout(new BorderLayout());

	// listen to property changes...
	theViewer.addPropertyChangeListener(this);
	
	TextHelpModel model = theViewer.getModel();
	if (model != null) {
	    // listen to id changes...
	    model.addHelpModelListener(this);
	    // listen to highlight changes...
	    model.addTextHelpModelListener(this);
	}

	html = new JHEditorPane();
	html.addPropertyChangeListener(this);

	html.setEditable(false);
	html.addHyperlinkListener(this);
	JScrollPane scroller = new JScrollPane();
	scroller.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.white,
					   Color.gray));
	vp = scroller.getViewport();
	vp.add(html);
    
    // here is the suspect line:
    //
    // Interesting fact: It took about eight hours to come up with 
    // a solution which incorporated removal of this line while
    // keeping the base javahelp classes intact, and dealing with
    // miscellaneous errors that came up.
    // 
    // But I won, see.  I won.  That's all that matters,
    // mister computer.  Bite me!! nyeh nyeh!!!!
    //
	// vp.setBackingStoreEnabled(true);
	    
	theViewer.add ("Center", scroller);
	loadingURL = false;
	pendingHighlightsEvent = null;
    }

    public void uninstallUI(JComponent c) {
	debug("uninstallUI");
	JHelpContentViewer viewer = (JHelpContentViewer) c;
	viewer.removePropertyChangeListener(this);
	html.removePropertyChangeListener(this);
	TextHelpModel model = viewer.getModel();
	if (model != null) {
	    model.removeHelpModelListener(this);
	    model.removeTextHelpModelListener(this);
	}
	viewer.setLayout(null);
	viewer.removeAll();
    }

    public Dimension getPreferredSize(JComponent c) {
	return PREF_SIZE;
    }

    public Dimension getMinimumSize(JComponent c) {
	return MIN_SIZE;
    }

    public Dimension getMaximumSize(JComponent c) {
	// This doesn't seem right. But I'm not sure what to do for now
	return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public void idChanged(HelpModelEvent e) {
	ID id = e.getID();
	URL url = e.getURL();
	TextHelpModel model = theViewer.getModel();
	debug("idChanged("+e+")");
	debug("  = " + id + "--" + url);
	debug("  my helpModel: "+model);
	    
	model.setDocumentTitle(null);

	// HERE - REMIND - event should be generated after visit
	// with the title already in there - epll.
	try {
	    Highlighter h = html.getHighlighter();
	    debug("removeAllHighlights");
	    h.removeAllHighlights();
	    try {
		loadingURL = true;
		html.setPage(url);
	    } catch (Exception ex) {
		loadingURL = false;
		// IGNORE FOR EA2 --- !! - epll
	    }
	    debug("html current EditorKit is: "+html.getEditorKit());
	    debug("html current ContentType is: "+html.getContentType());
	} catch (Exception e3) {
	    // e.g. and incorrect URL will generate a nullexception!!
	    // ignore
	    debug ("Exception geneartated");
	}
	debug("done with idChanged");
    }

    private void rebuild() {
	debug("rebuild");
	TextHelpModel model = theViewer.getModel();
	if (model == null) {
	    debug("rebuild-end: model is null");
	    return;
	}
	// The model has changed. I'll need to clear the HTML viewer and
	// put in the home page
	Highlighter h = html.getHighlighter();
	debug("removeAllHighlights");
	h.removeAllHighlights();
	HelpSet hs = model.getHelpSet();
	try {
	    ID id = hs.getHomeID();
	    model.setCurrentID(id);
	} catch (Exception e) {
	    // ignore
	}
	debug("rebuld-end");
    }	

    public void propertyChange(PropertyChangeEvent event) {
	debug("propertyChange: " + event.getPropertyName() + "\n\toldValue:" + event.getOldValue() + "\n\tnewValue:" + event.getNewValue());

        if (event.getSource() == theViewer) {
            String changeName = event.getPropertyName();
	    if (changeName.equals("helpModel")) {
		TextHelpModel oldModel = (TextHelpModel) event.getOldValue();
		TextHelpModel newModel = (TextHelpModel) event.getNewValue();
		if (oldModel != null) {
		    oldModel.removeHelpModelListener(this);
		    oldModel.removeTextHelpModelListener(this);
		}
		if (newModel != null) {
		    newModel.addHelpModelListener(this);
		    newModel.addTextHelpModelListener(this);
		}
		rebuild();
	    } else if (changeName.equals("font")) {
		debug ("font changed");
		Font newFont = (Font)event.getNewValue();
		EditorKit ek = html.getEditorKit();
		if (ek instanceof HTMLEditorKit) {
		    String cssData = "body { font-family: " +
			newFont.getFamily() + " }";
		    HTMLEditorKit hek = (HTMLEditorKit) ek;
		    StyleSheet styles = hek.getStyleSheet();
		    try {
			styles.loadRules(new StringReader(cssData), null);
		    } catch (Exception ex) {
		    }
		}
		RepaintManager.currentManager(html).markCompletelyDirty(html);
	    }
        } else if (event.getSource() == html) {
	    String changeName = event.getPropertyName();
	    if (changeName.equals("page")) {
		debug ("page finished loading");
		loadingURL = false;
		if (pendingHighlightsEvent != null) {
		    debug("Loading the highlights now");
		    highlightsChanged(pendingHighlightsEvent);
		    pendingHighlightsEvent = null;
		}
		Document doc = html.getDocument();
		String title = (String)doc.getProperty(Document.TitleProperty);
		TextHelpModel model = theViewer.getModel();
		model.setDocumentTitle(title);

		// Propogate this to property listeners of the ContentViewer.
		// Can't acutally send the real thing because Objects and
		// Strings aren't supported.
		theViewer.firePropertyChange(event.getPropertyName(), 
					     false, true);
	    }
	}
    }

    /**
     * Notification of a change relative to a 
     * hyperlink.
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
	if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
	    // If in a frame do something different
	    if (e instanceof HTMLFrameHyperlinkEvent) {
		((HTMLDocument)html.getDocument()).processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent)e);
	    } else {
		// Not a frame
		linkActivated(e.getURL());
	    }
	}
    }

    /**
     * Follows the reference in an
     * link.  The given url is the requested reference.
     * By default this calls <a href="#setPage">setPage</a>,
     * and if an exception is thrown the original previous
     * document is restored and a beep sounded.  If an 
     * attempt was made to follow a link, but it represented
     * a malformed url, this method will be called with a
     * null argument.
     *
     * @param u the URL to follow
     */
    protected void linkActivated(URL u) {
	debug("linkActivated - URL=" + u );
	Cursor c = html.getCursor();
	Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	html.setCursor(waitCursor);
	// workaround for bug #418959
	// if there is an anchor make sure the file doesn't end in "/" or "\"
	// if it does then get the current file name and append the ref
	String ref = u.getRef();
	if (ref != null) {
	    String file = u.getFile();
	    if (file.endsWith("/") || file.endsWith("\\")) {
		u = html.getPage();
 		debug ("current u=" + u);
		file = u.getFile();
		debug ("file=" + file);
		try {
		    u = new URL(u.getProtocol(), u.getHost(),
				u.getPort(), file + "#" + ref);
		} catch (MalformedURLException e2) {
		    return;
		}
 		debug ("new u=" + u);
	    }
	}
	SwingUtilities.invokeLater(new PageLoader(u, c));
    }

    /**
     * Temporary class that loads synchronously (although
     * later than the request so that a cursor change
     * can be done).
     */
    class PageLoader implements Runnable {
	
	PageLoader(URL u, Cursor c) {
	    url = u;
	    cursor = c;
	}

        public void run() {
	    if (url == null) {
		// restore the original cursor
		html.setCursor(cursor);

		RepaintManager.currentManager(html).markCompletelyDirty(html);
	    } else {
		Document doc = html.getDocument();
		String title;
		try {
		    html.setPage(url);
		    loadingURL = true;
		    doc = html.getDocument();
		    title = (String)doc.getProperty(Document.TitleProperty);

		    TextHelpModel model = theViewer.getModel();
		    model.setDocumentTitle(title);
		    ID id = model.getHelpSet().getCombinedMap().getIDFromURL(url);
		    if (id != null) {
			try {
			    model.setCurrentID(id);
			} catch (InvalidHelpSetContextException ex) {
			    model.setCurrentURL(url);
			}
		    } else {
			model.setCurrentURL(url);
		    }
		} catch (IOException ioe) {
		    loadingURL = false;
		    html.setDocument(doc);
		    html.getToolkit().beep();
		} finally {
		    // schedule the cursor to revert after
		    // the paint has happended.
		    url = null;
		    SwingUtilities.invokeLater(this);
		}
	    }
	}

	URL url;
	Cursor cursor;
    }

    /**
     * Determines if highlights have changed.
     * Collects all the highlights and marks the presentation.
     *
     * @param e The TextHelpModelEvent.
     */
    public void highlightsChanged(TextHelpModelEvent e) {
	debug("highlightsChanged "+e);

	// if we're loading this url this event will need to wait
	// until we're notified that the page has finished loaded.
	// we could wait(), but that would prevent the execution of
	// other events in this thread. Instead preserve the
	// TextHelpModelEvent and recall this method when the notification
	// occurs.
	if (loadingURL) {
	    debug("Humm. loadingURL wait a little");
	    pendingHighlightsEvent = e;
	    return;
	}

	Highlighter h = html.getHighlighter();
	debug1("removeAllHighlights");
	h.removeAllHighlights();

	TextHelpModel m = (TextHelpModel) e.getSource();
	TextHelpModel.Highlight highlights[] = m.getHighlights();

	Highlighter.HighlightPainter p = 
	    new DefaultHighlighter.DefaultHighlightPainter(html.getSelectionColor());
	for (int i=0; i<highlights.length; i++) {
	    int pos0 = highlights[i].getStartOffset();
	    int pos1 = highlights[i].getEndOffset();
	    debug("  highlight: "+pos0+", "+pos1);
	    try {
		h.addHighlight(pos0, pos1, p);

		// scroll to the first position
		if (i == 0) {
		    Runnable callScrollToPosition = new ScrollToPosition(html,
									pos1);
		    SwingUtilities.invokeLater(callScrollToPosition);
		}
	    } catch (BadLocationException bl) {
		debug ("badLocationExcetpion thrown - " + bl);
	    }
	}
	RepaintManager.currentManager(html).markCompletelyDirty(html);
    }

    private class ScrollToPosition implements Runnable {
	private int pos;
	private JEditorPane html;
	
	public ScrollToPosition (JEditorPane html, int pos) {
	    this.html = html;
	    this.pos = pos;
	}

	public void run() {
	    try {
		Rectangle rec = html.modelToView(pos);
		if (rec != null) {
		    html.scrollRectToVisible(rec);
		}
	    } catch (BadLocationException bl) {
	    }
	}
    }

    /**
     * For printf debugging.
     */
    private final static boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("SimpleContentViewerUI: " + str);
        }
    }
    private final static boolean debug1 = false;
    private static void debug1(String str) {
        if (debug1) {
            System.out.println("SimpleContentViewerUI: " + str);
        }
    }
}
