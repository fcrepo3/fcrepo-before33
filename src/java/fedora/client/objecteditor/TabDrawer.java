package fedora.client.objecteditor;

public interface TabDrawer {

    public void setDirty(String id, boolean isDirty);
    public void colorTabForState(String id, String state);

}