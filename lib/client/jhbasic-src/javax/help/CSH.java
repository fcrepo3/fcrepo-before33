/*
 * @(#) CSH.java 1.39 - last change made 05/16/01
 *
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

package javax.help;

import java.lang.reflect.*;
import javax.help.*;
import javax.help.event.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Component;
import java.applet.Applet;
import java.net.URL;
import javax.help.Map.ID;
import java.util.Stack;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

/**
 * A convenience class that provides simple
 * access to context-senstive help functionality. It creates a default JavaHelp
 * viewer as well as ActionListeners for "Help" keys, on-item help, and
 * help buttons.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Stepan Marek
 * @version	1.39	05/16/01
 *
 */

public class CSH {

    static private Hashtable comps;

    /**
     * Sets the helpID for a component.
     * If helpID is null this method removes the helpID from the component.
     */
    public static void setHelpIDString(Component comp, String helpID) {
	// For JComponents just use client property
	if (comp instanceof JComponent) {
	    ((JComponent)comp).putClientProperty("HelpID", helpID);
	} else {
	    // For Components we have an internal Hashtable of components and
	    // their properties.

	    // Initialize as necessary
	    if (comps == null) {
		comps = new Hashtable(5);
	    }

	    // See if this component has already set some client properties
	    // If so update.
	    // If not then create the client props (as needed) and add to
	    // the internal Hashtable of components and properties
	    Hashtable clientProps = (Hashtable) comps.get(comp);
	    if (clientProps != null) {
		if (helpID != null) {
		    clientProps.put("HelpID", helpID);
		} else {
		    clientProps.remove("HelpID");
		    if (clientProps.isEmpty()) {
			comps.remove(comp);
		    }
		}
	    } else {
		// Only create properties if there is a valid helpID
		if (helpID != null) {
		    clientProps = new Hashtable(2);
		    clientProps.put("HelpID", helpID);
		    comps.put(comp, clientProps);
		}
	    }
	}	    
    }

    /**
     * Returns the helpID for a component.
     * If the component doesn't have associated help, traverse the
     * component's ancestors for help. 
     */
    public static String getHelpIDString(Component comp) {
	String helpID = null;
	// For JComponents use the client property
	// All others must check the internal component hashtable
	if (comp instanceof JComponent) {
	    helpID = (String) ((JComponent)comp).getClientProperty("HelpID");
	} else {
	    if (comps != null) {
		Hashtable clientProps = (Hashtable)comps.get(comp);
		if (clientProps !=null) {
		    helpID = (String) clientProps.get("HelpID");
		}
	    }
	}

	// return the helpID if it exists
	if (helpID != null) {
	    return helpID;
	}

	// loop through the parents to try to find a valid helpID
	Container parent = comp.getParent();
	if (parent != null) {
	    return getHelpIDString(parent);
	} else {
	    return null;
	}
    }

    /**
     * Sets the HelpSet for a component.
     * If HelpSet is null, this method removes the HelpSet 
     * from the component.
     */
    public static void setHelpSet(Component comp, HelpSet hs) {
	if (comp == null) {
	    throw new IllegalArgumentException("Invalid Component");
	}
	// For JComponents just use client property
	if (comp instanceof JComponent) {
	    ((JComponent)comp).putClientProperty("HelpSet", hs);
	} else {
	    // For Components we have an internal Hashtable of components and
	    // their properties.

	    // Initialize as necessary
	    if (comps == null) {
		comps = new Hashtable(5);
	    }

	    // See if this component has already set some client properties
	    // If so update.
	    // If not then create the client props (as needed) and add to
	    // the internal Hashtable of components and properties
	    Hashtable clientProps = (Hashtable) comps.get(comp);
	    if (clientProps != null) {
		if (hs != null) {
		    clientProps.put("HelpSet", hs);
		} else {
		    clientProps.remove("HelpSet");
		    if (clientProps.isEmpty()) {
			comps.remove(comp);
		    }
		}
	    } else {
		// Only create properties if there is a valid helpID
		if (hs != null) {
		    clientProps = new Hashtable(2);
		    clientProps.put("HelpSet", hs);
		    comps.put(comp, clientProps);
		}
	    }
	}	    
    }

    /**
     * Returns the HelpSet for a component.
     * HelpSets are stored in conjunction with helpIDs. It is possible for a
     * JComponent to have
     * a helpID without a HelpSet, but a JComponent cannot have a HelpSet 
     * without a helpID.
     * If the component doesn't have an associated helpID, traverse the
     * component's ancestors for a helpID. If the componet has a helpID but
     * doesn't have a HelpSet, return null.
     *
     * @see getHelpID
     */
    public static HelpSet getHelpSet(Component comp) {
	String helpID = null;
	HelpSet hs = null;
	// For JComponents use the client property
	// All others must check the internal component hashtable
	if (comp instanceof JComponent) {
	    helpID = (String) ((JComponent)comp).getClientProperty("HelpID");
	    if (helpID != null) {
		hs = (HelpSet) ((JComponent)comp).getClientProperty("HelpSet");
	    }
	} else {
	    if (comps != null) {
		Hashtable clientProps = (Hashtable)comps.get(comp);
		if (clientProps !=null) {
		    helpID = (String) clientProps.get("HelpID");
		    if (helpID != null) {
			hs = (HelpSet) clientProps.get("HelpSet");
		    }
		}
	    }
	}
	if (helpID != null) {
	    return hs;
	}
	Container parent = comp.getParent();
	if (parent != null) {
	    return getHelpSet(parent);
	} else {
	    return null;
	}
    }

    /**
     * Sets the helpID for a MenuItem.
     * If helpID is null, this method removes the helpID from the component.
     */
    public static void setHelpIDString(MenuItem comp, String helpID) {
	// For MenuItems we have an internal Hashtable of components and
	// their properties.
	
	// Initialize as necessary
	if (comps == null) {
	    comps = new Hashtable(5);
	}

	// See if this component has already set some client properties
	// If so update.
	// If not then create the client props (as needed) and add to
	// the internal Hashtable of components and properties
	Hashtable clientProps = (Hashtable) comps.get(comp);
	if (clientProps != null) {
	    if (helpID != null) {
		clientProps.put("HelpID", helpID);
	    } else {
		clientProps.remove("HelpID");
		if (clientProps.isEmpty()) {
		    comps.remove(comp);
		}
	    }
	} else {
	    // Only create properties if there is a valid helpID
	    if (helpID != null) {
		clientProps = new Hashtable(2);
		clientProps.put("HelpID", helpID);
		comps.put(comp, clientProps);
	    }
	}
    }	    

    /**
     * Returns the helpID for a MenuItem.
     * If the component doesn't have associated help, traverse the
     * component's ancestors for help. 
     */
    public static String getHelpIDString(MenuItem comp) {
	String helpID = null;
	if (comps != null) {
	    Hashtable clientProps = (Hashtable)comps.get(comp);
	    if (clientProps !=null) {
		helpID = (String) clientProps.get("HelpID");
	    }
	}

	// return the helpID if it exists
	if (helpID != null) {
	    return helpID;
	}

	// loop through the parents to try to find a valid helpID
	MenuContainer parent = comp.getParent();
	if (parent != null && parent instanceof MenuItem) {
	    return getHelpIDString((MenuItem)parent);
	} else {
	    return null;
	}
    }

    /**
     * Sets the HelpSet for a MenuItem.
     * If HelpSet is null, this method removes the HelpSet 
     * from the component.
     */
    public static void setHelpSet(MenuItem comp, HelpSet hs) {
	// For MenuItem and Components we have an internal Hashtable of 
	// components and their properties.

	// Initialize as necessary
	if (comps == null) {
	    comps = new Hashtable(5);
	}

	// See if this component has already set some client properties
	// If so update.
	// If not then create the client props (as needed) and add to
	// the internal Hashtable of components and properties
	Hashtable clientProps = (Hashtable) comps.get(comp);
	if (clientProps != null) {
	    if (hs != null) {
		clientProps.put("HelpSet", hs);
	    } else {
		clientProps.remove("HelpSet");
		if (clientProps.isEmpty()) {
		    comps.remove(comp);
		}
	    }
	} else {
	    // Only create properties if there is a valid helpID
	    if (hs != null) {
		clientProps = new Hashtable(2);
		clientProps.put("HelpSet", hs);
		comps.put(comp, clientProps);
	    }
	}
    }	    

    /**
     * Returns the HelpSet for a MenuItem.
     * HelpSets are stored in conjunction with helpIDs. It is possible for a
     * MenuItem to have a helpID without a HelpSet, but a MenuItem 
     * cannot have a HelpSet without a helpID.
     * If the component doesn't have an associated helpID, traverse the
     * component's ancestors for a helpID. If the componet has a helpID, but
     * doesn't have a HelpSet return null.
     *
     * @see getHelpID
     */
    public static HelpSet getHelpSet(MenuItem comp) {
	String helpID = null;
	HelpSet hs = null;
	if (comps != null) {
	    Hashtable clientProps = (Hashtable)comps.get(comp);
	    if (clientProps !=null) {
		helpID = (String) clientProps.get("HelpID");
		if (helpID != null) {
		    hs = (HelpSet) clientProps.get("HelpSet");
		}
	    }
	}
	if (helpID != null) {
	    return hs;
	}
	MenuContainer parent = comp.getParent();
	if (parent != null && parent instanceof MenuItem) {
	    return getHelpSet((MenuItem)parent);
	} else {
	    return null;
	}
    }

    /**
     * Context Sensitive Event Tracking
     *
     * Creates a new EventDispatchThread from which to dispatch events. This
     * method returns when stopModal is invoked.
     *
     * @return Object The object on which the event occurred. Null if
     * cancelled on an undetermined object.
     */
    public static Object trackCSEvents() {
	// Should the cursor change to a quesiton mark here or
	// require the user to change the cursor externally to this method?
	// The problem is that each component can have it's own cursor. 
	// For that reason it might be better to have the user change the
	// cusor rather than us.

	// To track context-sensitive events get the event queue and process
	// the events the same way EventDispatchThread does. Filter out
	// ContextSensitiveEvents SelectObject & Cancel (MouseDown & ???). 
	// Note: This code only handles mouse events. Accessiblity might
	// require additional functionality or event trapping

	// If the eventQueue can't be retrieved, the thread gets interrupted,
	// or the thread isn't a instanceof EventDispatchThread then return
	// a null as we won't be able to trap events.
	int eventNumber = -1;
 	try {
	    // can't use instanceof EventDispatchThread because 
	    // the class isn't public
	    if (Thread.currentThread().getClass().getName().endsWith("EventDispatchThread")) {
		EventQueue eq = null;
		// Find the eventQueue. If we can't get to it then just return
		// null since we won't be able to trap any events.
		
                try {
		    eq = Toolkit.getDefaultToolkit().getSystemEventQueue();
		} catch (Exception ee) {
                    debug(ee);
                    // return null;
		}

		// Safe guard
		if (eq == null) {
		    return null;
		}

                // Process the events until an object has been selected or
		// the context-sensitive search has been canceled.
		while (true) {
		    // This is essentially the body of EventDispatchThread
		    // modified to trap context-senstive events and act 
		    // appropriately
                    eventNumber++;
		    AWTEvent event = eq.getNextEvent();
		    Object src = event.getSource();
		    // can't call eq.dispatchEvent 
		    // so I pasted it's body here

                    // debug(event);
                    
		    // Not sure if I should suppress ActiveEvents or not
		    // Modal dialogs do. For now we will not suppress the
		    // ActiveEvent events

		    try {
			Class kActiveEvent
			    = Class.forName("java.awt.ActiveEvent");
			if (kActiveEvent != null) {
			    // in 1.2 or later
			    if (kActiveEvent.isInstance(event)) {
				// we have an ActiveEvent in our hands
				
				Method m = null;
				Class types[] = {};
				Object args[] = {};
				m = kActiveEvent.getMethod("dispatch",
							   types);
				if (m != null) {
				    m.invoke(event, args);
				    continue;
				}
			    }
			}
		    } catch (NoSuchMethodError ex) {
				// ignore
		    } catch (ClassNotFoundException ex) {
				// ignore
		    } catch (IllegalAccessException ex) {
				// ignore
		    } catch (InvocationTargetException ex) {
				// ignore
		    } catch (NoSuchMethodException ex) {
				// ignore
		    }

		    if (src instanceof Component) {
			// Trap the context-sensitive events here
			if (event instanceof InputEvent) {
			    if (event instanceof KeyEvent) {
				KeyEvent e = (KeyEvent) event;
				// if this is the cancel key then exit
				// otherwise pass all other keys up
				if (e.getKeyCode() == KeyEvent.VK_CANCEL) {
				    e.consume();
				    return null;
				} else {
				    dispatchEvent(src, event);
				}
			    } else if (event instanceof MouseEvent) {
				MouseEvent e = (MouseEvent) event;
				int eID = e.getID();
				if (eID == MouseEvent.MOUSE_CLICKED ||
				    eID == MouseEvent.MOUSE_PRESSED ||
				    eID == MouseEvent.MOUSE_RELEASED) {
				    // This should be MB1 only but the 
				    // SwingUtilities code doesn't work
				    if (eID == MouseEvent.MOUSE_CLICKED) {
					if (eventNumber == 0) {
					    dispatchEvent(src,event);
					    continue;
					}
					e.consume();
					return getDeepestObjectAt(src, e.getX(), e.getY());
				    }
				    e.consume();
				} else {
				    e.consume();
				}
			    } else {
				dispatchEvent(src, event);
			    }
			} else {
			    dispatchEvent(src, event);
			}
		    } else if (src instanceof MenuComponent) {
			if (src instanceof MenuItem) {
			    // Trap the context-sensitive events here
			    if (event instanceof InputEvent) {
				if (event instanceof KeyEvent) {
				    KeyEvent e = (KeyEvent) event;
				// if this is the cancel key then exit
				// otherwise pass all other keys up
				    if (e.getKeyCode() == KeyEvent.VK_CANCEL) {
					e.consume();
					return null;
				    } else {
					dispatchEvent(src, event);
				    }
				} else if (event instanceof MouseEvent) {
				    MouseEvent e = (MouseEvent) event;
				    int eID = e.getID();
				    if (eID == MouseEvent.MOUSE_CLICKED ||
					eID == MouseEvent.MOUSE_PRESSED ||
					eID == MouseEvent.MOUSE_RELEASED) {
					// This should be MB1 only but the 
					// SwingUtilities code doesn't work
					if (eID == MouseEvent.MOUSE_CLICKED) {
					    if (eventNumber == 0) {
						dispatchEvent(src,event);
						continue;
					    }
					    e.consume();
					    return getDeepestObjectAt(src, e.getX(), e.getY());
					}
					e.consume();
				    } else {
					e.consume();
				    }
				} else {
				    dispatchEvent(src, event);
				}
			    }
			} else {
			    // Any other type of MenuComponent just pass the
			    // events through
			    dispatchEvent(src, event);
			}
		    } else {
			System.err.println("unable to dispatch event: " + event);
                        //debug("unable to dispatch event: " + event);
		    }
		}
	    }
	} catch(InterruptedException e){
	debug ("InterrupedExeception");
	}
	debug ("Fall Through code");
	return null;
    }

    private static void dispatchEvent(Object src, AWTEvent event) {
	if (src instanceof Component) {
	    ((Component) src).dispatchEvent(event);
	} else if (src instanceof MenuComponent) {
	    ((MenuComponent) src).dispatchEvent(event); 
	}
    }

    /**
     * Gets the higest visible component in a ancestor hierarchy at
     * specific x,y coordinates
     */
    private static Object getDeepestObjectAt(Object parent, 
					     int x, int y) {
	if(parent != null && parent instanceof Container) {
	    // use a copy of 1.3 Container.findComponentAt
	    Component child = findComponentAt((Container)parent, x, y);
	    if(child != null && child != parent) {
		if (child instanceof JRootPane) {
		    JLayeredPane lp = ((JRootPane)child).getLayeredPane();
		    Rectangle b = lp.getBounds();
		    child = (Component)getDeepestObjectAt(lp, x - b.x,y-b.y);
		    if(child != null) {
			return child;
		    }
		} else {
			return child;
		}
	    }
	}
	// if the parent is not a Container then it might be a MenuItem.
	// But even if it isn't a MenuItem just return the parent because
	// that's a close as we can come.
	return parent;
    }

    private static Component findComponentAt(Container cont, int x, int y) {
	synchronized (cont.getTreeLock()) {
	   return findComponentAt(cont, x, y, true, false);
	}
    }

    private static Component findComponentAt(Container cont, int x, int y, boolean ignoreEnabled,
				    boolean ignoreGlassPane)
    {
        if (!(cont.contains(x, y) && cont.isVisible() && (ignoreEnabled || cont.isEnabled()))) {
	    return null;
	}
	int ncomponents = cont.getComponentCount();
	Component component[] = cont.getComponents();

	Component glassPane = null;
	if (ignoreGlassPane && (cont instanceof JRootPane)) {
	    glassPane = ((JRootPane)cont).getGlassPane();
	}

	// Two passes: see comment in sun.awt.SunGraphicsCallback
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = component[i];
	    if (comp != null && comp != glassPane &&
                !(comp.getPeer() instanceof java.awt.peer.LightweightPeer)) {
                    Point point = comp.getLocation();
		    if (comp instanceof Container) {
		        comp = findComponentAt((Container)comp,
                        x - point.x,
                        y - point.y,
                        ignoreEnabled,
                        ignoreGlassPane);
		    } else {
		        comp = comp.locate(x - point.x, y - point.y);
		    }
		    if (comp != null && comp.isVisible() &&
		        (ignoreEnabled || comp.isEnabled()))
		    {
		        return comp;
		    }
	    }
	}
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = component[i];
	    if (comp != null && comp != glassPane &&
                comp.getPeer() instanceof java.awt.peer.LightweightPeer) {
                    Point point = comp.getLocation();
		    if (comp instanceof Container) {
		        comp = findComponentAt((Container)comp,
                        x - point.x,
                        y - point.y,
                        ignoreEnabled,
                        ignoreGlassPane);
		    } else {
		        comp = comp.locate(x - point.x, y - point.y);
		    }
		    if (comp != null && comp.isVisible() &&
		        (ignoreEnabled || comp.isEnabled()))
		    {
		        return comp;
		    }
	    }
	}
	return cont;
    }

    /**
     * An ActionListener that displays the help of the
     * object that currently has focus. This method is used
     * to enable HelpKey action listening for components other than
     * the RootPane. This listener determines if the
     * object with the current focus has a helpID. If it does, the helpID 
     * is displayed,
     * otherwise the helpID on the action's source is displayed (if one exists).
     * 
     * @see HelpBroker.enableHelpKey
     */
    public static class DisplayHelpFromFocus implements ActionListener {

	private HelpBroker hb;

	public DisplayHelpFromFocus(HelpBroker hb) {
	    if (hb == null) {
		throw new NullPointerException("hb");
	    }
	    this.hb = hb;
	}

        public void actionPerformed(ActionEvent e) {
	    Component src = (Component) e.getSource();
	    // Start by setting the ownerWindow in the HelpBroker
            if (hb instanceof DefaultHelpBroker) {
                Window owner = SwingUtilities.windowForComponent(src);
                ((DefaultHelpBroker)hb).setActivationWindow(owner);
            }

            Component comp = CSH.findFocusOwner(src);
	    if (comp == null) {
		comp = src;
	    }

	    String helpID = CSH.getHelpIDString(comp);
	    HelpSet hs = CSH.getHelpSet(comp);
	    if (hs == null) {
		hs = hb.getHelpSet();
	    }
	    try {
		ID id = null;
		try {
		    id = ID.create(helpID, hs);
		} catch (BadIDException exp2) {
		    id = hs.getHomeID();
		    if (id == null) {
			throw (exp2);
		    }
		}
		hb.setCurrentID(id);
		hb.setDisplayed(true);
	    } catch (Exception e2) {
		e2.printStackTrace();
	    }
	}
    }

    /**
     * Returns the deepest armed MenuItem from Menu hierarchy.
     */
    private static JMenuItem findArmedMenuItem(JMenu menu) {
        if (menu == null) {
            return null;
        }
        synchronized (menu.getTreeLock()) {
            if (!menu.isArmed()) {
                return null;
            }
            for(int i = 0, n = menu.getMenuComponentCount(); i < n; i++) {
                Component c = menu.getMenuComponent(i);
                if (c instanceof JMenuItem && ((JMenuItem)c).isArmed()) {
                    if (c instanceof JMenu) {
                        c = findArmedMenuItem((JMenu)c);
                    }
                    return (JMenuItem)c;
                }
            }
            return menu;
        }
    }
    
    /**
     * Returns the deepest armed MenuItem from Menu hierarchy or invoker 
     * of this PopupMenu in case of no MenuItem is armed.
     */
    private static Component findArmedComponent(JPopupMenu popup) {
        if (popup == null) {
            return null;
        }
        synchronized (popup.getTreeLock()) {
            if (!popup.isVisible()) {
                return null;
            }
            for (int i = 0, n = popup.getComponentCount(); i < n; i++) {
                Component c = popup.getComponent(i);
                if (c instanceof JMenuItem && ((JMenuItem)c).isArmed()) {
                    if (c instanceof JMenu) {                    
                        c = findArmedMenuItem((JMenu)c);
                    }
                    return c;
                }
            }
            return popup.getInvoker();
        }
    }
    
    /**
     * Returns the child component which has focus with respects
     * of PopupMenu visibility.
     */
    private static Component findFocusOwner(Component c) {
        synchronized (c.getTreeLock()) {        
            if (c instanceof JPopupMenu) {
                return findArmedComponent((JPopupMenu)c);
            }
        
            if (c instanceof JComponent && ((JComponent)c).hasFocus()) {
                if (c instanceof JMenu) {                    
                    c = findArmedMenuItem((JMenu)c);
                }
                return c;
            }
        
            if (c instanceof Container) {
                for (int i = 0, n = ((Container)c).getComponentCount(); i < n; i++) {
                    Component focusOwner = findFocusOwner(((Container)c).getComponent(i));
                    if (focusOwner != null) {
                        return focusOwner;
                    }
                }
            }
            return null;  // Component doesn't have hasFocus().
        }
    }
    
    /** 
     * An ActionListener that displays help on a 
     * selected object after tracking context-sensitive events. 
     * It is normally activated
     * from a button. It uses CSH.trackingCSEvents to track context-sensitive  
     * events and when an object is selected it gets
     * the helpID for the object and displays the helpID in the help viewer.
     */
    public static  class DisplayHelpAfterTracking implements ActionListener {

	private HelpBroker hb;

	public DisplayHelpAfterTracking(HelpBroker hb) {
	    if (hb == null) {
		throw new NullPointerException("hb");
	    }
	    this.hb = hb;
	}

	public void actionPerformed(ActionEvent e) {
            Cursor onItemCursor, oldCursor;

            // Make sure that LAF is installed.
            // It is necessery for UIManager.get("HelpOnItemCursor");
            SwingHelpUtilities.installUIDefaults();

            // Get the onItemCursor
            onItemCursor = (Cursor) UIManager.get("HelpOnItemCursor");
            if (onItemCursor == null) {
                return;
            }

	    // change all the cursors on all windows
            Vector topComponents = null;
            cursors = null;
            
            if (onItemCursor != null) {
                cursors = new Hashtable();
                topComponents = getTopContainers(e.getSource());
                Enumeration enum = topComponents.elements();
                while (enum.hasMoreElements()) {
                    setCursor((Container)enum.nextElement(), onItemCursor);
                }
            }

            // track the CS Events and display help on the component
	    Object obj = CSH.trackCSEvents();

	    // Start by setting the ownerWindow in the HelpBroker
	    Component comp = (Component) e.getSource();
            try {
                Window owner = (Window)SwingUtilities.getAncestorOfClass(Class.forName("java.awt.Window"), comp);
                if (hb instanceof javax.help.DefaultHelpBroker) {
		    ((DefaultHelpBroker)hb).setActivationWindow(owner);
		}
            } catch (ClassNotFoundException ex) {
                // Ignore
            }

	    // OK now do the CSH stuff
	    String helpID = null;
	    HelpSet hs = null;
	    if (obj != null && obj instanceof Component) {
		helpID = CSH.getHelpIDString((Component)obj);
		hs = CSH.getHelpSet((Component)obj);
	    } else if (obj != null && obj instanceof MenuItem) {
		helpID = CSH.getHelpIDString((MenuItem)obj);
		hs = CSH.getHelpSet((MenuItem)obj);
	    }
	    if (hs == null) {
		hs = hb.getHelpSet();
	    }
 	    try {
		ID id = ID.create(helpID, hs);
		if (id == null) {
		    id = hs.getHomeID();
		}
 		hb.setCurrentID(id);
		hb.setDisplayed(true);
 	    } catch (Exception e2) {
 	    }

	    // restore the old cursors
            if (topComponents != null) {
                Enumeration containers = topComponents.elements();
                while (containers.hasMoreElements()) {
                    resetAndRestoreCursors((Container)containers.nextElement());
                }
            }
            cursors = null;
	}

	private Hashtable cursors;
	private Stack cursorStack;

        /*
         * Get all top level containers to change it's cursors
         */
        private Vector getTopContainers(Object source) {
            // This method is used to obtain all top level components of application
            // for which the changing of cursor to question mark is wanted.
            // Method Frame.getFrames() is used to get list of Frames and 
            // Frame.getOwnedWindows() method on elements of the list
            // returns all Windows, Dialogs etc. It works correctly in application.
            // Problem is in applets. There is no way how to get reference to applets
            // from elsewhere than applet itself. So, if request for CSH (this means
            // pressing help button or select help menu item) does't come from component
            // in a Applet, cursor for applets is not changed to question mark. Only for 
            // Frames, Windows and Dialogs is cursor changed properly.
            
            Vector containers = new Vector();
            Component topComponent = null;
            if (source instanceof Component) {
                topComponent = getTopComponentForComponent((Component)source);
            } else if (source instanceof MenuItem) {
                topComponent = getTopComponentForMenuItem((MenuItem)source);                
            }
            if (topComponent instanceof Applet) {
                Enumeration applets = ((Applet)topComponent).getAppletContext().getApplets();
                while (applets.hasMoreElements()) {
                    containers.add(applets.nextElement());
                }
            }
            Frame frames[] = Frame.getFrames();
            for (int i = 0; i < frames.length; i++) {
                Window[] windows = frames[i].getOwnedWindows();
                for (int j = 0; j < windows.length; j++) {
                    containers.add(windows[j]);
                }
                if (!containers.contains(frames[i])) {
                    containers.add(frames[i]);
                }
            }
            return containers;
        }
        
	/*
	 * Get the top most component for a given component 
	 */
	private Component getTopComponentForComponent(Component comp) {
            Component parent = comp;
            while (parent != null) {
                comp = parent;
                if (parent instanceof Window) {
                    break;
                }
                if (parent instanceof Applet) {
                    break;
                }
                if (parent instanceof MenuElement) {
		    if (parent instanceof JPopupMenu) {
			parent = ((JPopupMenu)parent).getInvoker();
		    } else {
			parent = ((MenuElement)parent).getComponent();
		    }
		}
                parent = parent.getParent();
            }
            return comp;
	}


	/*
	 * Get the top most component for a given MenuItem.
	 * This is a little tricky
	 */
	private Component getTopComponentForMenuItem(MenuItem mi) {
	    MenuContainer tmcont = mi.getParent();;
	    MenuContainer parent = tmcont;
	    while (parent != null) {
		if (parent instanceof Component) {
		    break;
		}
		tmcont = parent;
		parent = ((MenuComponent)tmcont).getParent();
	    }

	    if (parent == null) {
		return null;
	    }
	    Component comp = getTopComponentForComponent((Component) parent);
	    return comp;
	}


	/*
	 * Set the cursor recursively on all the objects in the objects
	 * highest most hierarchy
	 */
        private void setCursor(Component comp, Cursor cursor) {
	    debug("setCursor");
            if (comp == null) return;
	    cursorStack = new Stack();
	    setAndStoreCursors(comp, cursor);
            cursorStack = null;
        }

	/*
	 * Set the cursor for a component and its children. 
	 * Store the old cursors for future resetting
	 */
	private void setAndStoreCursors(Component comp, Cursor cursor) {
	    boolean addedToStack = false;
	    Cursor compCursor = comp.getCursor();
	    if (cursorStack.empty()) {
		cursorStack.push(compCursor);
		cursors.put (comp, compCursor);
		addedToStack = true;
		debug ("store cursor " + compCursor + " on " + comp);
	    } else if (compCursor != (Cursor)cursorStack.peek()) {
		cursorStack.push(compCursor);
		cursors.put (comp, compCursor);
		addedToStack = true;
		debug ("store cursor " + compCursor + " on " + comp);
	    }
	    if (comp instanceof Container) {
		Container cont = (Container) comp;
		int ncomponents = cont.getComponentCount();
		Component component[] = cont.getComponents();
		for (int i = 0 ; i < ncomponents ; i++) {
		    Component comp2 = component[i];
		    if (comp != null) {
			setAndStoreCursors(comp2, cursor);
		    }
		}
	    }
	    comp.setCursor(cursor);
	    debug ("set cursor on " + comp);
	    if (addedToStack) {
		cursorStack.pop();
	    }
	}

	/*
	 * Reset Hashtables and restore the cursors for a component and
	 * it children
	 */
	private void resetAndRestoreCursors(Component comp) {
	    debug ("resetAndRestoreCursors");
            if (comp == null) return;
	    cursorStack = new Stack();
	    restoreCursors(comp);
	    cursorStack = null;
	}

	/*
	 * Actually restore the cursor for a component and its children
	 */
	private void restoreCursors(Component comp) {
	    boolean addedToStack = false;
	    Cursor oldCursor = (Cursor) cursors.get(comp); 
	    if (oldCursor != null) {
		cursorStack.push(oldCursor);
		comp.setCursor(oldCursor);
		debug("restored cursor " + oldCursor + " on " + comp);
		addedToStack = true;
	    } else if (!cursorStack.empty()) {
		comp.setCursor((Cursor)cursorStack.peek());
		debug("restored cursor " + (Cursor)cursorStack.peek() + " on " + comp);
	    }
	    if (comp instanceof Container) {
		Container cont = (Container) comp;
		int ncomponents = cont.getComponentCount();
		Component component[] = cont.getComponents();
		for (int i = 0 ; i < ncomponents ; i++) {
		    Component comp2 = component[i];
		    if (comp != null) {
			restoreCursors(comp2);
		    }
		}
	    }
	    if (addedToStack) {
		cursorStack.pop();
	    }
	}
    }
    
    /**
     * An ActionListener that 
     * gets the helpID for the action source and displays the helpID in the
     * help viewer.
     *
     * @see HelpBroker.enableHelp
     */
    public static class DisplayHelpFromSource implements ActionListener {

	private HelpBroker hb;

	public DisplayHelpFromSource(HelpBroker hb) {
	    if (hb == null) {
		throw new NullPointerException("hb");
	    }
	    this.hb = hb;
	}

	public void actionPerformed(ActionEvent e) {
	    Object source = e.getSource();

	    // Start by setting the ownerWindow in the HelpBroker
	    // Try to figure out the ActivationOwner
	    if (source instanceof Component) {
		Component comp = (Component) e.getSource();
		if (comp instanceof javax.swing.JMenuItem) {
		    // Loop through until the top is reached
		    while (comp instanceof javax.swing.JMenuItem) {
			Container parent = comp.getParent();
			if (parent instanceof javax.swing.JPopupMenu) {
			    comp = ((JPopupMenu)parent).getInvoker();
			    continue;
			}
			break;
		    }
		}
		try {
		    Window owner = (Window)SwingUtilities.getAncestorOfClass(Class.forName("java.awt.Window"), comp);
		    debug ("owner = " + owner);
		    if (hb instanceof javax.help.DefaultHelpBroker) {
			((DefaultHelpBroker)hb).setActivationWindow(owner);
		    }
		}
		catch (ClassNotFoundException ex) {
		    // Ignore
		}
	    } else if (source instanceof MenuItem) {
		MenuItem mi = (MenuItem) e.getSource();
		Window owner = null;
		while (true) {
		    MenuContainer mc = mi.getParent();
		    if (mc instanceof Menu) {
			mi = (MenuItem)mc;
			continue;
		    } else if (mc instanceof MenuBar) {
			try {
			    owner = (Window)SwingUtilities.getAncestorOfClass(Class.forName("java.awt.Window"), (Component)((MenuBar)mc).getParent());
			} 
			catch (ClassNotFoundException ex) {
			}
			break;
		    } else if (mc instanceof Frame) {
			owner = (Window)mc;
			break;
		    } else if (mc instanceof Component) {
			try {
			    owner = (Window)SwingUtilities.getAncestorOfClass(Class.forName("java.awt.Window"), (Component) mc);
			} 
			catch (ClassNotFoundException ex) {
			}
			break;
		    }
		}
		debug ("owner = " + owner);
		if (hb instanceof javax.help.DefaultHelpBroker) {
		    ((DefaultHelpBroker)hb).setActivationWindow(owner);
		}
	    }

	    // Now get the help for the source
	    String helpID = null;
	    HelpSet hs = null;
	    if (source instanceof Component) {
		Component compSource = (Component)source;
		helpID = CSH.getHelpIDString(compSource);
		hs = CSH.getHelpSet(compSource);
	    } else if (source instanceof MenuItem) {
		MenuItem miSource = (MenuItem)source;
		helpID = CSH.getHelpIDString(miSource);
		hs = CSH.getHelpSet(miSource);
	    } else {
		// unsupported
		return;
	    }
	    if (hs == null) {
		hs = hb.getHelpSet();
	    }
	    try {
		ID id = ID.create(helpID, hs);
		if (id == null) {
		    id = hs.getHomeID();
		}
		hb.setCurrentID(id);
		hb.setDisplayed(true);
	    } catch (Exception e2) {
		// ERRORS
		System.err.println("trouble in HelpActionListener");
                
                if (debug)
                    e2.printStackTrace();
	    }
	}
    }

    /**
     * Debugging code...
     */

    private static final boolean debug = false;
    private static void debug(Object msg) {
	if (debug) {
	    System.err.println("CSH: "+msg);
	}
    }
 
}

