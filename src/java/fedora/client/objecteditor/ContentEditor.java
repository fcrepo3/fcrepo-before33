package fedora.client.objecteditor;

import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Edit content of certain types in a JComponent.
 */
public abstract class ContentEditor
        extends ContentViewer {

    /**
     * Always returns true.
     */
    public final boolean isEditor() {
        return true;
    }

    /**
     * Called when the caller wants what is in the view to be
     * considered "not dirty" because it's been saved that way.
     */
    public abstract void changesSaved();

    /**
     * Called when the caller wants to update the view back to the data
     * was originally passed in.
     */
    public abstract void undoChanges();

    /**
     * Returns true if the content should be considered "dirty" (e.g. it has
     * changed due to some form of editing).
     */
    public abstract boolean isDirty();

    /**
     * Sets the listener that this ContentEditor will notify via 
     * listener.actionPerformed(...) when any content-changing events occur
     * that could potentially affect its "dirty state" (whether going from not
     * dirty to dirty, or dirty to not dirty).
     */
    public abstract void setContentChangeListener(ActionListener listener); 

    /**
     * Gets the content in its edited state.
     */
    public abstract InputStream getContent();

}