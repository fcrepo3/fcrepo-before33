/*
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
/*
 * @(#) BasicHelpUI.java 1.68 - last change made 05/04/01
 */

package javax.help.plaf.basic;

import javax.help.*;
import javax.help.plaf.HelpUI;
import javax.help.event.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Locale;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import javax.help.Map.ID;
import com.sun.java.help.impl.JHelpPrintHandler;
import java.awt.datatransfer.DataFlavor;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

/**
 * The default UI for JHelp. 
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Stepan Marek
 * @version   1.68     05/04/01
 */

public class BasicHelpUI extends HelpUI
	implements PropertyChangeListener, Serializable
{
    protected JHelp help;
    protected JToolBar toolbar;
    protected JSplitPane splitPane;
    protected JTabbedPane tabbedPane;
    protected Vector navs=new Vector();

    private Vector history = new Vector();
    private int historyIndex = -1; // empty

    private static Dimension PREF_SIZE = new Dimension(600,600);
    private static Dimension MIN_SIZE = new Dimension(300,200);
    static boolean noPageSetup;

    // Simple test to determine if pageSetup works on this system
    // No for 1.1
    // Yes for 1.2 on Windows, no for Solaris,Linux,HP
    // Yes for 1.3
    static boolean on1dot1;
    static {
	on1dot1 = false;
        noPageSetup = false;
        try {
            // Test if method introduced in 1.2 is available.
            Method m = Toolkit.class.getMethod("getMaximumCursorColors", null);
            noPageSetup = (m == null);
	    on1dot1 = (m == null);
        } catch (NoSuchMethodException e) {
	    noPageSetup = true;
	    on1dot1 = true;
        }
	
	// get out early if we already no we won't do page setup
	if (!noPageSetup) {
	    boolean on1dot2 = false;
	    try {
		// Test if method introduced in 1.3 is available.
		Method m = DataFlavor.class.getMethod("getTextPlainUnicodeFlavor", null);
		on1dot2 = (m == null);
	    } catch (NoSuchMethodException e) {
		on1dot2 = true;
	    }

	    String osName[] = new String[]{""};
	    osName[0] = System.getProperty("os.name");
	    if (osName[0] != null) {
		if ((osName[0].indexOf("Solaris") != -1) || 
		    (osName[0].indexOf("SunOS") != -1) || 
                    (osName[0].indexOf("Linux") != -1) ||
                    (osName[0].indexOf("HP-UX") != -1)){
		    if (on1dot2) {
			noPageSetup = true;
		    }
		} 
	    }
	}
    }

    // icons used for the default & navigators buttons
    private ImageIcon prev = getIcon("images/prev.gif");
    private ImageIcon next = getIcon("images/next.gif");
    private ImageIcon print = getIcon("images/print.gif");
    private ImageIcon pageSetup = getIcon("images/pageSetup.gif");
    private JButton prevButton;
    private JButton nextButton;
    private JButton printButton;
    private JButton pageSetupButton;
    private int dividerLocation = 0;
    private final double dividerLocationRatio = 0.30;

    private IdChangeListener changeListener = new IdChangeListener();

    public static ComponentUI createUI(JComponent x) {
        return new BasicHelpUI((JHelp) x);
    }

    public BasicHelpUI(JHelp b) {
	debug("createUI - sort of");
    }

    public void installUI(JComponent c) {
	debug("installUI");
	help = (JHelp)c;
	help.setLayout(new BorderLayout());

	// listen to property changes
	help.addPropertyChangeListener(this);

	// listen to ID changes (for history)
	HelpModel hm = getModel();
	if (hm != null) {
	    hm.addHelpModelListener(changeListener);
	}

	// ToolBar should be visible externally.
	    
	toolbar = createToolBar(HelpUtilities.getLocale(c));
	toolbar.setFloatable(false);
	help.add ("North", toolbar);

	// The navigators
	tabbedPane = new JTabbedPane();
	tabbedPane.setVisible(false);

	splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				   false, 
				   tabbedPane,
				   help.getContentViewer());

	splitPane.setOneTouchExpandable(true);
	help.add("Center", splitPane);

        JHelpNavigator first = null;
        for (Enumeration e = help.getHelpNavigators(); e.hasMoreElements();) {
            JHelpNavigator nav = (JHelpNavigator)e.nextElement();
            addNavigator(nav);
            if (first == null) first = nav;
        }
        
        debug("setting the current Navigator");
	if (first != null) {
	    this.setCurrentNavigator(first);
	}

	// load everything
	rebuild();
    }

    protected JToolBar createToolBar(Locale locale) {
	toolbar = new JToolBar();

	prevButton = new JButton(prev);
	prevButton.setEnabled(false);
	prevButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		goBack();
	    }
	});
	prevButton.setToolTipText(HelpUtilities.getString(locale,
							  "tip.previous"));

	nextButton = new JButton(next);
	nextButton.setEnabled(false);
	nextButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		goForward();
	    }
	});
	nextButton.setToolTipText(HelpUtilities.getString(locale,
							  "tip.next"));

        JHelpPrintHandler ph = null;
        printButton = new JButton(print);
	try {
            if (on1dot1) {
		Class c = Class.forName("com.sun.java.help.impl.JHelpPrintHandler");
		Constructor constr = 
		    c.getConstructor(new Class[]{HelpModel.class, Component.class});
		ph = (JHelpPrintHandler)
		    constr.newInstance(new Object[]{getModel(), printButton});
            } else {
		Class c = Class.forName("com.sun.java.help.impl.JHelpPrintHandler1_2");
		Constructor constr = 
		    c.getConstructor(new Class[]{HelpModel.class, Component.class});
		ph = (JHelpPrintHandler)
		    constr.newInstance(new Object[]{getModel(), printButton});
            }
        } catch (Throwable t) {
        }

        printButton.addActionListener(ph);
        printButton.setToolTipText(HelpUtilities.getString(locale, "tip.print"));
	    
        pageSetupButton = null;
        if (!noPageSetup) {
	    pageSetupButton = new JButton(pageSetup);
	    ph.handlePageSetup(pageSetupButton);
	    pageSetupButton.addActionListener(ph);
	    pageSetupButton.setToolTipText
		(HelpUtilities.getString(locale, "tip.pageSetup"));
        }
	
	toolbar.add(prevButton);
	toolbar.add(nextButton);
	toolbar.add(new JToolBar.Separator());
	if (printButton != null) {
	    toolbar.add(printButton);
	}
	if (pageSetupButton != null) {
	    toolbar.add(pageSetupButton);
	}

	return toolbar;
    }

    public void uninstallUI(JComponent c) {
	debug("uninstallUI");

	help.removePropertyChangeListener(this);
	help.setLayout(null);
	help.removeAll();

	HelpModel hm = getModel();
	if (hm != null) {
	    hm.removeHelpModelListener(changeListener);
	}

	help = null;
	toolbar = null;
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
	    
    private void rebuild() {
	HelpModel hm = getModel();
	if (hm == null) {
	    return;
	}

	// Discard any history
	discardHistory();

        try {
            HelpSet hs = hm.getHelpSet();
            ID id = hs.getHomeID();
            hm.setCurrentID(id);
        } catch (Exception e) {
            // For example, a null HelpSet!
            return;
        }
    }	

    public void propertyChange(PropertyChangeEvent event) {
	debug("propertyChange: " + event.getPropertyName());

        if (event.getSource() == help) {
            String changeName = event.getPropertyName();
            if (changeName.equals("helpModel")) {
		rebuild();
	    } else if (changeName.equals("font")) {
		debug ("Font change");
		Font newFont = (Font)event.getNewValue();
		help.getContentViewer().setFont(newFont);
		help.getContentViewer().invalidate();
		Enumeration entries = help.getHelpNavigators();
		while (entries.hasMoreElements()) {
		    JHelpNavigator nav = (JHelpNavigator)entries.nextElement();
		    nav.setFont(newFont);
		}
	    } else if (changeName.equals("navigatorDisplayed")) {
		tabbedPane.setVisible(((Boolean)event.getNewValue()).booleanValue());
	    }
        }
    }

    protected HelpModel getModel() {
	if (help == null) {
	    return null;
	} else {
	    return help.getModel();
	}
    }

    public void addNavigator(JHelpNavigator nav) {
	debug("addNavigator");
	navs.addElement(nav);
	Icon icon = nav.getIcon();
	if (icon != null) {
	    tabbedPane.addTab("", icon, nav, nav.getNavigatorLabel());
	} else {
	    String name = nav.getName();
	    if (name == null) {
		name = "<unknown>";
	    }
	    tabbedPane.addTab(name, icon, nav);
	}
        nav.setVisible(false);
	tabbedPane.setVisible(help.isNavigatorDisplayed());

	help.invalidate();
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		// The first time, arrange for the split size...
		// This should be customizable
		// setting a ratio at this point doesn't really work.
		// instead we will set the point based on the ratio and the
		// preferred sizes
		if (dividerLocation == 0d) {
		    Dimension dem = splitPane.getSize();
		    // if there is a size then perform the estimate
		    // otherwise use the default sizes
		    if (dem.width != 0) {
			splitPane.setDividerLocation((int)
						     ((double)(dem.width - 
							       splitPane.getDividerSize())
						      * dividerLocationRatio));
		    }
		    dividerLocation = splitPane.getDividerLocation();
		}
	    }
	});
    }

    public void removeNavigator(JHelpNavigator nav) {
	debug("removeNavigator");
	navs.removeElement(nav);
	tabbedPane.remove(nav);
	help.invalidate();
    }

    public Enumeration getHelpNavigators() {
	return navs.elements();
    }

    /**
     * Sets the current Navigator.
     *
     * @param navigator The navigator
     * @exception throws InvalidNavigatorException if not one of the HELPUI
     * navigators.
     */
    public void setCurrentNavigator(JHelpNavigator nav) {
        try {
            tabbedPane.setSelectedComponent(nav);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("JHelpNavigator must be added first");
        }
    }

    public JHelpNavigator getCurrentNavigator() {
	return (JHelpNavigator) tabbedPane.getSelectedComponent();
    }

    private ImageIcon getIcon(String name) {
	return getIcon(BasicHelpUI.class, name);
    }

    // public for now - need to reevalutate
    public static ImageIcon getIcon(Class klass, String name) {
	ImageIcon ig = null;
	try {
	    ig = SwingHelpUtilities.getImageIcon(klass, name);
	} catch (Exception ex) {
	}

	if (debug || ig == null) {
	    System.err.println("GetIcon");
	    System.err.println("  name: "+name);
	    System.err.println("  klass: "+klass);
	    URL url = klass.getResource(name);
	    System.err.println("  URL is "+url);
	    System.err.println("  ImageIcon is "+ig);
	}
	return ig;
    }

    /**
     * Internal class that listens to ID changes.
     */
    private class IdChangeListener implements HelpModelListener {
	/**
	 * Processes an idChanged event.
	 */
    public void idChanged(HelpModelEvent e) {
	debug("idChanged("+e+")");
	debug("  historyIndex=="+historyIndex);
	debug("  history.size=="+history.size());

	if (historyIndex == history.size()-1) {
	    // we are at the end
	    // (this covers the initial case of historyIndex == -1
	    history.addElement(e);
	    historyIndex += 1;
	    computeHistoryButtons();
	    
	    return;
	}

	if (historyIndex >= -1 &&
	    historyIndex < history.size()-1) {
	    // check the next slot where to record

	    historyIndex += 1;	// advance
	    HelpModelEvent h
		= (HelpModelEvent) history.elementAt(historyIndex);

	    if (h == null) {
		// this really should not happen
		discardHistory();
		return;
	    }

	    // compare ID's first
	    if (h.getID() != null &&	e.getID() != null &&
		h.getID().equals(e.getID())) {
		// we are were we wanted to be, just return
		computeHistoryButtons();
		return;
	    }

	    // compare URL's now
	    if (h.getURL() != null && e.getURL() != null &&
		h.getURL().sameFile(e.getURL())) {
		// we are were we wanted to be, just return
		computeHistoryButtons();
		return;
	    }

	    // new location is different, so throw away object and the rest
	    history.setSize(historyIndex);
	    // add the new element
	    history.addElement(e);
	    computeHistoryButtons();
	}
    }
    }

    private void computeHistoryButtons() {
	debug("computeHistoryButtons");
	debug("  historyIndex"+historyIndex);
	debug("  history.size"+history.size());

	prevButton.setEnabled(historyIndex > 0);
	nextButton.setEnabled(historyIndex < history.size()-1);
    }

    private void discardHistory() {
	history.setSize(0);
	historyIndex = -1;
	prevButton.setEnabled(false);
	nextButton.setEnabled(false);
    }

    protected void goBack() {
	gotoHistoryEntry(historyIndex-1);
    }

    protected void goForward() {
	gotoHistoryEntry(historyIndex+1);
    }

    private void gotoHistoryEntry(int index) {
	debug("gotoHistoryEntry("+index+")");

	HelpModel hm = getModel();
	if (hm == null) {
	    return;
	}

	if (index < 0 || index >= history.size()) {
	    // invalid index
	    discardHistory();
	    return;
	}

	HelpModelEvent e = (HelpModelEvent) history.elementAt(index);
	// set the historyIndex so it is ready to take the next event...
	historyIndex = index-1;
	ID id = e.getID();
	URL url = e.getURL();
	if (id != null) {
	    // try to set the ID
	    try {
		debug("  setCurrentID"+id);
		hm.setCurrentID(id);
		return;
	    } catch (Exception ex) {
		// fall through
	    }
	}
	if (url != null) {
	    // try to set the URL
	    try {
		debug("  setCurrentURL"+url);
		hm.setCurrentURL(url);
		return;
	    } catch (Exception ex) {
		// fall through
	    }
	}
	// this really should not happen but...
	discardHistory();
    }

    /**
     * For printf debugging.
     */
    private static boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("BasicHelpUI: " + str);
        }
    }
}

