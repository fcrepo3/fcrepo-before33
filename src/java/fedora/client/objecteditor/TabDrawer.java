/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.objecteditor;

/**
 * A thing that draws tabs.
 * 
 */
public interface TabDrawer {

    public void setDirty(String id, boolean isDirty);
    public void colorTabForState(String id, String state);

}