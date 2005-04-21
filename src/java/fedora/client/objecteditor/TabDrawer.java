package fedora.client.objecteditor;

/**
 * A thing that draws tabs.
 * 
 */
public interface TabDrawer {

    public void setDirty(String id, boolean isDirty);
    public void colorTabForState(String id, String state);

}