package fedora.client.objecteditor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import fedora.client.Administrator;

import fedora.server.types.gen.Datastream;
import fedora.server.types.gen.Disseminator;

public class DisseminatorPane
        extends EditingPane {

    public DisseminatorPane(ObjectEditorFrame gramps, String pid, 
            Disseminator[] versions, DisseminatorsPane owner)
            throws Exception {
        super(gramps, owner, versions[0].getID());
    }

    public boolean isDirty() {
        return false;
    }

    public void saveChanges(String logMessage) throws Exception {
    }

    /**
     * Called when changes to the server succeeded.
     * This method can do anything, but it should at least ensure that the
     * model and view are in-sync with each other (accurately reflecting the
     * current state of the server).
     */
    public void changesSaved() throws Exception {
    }

    /**
     * Revert to original values, then call updateButtonVisibility.
     */
    public void undoChanges() {
    }


}
