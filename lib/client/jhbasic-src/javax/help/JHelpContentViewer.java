/*
 * @(#) JHelpContentViewer.java 1.31 - last change made 05/04/01
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
import javax.swing.JEditorPane;
import javax.accessibility.*;
import java.util.Hashtable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.help.event.*;
import javax.help.plaf.HelpContentViewerUI;
import javax.swing.text.EditorKit;
import javax.help.Map.ID;

/**
 * A component to represent the Help viewer that can be embedded if desired.
 *
 * @author Eduardo Pelegri-Llopart
 * @version   1.31     05/04/01
 */
public class JHelpContentViewer extends JComponent implements Accessible{
    protected TextHelpModel model;

    /**
     * Creates a JHelp with an instance of DefaultHelpModel as its data model.
     *
     * @param hs The HelpSet that provides context information. A null hs is valid
     * and creates a TextHelpModel with no HelpSet defined.
     */
    public JHelpContentViewer(HelpSet hs) {
	super();
	setModel(new DefaultHelpModel(hs));
	updateUI();
    }
    
    /**
     * Creates a JHelp with a default TextHelpModel.
     */
    public JHelpContentViewer() {
	super();
	setModel(new DefaultHelpModel(null)); // no HelpSet here!
	updateUI();
    }

    /**
     * Creates a JHelp with an specific TextHelpModel as its data model.
     *
     * @param model The TextHelpModel. A null model is valid.
     */
    public JHelpContentViewer(TextHelpModel model) {
	super();
	setModel(model);
	updateUI();
    }
    
    /**
     * Sets the HelpModel that provides the data.
     * Necessary to convert navigation action into visits...
     *
     * @param newModel The new Model to provide events for this viewer.
     */
    public void setModel(TextHelpModel newModel) {
	debug("setModel: "+newModel);
	TextHelpModel oldModel = model;
        if (newModel != oldModel) {
            model = newModel;
            firePropertyChange("helpModel", oldModel, model);
            invalidate();
        }
    }
    /**
     * @return The HelpModel that provides the events.
     */
    public TextHelpModel getModel() {
	return model;
    }

    /**
     * Sets the HelpViewerUI that provides the current look and feel.
     *
     * @param ui Provides the ComponentUI object. A null ui is valid.
     */
    public void setUI(HelpContentViewerUI ui) {
	debug("setUI");
	if ((HelpContentViewerUI)this.ui != ui) {
	    super.setUI(ui);
	    repaint();
	}
    }

    /**
     * @return The HelpViewerUI that provides the current look and feel.
     */
    public HelpContentViewerUI getUI() {
	return (HelpContentViewerUI)ui;
    }

    /**
     * Replaces the UI with the latest version from the default 
     * UIFactory.
     *
     * @overrides updateUI in class JComponent
     */
    public void updateUI() {
        SwingHelpUtilities.installUIDefaults();
	setUI((HelpContentViewerUI)UIManager.getUI(this));
        invalidate();
    }

    /**
     * @return "HelpViewerUI"
     */
    public String getUIClassID()
    {
        return "HelpContentViewerUI";
    }

    /*
     * Make sure the Look and Feel is set for the Help Component.
     */
    static {
	SwingHelpUtilities.installLookAndFeelDefaults();
    }

    /**
     * Visits a given ID.  Propagates down into the model.
     *
     * @param id The ID to visit.
     * @exception InvalidHelpSetContextException if id.hs is not contained in the
     * HelpSet of the current model.
     */
    public void setCurrentID(ID id) throws InvalidHelpSetContextException {
	model.setCurrentID(id);
    }

    /**
     * Visits a given ID.  Propagates down into the model.
     *
     * @param id The String to visit.  Relative to the HS of the current model.
     * @exception BadIDException The ID is not valid for the HelpSet for the current model.
     */
    public void setCurrentID(String id) throws BadIDException {
	try {
	    model.setCurrentID(ID.create(id, getModel().getHelpSet()));
	} catch (InvalidHelpSetContextException ex) {
	    // cannot happen
	}
    }

    /**
     * Visits a given URL.  Propagates down into the model.
     *
     * @param url The URL to visit.  Relative to the HS of the current model
     */
    public void setCurrentURL(URL url) {
	model.setCurrentURL(url);
    }

    /**
     * @return The URL currently being presented in the viewer.
     */
    public URL getCurrentURL() {
	return model.getCurrentURL();
    }

    /**
     * @return The document title.
     */

    public String getDocumentTitle() {
	return model.getDocumentTitle();
    }

    /**
     * Hightlights a section of the current document from p0 to p1.
     *
     * @param p0 Starting position.
     * @param p1 Ending position.
     */
    public void addHighlight(int p0, int p1) {
	model.addHighlight(p0,p1);
    }

    /**
     * Removes any Highlights.
     */
    public void removeAllHighlights() {
	model.removeAllHighlights();
    }

    /**
     * The local kitRegistry, indexed by mime type.
     */
    private Hashtable kitRegistry;

    /*
     * Creation of EditorKits.
     * This is similar to the registry used in JEditorPane, except:
     * <p>
     * (1) A separate registry is used so as to not interfere with other uses of JEditorPane
     * <br>
     * (2) The registry is centered around a HelpSet
     */

    /**
     * Creates a handler for the given type from the registry of editor kits.
     * If the registered class has not yet been loaded, an attempt
     * is made to dynamically load the prototype of the kit for the
     * given type.  If the type was registered with a ClassLoader,
     * that ClassLoader is used to load the prototype.  If there
     * was no registered ClassLoader, the ClassLoader for the HelpSet
     * is used to load the prototype.
     * <p>
     * Once a prototype EditorKit instance is successfully located,
     * it is cloned and the clone is returned.
     *
     * @param type the content type
     * @return the editor kit, or null if one cannot be created
     */
    public EditorKit createEditorKitForContentType(String type) {
        EditorKit k = null;
        if (kitRegistry == null) {
            // nothing has been loaded yet.
            kitRegistry = new Hashtable();
        } else {
            k = (EditorKit) kitRegistry.get(type);
        }
        if (k == null) {
            // try to dynamically load the support 
	    HelpSet hs = model.getHelpSet();
            String classname =
		(String) hs.getKeyData(HelpSet.kitTypeRegistry,
				       type);
	    // I don't know of a class for this type
	    if (classname == null) {
		return null;
	    }
	    ClassLoader loader =
		(ClassLoader) hs.getKeyData(HelpSet.kitLoaderRegistry,
					    type);
	    if (loader == null) {
		loader = hs.getLoader();
	    }
            try {
		Class c;
		if (loader != null) {
		    c = loader.loadClass(classname);
		} else {
		    c = Class.forName(classname);
		}
                k = (EditorKit) c.newInstance();
                kitRegistry.put(type, k);
            } catch (Throwable e) {
                e.printStackTrace();
                k = null;
            }
        }

        // create a copy of the prototype or null if there
        // is no prototype.
        if (k != null) {
            return (EditorKit) k.clone();
        } else {
	    // null, check the JEditorPane registry
	    k = JEditorPane.createEditorKitForContentType(type);
	}
        return k;
    }

    /**
     * Adds a listener for the TExtHelpModelEvent posted after the model has
     * changed.
     * 
     * @param l - The listener to add.
     * @see javax.help.TextHelpModel#removeHelpModelListener
     */
    public void addTextHelpModelListener(TextHelpModelListener l) {
	getModel().addTextHelpModelListener(l);
    }

    /**
     * Removes a listener previously added with <tt>addTextHelpModelListener</tt>
     *
     * @param l - The listener to remove.
     * @see javax.help.TextHelpModel#addTextHelpModelListener
     */
    public void removeHelpModelListener(TextHelpModelListener l) {
	getModel().removeTextHelpModelListener(l);
    }

    /**
     * Adds a listener for the HelpModelEvent posted after the model has
     * changed.
     * 
     * @param l - The listener to add.
     * @see javax.help.HelpModel#removeHelpModelListener
     */
    public void addHelpModelListener(HelpModelListener l) {
	getModel().addHelpModelListener(l);
    }

    /**
     * Removes a listener previously added with <tt>addHelpModelListener</tt>
     *
     * @param l - The listener to remove.
     * @see javax.help.HelpModel#addHelpModelListener
     */
    public void removeHelpModelListener(HelpModelListener l) {
	getModel().removeHelpModelListener(l);
    }

    /**
     * Debug code
     */
    private boolean debug = false;
    private void debug(String msg) {
	if (debug) {
	    System.err.println("JHelpContentViewer: "+msg);
	}
    }

/////////////////
// Accessibility support
////////////////

    /**
     * Get the AccessibleContext associated with this JComponent
     *
     * @return the AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJHelpContentViewer();
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
    protected class AccessibleJHelpContentViewer extends AccessibleJComponent {

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

