/*
 * @(#) JHelp.java 1.65 - last change made 05/04/01
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

import java.net.URL;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.LookAndFeel;
import javax.accessibility.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Locale;
import java.io.InputStream;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.help.plaf.HelpUI;
import javax.help.event.HelpSetListener;
import javax.help.event.HelpSetEvent;
import javax.help.Map.ID;

/**
 * Displays HelpSet data with navigators and a content viewer.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Stepan Marek
 * @version	1.65	05/04/01
 */
public class JHelp extends JComponent implements HelpSetListener, Accessible {
    protected TextHelpModel helpModel;
    protected Vector navigators;
    protected boolean navDisplayed;

    protected JHelpContentViewer contentViewer;

    /**
     * Create a JHelp with a JHelpContentViewer and all Navigators 
     * requested in the HelpSet.
     *
     * @param hs The HelpSet. If hs is null the JHelp is created with a 
     * TextHelpModel with no HelpSet.
     */
    public JHelp(HelpSet hs) {
	this(new DefaultHelpModel(hs));
    }
    
    /**
     * Create a JHelp component without a TextHelpModel.
     */
    public JHelp() {
	this((TextHelpModel)null);
    }

    /**
     * Create a JHelp using the TextHelpModel.
     *
     * @param model A model to use for the content and all the navigators. If <tt>model</tt>
     * is null it is the same as creating without a TextHelpModel
     */
    public JHelp(TextHelpModel model) {
	super();

	navigators = new Vector();
	navDisplayed = true;

	// HERE -- need to do something about doc title changes....

	this.contentViewer = new JHelpContentViewer(model);

	setModel(model);
	if (model != null) {
	    setupNavigators();
	}

        updateUI();
    }

    protected void setupNavigators() {
	HelpSet hs = helpModel.getHelpSet();
	// Simply return if the hs is null
	if (hs == null) {
	    return;
	}

	// Now add all the navigators
	NavigatorView views[] = hs.getNavigatorViews();

	debug("views: "+views);

	for (int i=0; i<views.length; i++) {
	    debug("  processing info: "+views[i]);

	    // We are currently assuming all the Navigators are JComponents
	    JHelpNavigator nav
		= (JHelpNavigator) views[i].createNavigator(helpModel);
	    
	    if (nav == null) {
		// For now...
		debug("no JHelpNavigator for given info");
	    } else {
		debug("  adding the navigator");
		navigators.addElement(nav);
		// HERE -- I don't think we want to change again the model
		//		    this.addHelpNavigator(nav);
            }
	}
        
	// Now add all the "child" HelpSets
	for (Enumeration e = hs.getHelpSets();
	     e.hasMoreElements(); ) {
	    HelpSet ehs = (HelpSet) e.nextElement();
	    addHelpSet(ehs);
	}
    }

    /**
     * Sets the HelpModel that provides the data.
     *
     * @param newModel The new Model. If <tt>newModel</tt> is null the internal model is set
     * to null.
     */
    public void setModel(TextHelpModel newModel) {
	TextHelpModel oldModel = helpModel;
        if (newModel != oldModel) {
	    if (oldModel != null) {
		oldModel.getHelpSet().removeHelpSetListener(this);
	    }
            helpModel = newModel;
	    if (newModel != null) {
		HelpSet hs = newModel.getHelpSet();
		if (hs != null) {
		    hs.addHelpSetListener(this);
		}
	    }
            firePropertyChange("helpModel", oldModel, helpModel);

	    // Now tell all the components we control
	    contentViewer.setModel((TextHelpModel) newModel);

	    // We'll have to destroy all of the navigators and
	    // reload them from the HelpSet
	    HelpUI help = getUI();

	    // Skip the navigators if the ui hasn't been setup yet.
	    if (help == null) {
		return;
	    }

	    for (Enumeration e = getHelpNavigators();
		 e.hasMoreElements(); ) {
		JHelpNavigator nav = (JHelpNavigator) e.nextElement();
		help.removeNavigator(nav);
	    }
	    navigators.removeAllElements();
	    setupNavigators();
	    updateUI();
        }
    }

    /**
     * @return The HelpModel that is providing the data.
     */
    public TextHelpModel getModel() {
	return helpModel;
    }

    // HERE - need customizers, etc... -epll
    // HERE - this is probably broken
    /**
     * Set the URL to the HelpSet.  This forces the HelpSet to be reloaded.
     *
     * @param spec Where to locate the HelpSet. A null spec is valid
     */
    public void setHelpSetSpec(String spec) {
	URL url;
	HelpSet hs;
	ClassLoader loader = this.getClass().getClassLoader();
	try {
	    url = new URL(spec);
	    hs = new HelpSet(loader, url);
	} catch (Exception ex) {
	    System.err.println("Trouble setting HelpSetSpec to spec |"+spec+"|");
	    System.err.println("  ex: "+ex);
	    hs = null;
	}
	contentViewer.setModel(new DefaultHelpModel(hs));
	setModel(contentViewer.getModel());
	updateUI();
    }

    /**
     * @return The URL to the HelpSet.
     */
    public URL getHelpSetURL() {
	HelpSet hs = contentViewer.getModel().getHelpSet();
	if (hs == null) {
	    return null;
	}
	return hs.getHelpSetURL();
    }

    // === The JComponent methods

    /**
     * Sets the HelpUI that will provide the current look and feel.
     * @param ui The HelpUI to set for this component. A null value for ui
     * is valid.
     */
    public void setUI(HelpUI ui) {
	if ((HelpUI)this.ui != ui) {
	    super.setUI(ui);
	}
    }

    /**
     * Returns the HelpUI that is providing the current look and feel.
     */
    public HelpUI getUI() {
	return (HelpUI)ui;
    }

    /**
     * Replaces the UI with the latest version from the default 
     * UIFactory.
     *
     * @overrides updateUI in class JComponent
     */
    public void updateUI() {
        SwingHelpUtilities.installUIDefaults();
        setUI((HelpUI)UIManager.getUI(this));
        invalidate();
    }

    /**
     * @return "HelpUI"
     */
    public String getUIClassID()
    {
        return "HelpUI";
    }

    /*
     * Findd the navigator with a given name.
     */
    private JHelpNavigator findNavigator(String name) {
	debug("findNavigator("+name+")");
	for (Enumeration e = getHelpNavigators();
	     e.hasMoreElements(); ) {
	    JHelpNavigator nav = (JHelpNavigator) e.nextElement();
	    debug("  nav: "+nav);
	    debug("  nav.getName: "+nav.getNavigatorName());
	    if (nav.getNavigatorName().equals(name)) {
		return nav;
	    }
	}
	return null;
    }

    /**
     * Adds a new HelpSet to "our" HelpSet.
     *
     * @param e HelpSetEvent
     * @see javax.help.event.HelpSetEvent
     * @see javax.help.event.HelpSetListener
     */
    public void helpSetAdded(HelpSetEvent e) {
	debug("helpSetAdded("+e+")");
	HelpSet ehs = e.getHelpSet();
	addHelpSet(ehs);
    }
    
    /*
     * Adds a HelpSet.
     */
    private void addHelpSet(HelpSet ehs) {
        debug("helpset :"+ehs);
	NavigatorView eviews[] = ehs.getNavigatorViews();
        
        //if master help set is created using new HelpSet() -without arguments it hasn't got any navigators to merge
        //we will create new navigators

        int count = 0;
        for (Enumeration e = getHelpNavigators() ; e.hasMoreElements() ;e.nextElement()) {
            ++count;
        }
        if(count == 0){
            debug("master helpset without navigators");            
            HelpModel newModel= new DefaultHelpModel(ehs);
            setModel((TextHelpModel)newModel);
            setupNavigators();
            return;
        }
        
             
    	for (int i=0; i<eviews.length; i++) {
	    String n = eviews[i].getName();
	    debug("addHelpSet: looking for navigator for "+n);
	    JHelpNavigator nav = findNavigator(n);
	    if (nav != null) {
		debug("   found");
		if (nav.canMerge(eviews[i])) {
		    debug("  canMerge: true; merging...");
		    nav.merge(eviews[i]);
		} else {
		    debug("  canMerge: false");
		}
	    } else {
		debug("   not found");
	    }
	}
	// In this version, we can only add views that appear at the top
    }

    /**
     * Removes a HelpSet from "our" HelpSet.
     */
    public void helpSetRemoved(HelpSetEvent e) {
	debug("helpSetRemoved("+e+")");
	HelpSet ehs = e.getHelpSet();
	removeHelpSet(ehs);
    }

    private void removeHelpSet(HelpSet ehs) {
	NavigatorView eviews[] = ehs.getNavigatorViews();

	for (int i=0; i<eviews.length; i++) {
	    String n = eviews[i].getName();
	    debug("removeHelpSet: looking for navigator for "+n);
	    JHelpNavigator nav = findNavigator(n);
	    if (nav != null) {
		debug("   found");
		if (nav.canMerge(eviews[i])) {
		    debug("  canMerge: true; removing...");
		    nav.remove(eviews[i]);
		} else {
		    debug("  canMerge: false");
		}
	    } else {
		debug("   not found");
	    }
	}
    }

    /**
     * Visits a given ID.  Propagates down into the model.
     *
     * @param id The ID to visit. Null id is valid for TextHelpModel.setCurrentID.
     * @exception InvalidHelpSetContextException if id.hs is not contained in getHelpSet()
     */
    public void setCurrentID(ID id) throws InvalidHelpSetContextException {
	if (helpModel != null) {
	    helpModel.setCurrentID(id);
	}
    }

    /**
     * Convenience version of the above. The implicit HelpSet is
     * the current HelpSet.
     *
     * @param id The String to visit. Null id is valid for TextHelpModel.setCurrentID.
     * @exception BadIDException if the string is not in the map for the HelpSet.
     */
    public void setCurrentID(String id) throws BadIDException {
	try {
	    helpModel.setCurrentID(ID.create(id, getModel().getHelpSet()));
	} catch (InvalidHelpSetContextException ex) {
	    // cannot happen
	}
    }

    /**
     * Visits a given URL.  Propagates down into the model.
     *
     * @param url The URL to visit
     */
    public void setCurrentURL(URL url) {
	helpModel.setCurrentURL(url);
    }

    /**
     * A JHelp can have a number of navigators.
     * One of navigators is active.
     * How they are presented depends on the UI, but they may be a collection
     * of tabs, with the active tab being at the front.
     * <br>
     * Each navigator listens to changes to the HelpModel.
     * A navigator can also tell the model to change--the change
     * is propagated to the other navigators, this component, and
     * the content viewer if they all use the same
     * HelpModel instance.
     *
     * @param navigator The Navigator to explicitly add to the JHelp.
     */
    public void addHelpNavigator(JHelpNavigator navigator) {
	debug("addHelpNavigator("+navigator+")");
	navigators.addElement(navigator);
	HelpUI help = getUI();
	help.addNavigator(navigator);
	
	// force a common model
	navigator.setModel(getModel());
    }

    /**
     * Removes a navigator.
     *
     * @param navigator The Navigator to explicitly add to the JHelp. 
     */
    public void removeHelpNavigator(JHelpNavigator navigator) {
	debug("removeHelpNavigator("+navigator+")");
	if (navigator == null) {
	    throw new NullPointerException("navigator");
	}
	navigators.removeElement(navigator);
	HelpUI help = getUI();
	help.removeNavigator(navigator);
    }

    /**
     * @return An Enumeration of HelpNavigators in the HelpUI.
     */
    public Enumeration getHelpNavigators() {
	return navigators.elements();
    }

    /**
     * Sets the current navigator in the HelpUI.
     *
     * @param navigator The navigator
     * @exception throws InvalidNavigatorException if not a one of HELPUI
     *   navigators.
     */
    public void setCurrentNavigator(JHelpNavigator navigator) {
	HelpUI help = getUI();
	help.setCurrentNavigator(navigator);
    }

    /**
     * @return The current navigator in the HelpUI
     */
    public JHelpNavigator getCurrentNavigator() {
	HelpUI help = getUI();
	return help.getCurrentNavigator();
    }

    /**
     * Hidess/Displays the Navigators in the HelpUI.
     *
     * @displayed Whether to display or not
     */
    public void setNavigatorDisplayed(boolean displayed) {
	if (navDisplayed != displayed) {
	    navDisplayed = displayed;
	    firePropertyChange("navigatorDisplayed", !displayed, displayed);
	}
    }

    /**
     * Determines if the Navigators are hidden/displayed in the HelpUI.
     *
     * @return Are the navigators displayed?
     */
    public boolean isNavigatorDisplayed() {
	return navDisplayed;
    }

    /**
     * Retrieves what is the current content viewer
     * Read-Only property?
     */
    public JHelpContentViewer getContentViewer() {
	return contentViewer;
    }

    /**
     * Debug code
     */

    private boolean debug = false;
    private void debug(String msg) {
	if (debug) {
	    System.err.println("JHelp: "+msg);
	}
    }

    /*
     * Make sure the Look and Feel will be set for the Help Component
     */
    static {
	SwingHelpUtilities.installLookAndFeelDefaults();
    }

/////////////////
// Accessibility support
////////////////

    /**
     * Get the AccessibleContext associated with this JComponent.
     *
     * @return The AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJHelp();
        }
        return accessibleContext;
    }

    /**
     * The class used to obtain the accessible role for this object.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJHelp extends AccessibleJComponent {

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
    }
}

