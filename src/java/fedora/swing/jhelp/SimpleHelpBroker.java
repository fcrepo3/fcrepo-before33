package fedora.swing.jhelp;

import javax.swing.*;
import javax.help.*;
import javax.help.event.HelpModelEvent;
import javax.help.plaf.HelpContentViewerUI;
import java.util.Enumeration;
import java.util.Hashtable;
import java.net.MalformedURLException;
import java.net.URL;
import java.awt.*;
import java.awt.event.*;
import javax.help.CSH;
import javax.help.Map.ID;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.TextHelpModel;
import java.util.Locale;
import java.awt.Image;
import java.awt.Font;
import java.awt.Dimension;
import java.lang.reflect.*;

/**
 * <p><b>Title:</b> SimpleHelpBroker.java</p>
 * <p><b>Description:</b> 
 * An impelmentation of the HelpBroker interface.
 *
 * This is based on <code>DefaultHelpBroker</code> v1.33 (01/25/99) that 
 * comes with JavaHelp, originally written by Roger Brinkley and Eduardo 
 * Pelegri-Llopart.
 * JavaHelp comes with Java, and is written by Sun Microsystems.
 * More information on JavaHelp can be found at
 * http://java.sun.com/products/javahelp/faq.html
 *
 * I've added a new constructor, which takes an Image (the icon you want to
 * use for the window), and also a little more intelligent size tracking.
 * When you close the presentation, it remembers the old size, so subsequent
 * opens look the same.
 *
 * There is a lot of extra stuff (like support for java 1.1) where I don't
 * fully understand what they were doing and what variables they're passing
 * around and whatnot, but it seems to work fine, and it does what I want
 * for now, so I'm not gonna futz with it except for adding functionality
 * as needed.
 *
 * Also, in createJHelp, this class configures the JHelp such that the content
 * viewer UI (the panel where the html is displayed) is a 
 * <code>SimpleContentViewerUI</code> instead of a <code>BasicContentViewerUI</code>.
 * See SimpleContentViewerUI javadoc for why it's better.
 * <p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>Copyright &copy; 2002, 2003 by The Rector and Visitors of the University of 
 * Virginia and Cornell University. All rights reserved.  
 * Portions created by Sun Microsystems are Copyright &copy; 
 * Sun Microsystems, originally made available at java.sun.com</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper
 */

public class SimpleHelpBroker implements HelpBroker, KeyListener {

    protected HelpSet helpset = null;
    protected JFrame frame = null;
    protected JHelp jhelp = null;
    protected Locale locale=null;
    protected Font font=null;
    
    private SimpleContentViewerUI m_contentViewerUI;

    /**
     * The container for modally activated help
     * @since 1.1
     */
    protected JDialog dialog = null;

    /**
     * The modal Window that activated help
     * @since 1.1
     */
    protected Window ownerWindow = null;

    /**
     * The flag for modally activated help. If true, help was activated from
     * a modal dialog. Can not be set to true for V1.1.
     * @since 1.1
     */
    protected boolean modallyActivated = false;
    
    private Image m_iconImage=null;

    static boolean on1dot1;

    // Simple test to determine if running on 1.2 or 1.1 system
    static {
        on1dot1 = true;
        try {
            // Test if method introduced in 1.2 is available.
	    Class types[] = {Dialog.class, boolean.class}; 
            Constructor k = JDialog.class.getConstructor(types);
            on1dot1 = (k == null);
        } catch (NoSuchMethodException e) {
            on1dot1 = true;
        }
    }

    /**
     * Constructor
     */
    public SimpleHelpBroker(HelpSet hs) {
	    setHelpSet(hs);
    }

    public SimpleHelpBroker(HelpSet hs, Image iconImage) {
	    setHelpSet(hs);
        m_iconImage=iconImage;
    }

    /**
     * Zero-argument constructor.
     * It should be followed by a setHelpSet() invocation.
     */

    public SimpleHelpBroker() {
    }

    /**
     * Returns the default HelpSet
     */
    public HelpSet getHelpSet() {
	return helpset;
    }

    /**
     * Changes the HelpSet for this broker.
     * @param hs The HelpSet to set for this broker. 
     * A null hs is valid parameter.
     */
    public void setHelpSet(HelpSet hs) {
	// If we already have a jhelp check if the HelpSet has changed.
	// If so change the model on the jhelp viewer.
	// This could be made smarter to cache the helpmodels per HelpSet
	if (hs != null && helpset != hs) {
	    if (jhelp != null) {
		TextHelpModel model = new DefaultHelpModel(hs);
		jhelp.setModel(model);
	    }
	    helpset = hs;
            
	}
    }


    /**
     * Gets the locale of this component.
     * @return This component's locale. If this component does not
     * have a locale, the defaultLocale is returned.
     * @see #setLocale
     */
    public Locale getLocale() {
	if (locale == null) {
	  return Locale.getDefault();
	}
	return locale;
    }

    /**
     * Sets the locale of this HelpBroker. The locale is propagated to
     * the presentation.
     * @param l The locale to become this component's locale. A null locale
     * is the same as the defaultLocale.
     * @see #getLocale
     */
    public void setLocale(Locale l) { 
	locale = l;
	if (jhelp != null) {
	    jhelp.setLocale(locale);
	}
    }

    /**
     * Gets the font for this HelpBroker.
     */
    public Font getFont () {
	createHelpWindow();

	if (font == null) {
	    return jhelp.getFont();
	}
	return font;
    }

    /**
     * Sets the font for this this HelpBroker.
     * @param f The font.
     */
    public void setFont (Font f) {
	font = f;
	if (jhelp != null) {
	    jhelp.setFont(font);
	}
    }
    
    public void setPosition(Point p) {
    }

    /**
     * Set the currentView to the navigator with the same 
     * name as the <tt>name</tt> parameter.
     *
     * @param name The name of the navigator to set as the 
     * current view. If nav is null or not a valid Navigator 
     * in this HelpBroker then an 
     * IllegalArgumentException is thrown.
     * @throws IllegalArgumentException if nav is null or not a valid Navigator.
     */
    public void setCurrentView(String name) {
	createHelpWindow();
	JHelpNavigator nav = null;

	for (Enumeration e = jhelp.getHelpNavigators();
	     e.hasMoreElements(); ) {
	    nav = (JHelpNavigator) e.nextElement();
	    if (nav.getNavigatorName().equals(name)) {
		break;
	    }
	    nav = null;
	}

	if (nav == null) {
	    throw new IllegalArgumentException("Invalid view name");
	}
	jhelp.setCurrentNavigator(nav);
    }

    /**
     * Determines the current navigator.
     */
    public String getCurrentView() {
	createHelpWindow();
	return jhelp.getCurrentNavigator().getNavigatorName();
    }


    /**
     * Initializes the presentation.
     * This method allows the presentation to be initialized but not displayed.
     * Typically this would be done in a separate thread to reduce the
     * intialization time.
     */
    public void initPresentation() {
	createHelpWindow();
    }

    /**
     * Displays the presentation to the user.
     */
    public void setDisplayed(boolean b) {
	debug ("setDisplayed");
	createHelpWindow();
	if (modallyActivated) {
	    if (b) {
		dialog.show();
	    } else {
		dialog.hide();
	    }
	} else {
	    frame.setVisible(b);

	// We should be able to just 
	// try {
	// 	frame.setState(Frame.NORMAL)
	// } catch (NoSuchMethodError ex) {
	// }
	// but IE4.0 barfs very badly at this
	// So...

	    try {
		Class types[] = {Integer.TYPE};
		Method m = Frame.class.getMethod("setState", types);

		if (m != null) {
		    Object args[] = {new Integer(0)}; // Frame.NORMAL
		    m.invoke(frame, args);
		}
	    } catch (NoSuchMethodError ex) {
		// as in JDK1.1
	    } catch (NoSuchMethodException ex) {
		// as in JDK1.1
	    } catch (java.lang.reflect.InvocationTargetException ex) {
		//
	    } catch (java.lang.IllegalAccessException ex) {
		//
	    }
	}
    }

    /**
     * Determines if the presentation is displayed.
     */
    public boolean isDisplayed() {
	if (modallyActivated) {
	    if (dialog != null) {
		return dialog.isShowing();
	    } else {
		return false;
	    }
	} else {
	    if (frame != null) {
		if (! frame.isVisible()) {
		    return false;
		}
		else {
		    // We should be able to just 
		    // try {
		    // 	return (frame.getState() == Frame.NORMAL)
		    // } catch (NoSuchMethodError ex) {
		    // }
		    // but IE4.0 barfs very badly at this
		    // So...

		    try {
			Method m = Frame.class.getMethod("getState", null);

			if (m != null) {
			    int value =((Integer)(m.invoke(frame, null))).intValue();
			    if (value == 0)
				return true;
			    else 
				return false;

			}
		    } catch (NoSuchMethodError ex) {
			// as in JDK1.1
		    } catch (NoSuchMethodException ex) {
			// as in JDK1.1
		    } catch (java.lang.reflect.InvocationTargetException ex) {
			//
		    } catch (java.lang.IllegalAccessException ex) {
			//
		    }
		    // On 1.1 I can't tell if it's raised or not.
		    // It's on the screen so true.
		    return true;
		}
	    } else {
		return false;
	    }
	}
    }

    /**
     * Requests the presentation be located at a given position.
     * This operation may throw an UnsupportedOperationException if the
     * underlying implementation does not allow this.
     */
    public void setLocation(Point p) { //throws UnsupportedOperationException {
	createHelpWindow();
	if (modallyActivated) {
	    dialog.setLocation(p);
	} else {
	    frame.setLocation(p);
	}
    }

    /**
     * Requests the location of the presentation.
     * @throws UnsupportedOperationException If the underlying implementation 
     * does not allow this.
     * @throws IllegalComponentStateExcetpion If the presentation is not 
     * displayed.
     * @returns Point the location of the presentation.
     */
    public Point getLocation() { //throws UnsupportedOperationException {
	if (jhelp == null) {
	    throw new java.awt.IllegalComponentStateException("presentation not displayed");
	}
	if (modallyActivated) {
	    if (dialog != null) {
		return dialog.getLocation();
	    }
	} else {
	    if (frame != null) {
		return frame.getLocation();
	    }
	}
	return null;

    }

    /**
     * Requests the presentation be set to a given size.
     * This operation may throw an UnsupportedOperationException if the
     * underlying implementation does not allow this.
     */
    public void setSize(Dimension d) { //throws UnsupportedOperationException {
	HELP_WIDTH = d.width;
	HELP_HEIGHT = d.height;
	createHelpWindow();
	if (modallyActivated) {
	    dialog.setSize(d);
	    dialog.validate();
	} else {
	    frame.setSize(d);
	    frame.validate();
	}
    }

    /**
     * Requests the size of the presentation.
     * @throws UnsupportedOperationException If the underlying implementation 
     * does not allow this.
     * @throws IllegalComponentStateExcetpion If the presentation is not 
     * displayed.
     * @returns Point the location of the presentation.
     */
    public Dimension getSize() { //throws UnsupportedOperationException {
	if (jhelp == null) {
	    throw new java.awt.IllegalComponentStateException("presentation not displayed");
	}
	if (modallyActivated) {
	    if (dialog != null) {
		return dialog.getSize();
	    }
	} else {
	    if (frame != null) {
		return frame.getSize();
	    }
	}
	return null;
    }

    /**
     * Hides/Shows view.
     */
    public void setViewDisplayed(boolean displayed) {
	createHelpWindow();
	jhelp.setNavigatorDisplayed(displayed);
    }

    /**
     * Determines if the current view is visible.
     */
    public boolean isViewDisplayed() {
	createHelpWindow();
	return jhelp.isNavigatorDisplayed();
    }

    /**
     * Shows this ID as content relative to the (top) HelpSet for the HelpBroker
     * instance--HelpVisitListeners are notified.
     *
     * @param id A string that identifies the topic to show for the loaded (top) HelpSet
     * @exception BadIDException The ID is not valid for the HelpSet
     */
    public void setCurrentID(String id) throws BadIDException {
	try {
	    setCurrentID(ID.create(id, helpset));
	} catch (InvalidHelpSetContextException ex) {
	    // this should not happen
	    new Error("internal error?");
	}
    }

    /**
     * Displays this ID--HelpVisitListeners are notified.
     *
     * @param id a Map.ID indicating the URL to display
     * @exception InvalidHelpSetContextException if the current helpset does not contain
     * id.helpset
     */
    public void setCurrentID(ID id) throws InvalidHelpSetContextException {
	debug("setCurrentID");

	createJHelp();
	jhelp.getModel().setCurrentID(id);
    }

    /**
     * Determines which ID is displayed (if any).
     */
    public ID getCurrentID() {
	if (jhelp !=null) {
	    return jhelp.getModel().getCurrentID();
	} else {
	    return null;
	}
    }

    /**
     * Displays this URL.
     * HelpVisitListeners are notified.
     * The currentID changes if there is a mathing ID for this URL
     * @param url The url to display. A null URL is a valid url.
     */
    public void setCurrentURL(URL url) {
	createHelpWindow();

	jhelp.getModel().setCurrentURL(url);
	if (modallyActivated) {
	    dialog.setVisible(true);
	    dialog.show();
	} else {
	    frame.setVisible(true);
	    frame.show();
	}
    }

    /**
     * Determines which URL is displayed.
     */
    public URL getCurrentURL() {
	return jhelp.getModel().getCurrentURL();
    }


    // Context-Senstive methods
    /**
     * Enables the Help key on a Component. This method works best when
     * the component is the
     * rootPane of a JFrame in Swing implementations, or a java.awt.Window
     * (or subclass thereof) in AWT implementations.
     * This method sets the default
     * helpID and HelpSet for the Component and registers keyboard actions
     * to trap the "Help" keypress. When the "Help" key is pressed, if the
     * object with the current focus has a helpID, the helpID is displayed.
     * otherwise the default helpID is displayed.
     *
     * @param comp the Component to enable the keyboard actions on.
     * @param id the default HelpID to be displayed
     * @param hs the default HelpSet to be displayed. If hs is null the default HelpSet
     * will be assumed.
     * 
     * @see getHelpKeyActionListener
     */
    public void enableHelpKey(Component comp, String id, HelpSet hs) {
	if (id == null) {
	    throw new NullPointerException("id");
	}
	CSH.setHelpIDString(comp, id);
	if (hs !=null) {
	    CSH.setHelpSet(comp, hs);
	}
	if (comp instanceof JComponent) {
	    JComponent root = (JComponent) comp;
	    root.registerKeyboardAction(getDisplayHelpFromFocus(),
				   KeyStroke.getKeyStroke(KeyEvent.VK_HELP, 0),
				   JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	    root.registerKeyboardAction(getDisplayHelpFromFocus() ,
				    KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
				    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	} else {
	    comp.addKeyListener(this);
	}
    }

    /**
     * Invoked when a key is typed. This event occurs when a
     * key press is followed by a key release.  Not intended to be overridden or extended.
     */
    public void keyTyped(KeyEvent e) {
	//ignore
    }

    /**
     * Invoked when a key is pressed. Not intended to be overridden or extended.
     */
    public void keyPressed(KeyEvent e) {
	//ingore
    }



    /**
     * Invoked when a key is released.  Not intended to be overridden or extended.
     */
    public void keyReleased(KeyEvent e) {
	// simulate what is done in JComponents registerKeyboardActions.
	int code = e.getKeyCode();
	if (code == KeyEvent.VK_F1 || code == KeyEvent.VK_HELP) {
	    ActionListener al = getDisplayHelpFromFocus();
	    al.actionPerformed(new ActionEvent(e.getComponent(),
					       ActionEvent.ACTION_PERFORMED,
					       null));
	}
	
    }

    /**
     * Enables help for a Component. This method sets a 
     * component's helpID and HelpSet. 
     *
     * @param comp the Component to set the id and hs on.
     * @param id the String value of an Map.ID.
     * @param hs the HelpSet the id is in. If hs is null the default HelpSet
     * will be assumed.
     * @see CSH.setHelpID
     * @see CSH.setHelpSet
     */
    public void enableHelp(Component comp, String id, HelpSet hs) 
    {
	if (id == null) {
	    throw new NullPointerException("id");
	}
	CSH.setHelpIDString(comp, id);
	if (hs != null) {
	    CSH.setHelpSet(comp, hs);
	}
    }

    /**
     * Enables help for a MenuItem. This method sets a 
     * component's helpID and HelpSet. 
     *
     * @param comp the MenuItem to set the id and hs on.
     * @param id the String value of an Map.ID.
     * @param hs the HelpSet the id is in. If hs is null the default HelpSet
     * will be assumed.
     * @see CSH.setHelpID
     * @see CSH.setHelpSet
     */
    public void enableHelp(MenuItem comp, String id, HelpSet hs) 
    {
	if (id == null) {
	    throw new NullPointerException("id");
	}
	CSH.setHelpIDString(comp, id);
	if (hs != null) {
	    CSH.setHelpSet(comp, hs);
	}
    }

    /**
     * Enables help for a Component. This method sets a 
     * Component's helpID and HelpSet and adds an ActionListener. 
     * When an action is performed
     * it displays the Component's helpID and HelpSet in the default viewer.
     *
     * @param comp the Component to set the id and hs on. If the Component is not 
     * a javax.swing.AbstractButton or a 
     * java.awt.Button an IllegalArgumentException is thrown.
     * @param id the String value of an Map.ID.
     * @param hs the HelpSet the id is in. If hs is null the default HelpSet
     * will be assumed.
     *
     * @see CSH.setHelpID
     * @see CSH.setHelpSet
     * @see javax.swing.AbstractButton
     * @see java.awt.Button
     * @throws IllegalArgumentException if comp is null.
     */
    public void enableHelpOnButton(Component comp, String id, HelpSet hs) 
    {
	if (!(comp instanceof AbstractButton) && !(comp instanceof Button)) {
	    throw new IllegalArgumentException("Invalid Component");
	}
	if (id == null) {
	    throw new NullPointerException("id");
	}
	CSH.setHelpIDString(comp, id);
	if (hs != null) {
	    CSH.setHelpSet(comp, hs);
	}
	if (comp instanceof AbstractButton) {
	    AbstractButton button = (AbstractButton) comp;
	    button.addActionListener(getDisplayHelpFromSource());
	} else 	if (comp instanceof Button) {
	    Button button = (Button) comp;
	    button.addActionListener(getDisplayHelpFromSource());
	}
    }

    /**
     * Enables help for a MenuItem. This method sets a 
     * Component's helpID and HelpSet and adds an ActionListener. 
     * When an action is performed
     * it displays the Component's helpID and HelpSet in the default viewer.
     *
     * @param comp the MenuItem to set the id and hs on. If comp is null
     * an IllegalAgrumentException is thrown.
     * @param id the String value of an Map.ID.
     * @param hs the HelpSet the id is in. If hs is null the default HelpSet
     * will be assumed.
     * @see CSH.setHelpID
     * @see CSH.setHelpSet
     * @see java.awt.MenuItem
     * @throws IllegalArgumentException if comp is null.
     */
    public void enableHelpOnButton(MenuItem comp, String id, HelpSet hs) 
    {
	if (comp == null) {
	    throw new IllegalArgumentException("Invalid Component");
	}
	if (id == null) {
	    throw new NullPointerException("id");
	}
	CSH.setHelpIDString(comp, id);
	if (hs != null) {
	    CSH.setHelpSet(comp, hs);
	}
	comp.addActionListener(getDisplayHelpFromSource());
    }

    /**
     * Returns the default DisplayHelpFromFocus listener.
     *
     * @see enableHelpKey
     */
    protected ActionListener getDisplayHelpFromFocus() {
	if (displayHelpFromFocus == null) {
	    displayHelpFromFocus = new CSH.DisplayHelpFromFocus(this);
	}
	return displayHelpFromFocus;
    }

    /**
     * Returns the default DisplayHelpFromSource listener.
     *
     * @see enableHelp
     */
    protected ActionListener getDisplayHelpFromSource() {
	if (displayHelpFromSource==null) {
	    displayHelpFromSource = new CSH.DisplayHelpFromSource(this);
	}
	return displayHelpFromSource;
    }

    /**
     * Set the activation window. If the window is an instance of a
     * Dialog and the is modal, modallyActivated help is set to true and 
     * ownerDialog is set to the window. In all other instances 
     * modallyActivated is set to false and ownerDialog is set to null.
     * @param window the activating window
     * @since 1.1
     */
    public void setActivationWindow(Window window) {
	if (window != null && window instanceof Dialog) {
	    Dialog tmpDialog = (Dialog) window;
	    if (tmpDialog.isModal()) {
		ownerWindow = window;
		modallyActivated = true;
	    } else {
		ownerWindow = null;
		modallyActivated = false;
	    }
	} else {
	    ownerWindow = null;
	    modallyActivated = false;
	}
    }
    
    boolean displayedAtLeastOnce=false;
    public void ensureContentPanelDrawn(Map.ID id) {
        try {
        if (m_contentViewerUI!=null) {
            URL u=helpset.getCombinedMap().getURLFromID(id);
            m_contentViewerUI.idChanged(new HelpModelEvent(this,id,u));
            displayedAtLeastOnce=true;
        }
        } catch (MalformedURLException mfurle) { }
    }

    /**
     * Private methods.
     */
    private int HELP_WIDTH = 645;
    private int HELP_HEIGHT = 495;
    
    private synchronized void createJHelp() {
	debug ("createJHelp");
	if (jhelp == null) {
	    jhelp = new JHelp(helpset);
        m_contentViewerUI=new SimpleContentViewerUI(jhelp.getContentViewer());
        jhelp.getContentViewer().setUI(m_contentViewerUI);
        /*
        try { 
          DefaultHelpModel md=(DefaultHelpModel) jhelp.getModel();
          md.add 
        } catch (ClassCastException cce) { }
       */ 
	    if (font != null) {
		jhelp.setFont(font);
	    }
	    if (locale != null) {
		jhelp.setLocale(locale);
	    }
	}
    }

    WindowListener dl;
    boolean modalDeactivated = true;

    private synchronized void createHelpWindow() {
	debug ("createHelpWindow");
	Point pos = null;
	Dimension size = null;
	JDialog tmpDialog = null;

	createJHelp();
	// Get the title from the HelpSet
	String helpTitle = helpset.getTitle();

	if (modallyActivated) {
	    // replace dialog.getOwner() with the following code
	    Window owner=null;
	    try {
		Method m = Window.class.getMethod("getOwner", null);
		
		if (m != null && dialog != null) {
		    owner = (Window) m.invoke(dialog, null);
		}
	    } catch (NoSuchMethodError ex) {
		// as in JDK1.1
	    } catch (NoSuchMethodException ex) {
		// as in JDK1.1
	    } catch (java.lang.reflect.InvocationTargetException ex) {
		//
	    } catch (java.lang.IllegalAccessException ex) {
		//
	    }
	    
	    if (dialog == null || owner != ownerWindow || modalDeactivated) {
		if (frame != null) {
		    pos = frame.getLocation();
		    size = frame.getSize();
		    frame.dispose();
		}
		if (dialog != null) {
		    pos = dialog.getLocation();
		    size = dialog.getSize();
		    tmpDialog = dialog;
		}
		if (on1dot1) {
		    dialog = new JDialog();
		    dialog.setTitle(helpTitle);
		} else {
		    dialog = new JDialog((Dialog)ownerWindow, helpTitle);

		    // Modal dialogs are really tricky. When the modal dialog
		    // is dismissed the JDialog will be dismissed as well.
		    // When that happens we need to make sure the ownerWindow
		    // is set to null so that a new dialog will be created so
		    // that events aren't blocked in the HelpViewer.
		    dl = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
			    debug ("modal window closing");
			    // JDK1.2.1 bug not closing owned windows
			    if (dialog.isShowing()) {
				dialog.hide();
			    }
			    if (ownerWindow != null)
			        ownerWindow.removeWindowListener(dl);
			    ownerWindow = null;
			    modalDeactivated = true;
			}
		    };
		    debug ("adding windowlistener");
		    ownerWindow.addWindowListener(dl);
		    modalDeactivated = false;
		}
		if (size != null) {
		    dialog.setSize(size);
		} else {
		    dialog.setSize(HELP_WIDTH, HELP_HEIGHT);
		}
		if (pos != null) {
		    dialog.setLocation(pos);
		}
		dialog.getContentPane().add(jhelp);
		if (tmpDialog != null) {
		    tmpDialog.dispose();
		}
	    }
	} else {
	    if (frame == null) { 
		frame = new JFrame(helpTitle);
        if (m_iconImage!=null) {
            frame.setIconImage(m_iconImage);
        }
		WindowListener l = new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
            HELP_WIDTH=e.getWindow().getWidth();
            HELP_HEIGHT=e.getWindow().getHeight();
			frame.setVisible(false);
		    }
		    public void windowClosed(WindowEvent e) {
			frame.setVisible(false);
            HELP_WIDTH=e.getWindow().getWidth();
            HELP_HEIGHT=e.getWindow().getHeight();
		    }
            
		};
		frame.addWindowListener(l);
	    }
	    if (dialog != null) {
		pos = dialog.getLocation();
		size = dialog.getSize();
		dialog.dispose();
		dialog = null;
		ownerWindow = null;
	    }
	    if (size != null) {
		frame.setSize(size);
	    } else {
		frame.setSize(HELP_WIDTH, HELP_HEIGHT);
	    }
	    if (pos != null) {
		frame.setLocation(pos);
	    }
	    frame.getContentPane().add(jhelp);
            frame.setTitle(helpset.getTitle());
	}

    }                 

    // the listeners.
    protected ActionListener displayHelpFromFocus;
    protected ActionListener displayHelpFromSource;

    /*
     * Make sure the Look and Feel will be set
     */
//    static {
//	SwingHelpUtilities.installLookAndFeelDefaults();
//    }

    /**
     * Debugging code...
     */

    private static final boolean debug = false;
    private static void debug(Object msg) {
	if (debug) {
	    System.err.println("SimpleHelpBroker: "+msg);
	}
    }
 
}

