package fedora.client.batch.types;

/**
 * Launch a dialog for entering information for a new object
 * (title, content model, and possibly a specified pid),
 * then create the object on the server and launch an editor on it.
 *
 * @author rlw@virginia.edu
 * @version $Id $
 */
public class DigitalObject {

    public String pid;
    public String label;
    public String cModel;
    public boolean force = false;
    public String state;
    public String logMessage;

    public DigitalObject()
    {}

}
