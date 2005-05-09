package fedora.client.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import fedora.client.Administrator;
import fedora.client.LoginDialog;

/**
 * Action for launching the login window.
 *
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class Login
        extends AbstractAction {

    public Login() {
        super("Change Repository...");
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            LoginDialog ld=new LoginDialog();
        } catch (Exception e) {
        	Administrator.showErrorDialog(Administrator.getDesktop(), "Login Error", 
        			e.getClass().getName() + ": " + e.getMessage(), e);
        }
    }

}