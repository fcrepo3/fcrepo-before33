package fedora.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Security;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.axis.AxisFault;
import org.apache.axis.AxisProperties;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fedora.client.Administrator;
import fedora.client.APIAStubFactory;

import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.RepositoryInfo;
import fedora.server.types.gen.UserInfo;

/**
 * Launch a dialog for logging into a Fedora repository.
 *
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */

public class LoginDialog
        extends JDialog {

    private JComboBox m_serverComboBox;
	private JComboBox m_protocolComboBox;
    private JComboBox m_usernameComboBox;
    private JPasswordField m_passwordField;

    private String m_lastUsername="fedoraAdmin";
    private String m_lastServer="localhost:8080";
	private String m_lastProtocol="http";
    private HashMap m_usernames;
    private HashMap m_servers;
	private HashMap m_protocols;
    
    public LoginDialog() {
        super(JOptionPane.getFrameForComponent(Administrator.getDesktop()), "Login", true);
        
        System.out.println("entered login diaglog");

        m_servers=new HashMap();
        m_protocols=new HashMap();
        m_protocols.put("http", "");
        m_protocols.put("https", "");
        m_usernames=new HashMap();

        JLabel serverLabel=new JLabel("Fedora Server");
		JLabel protocolLabel=new JLabel("Protocol");
        JLabel usernameLabel=new JLabel("Username");
        JLabel passwordLabel=new JLabel("Password");

        m_serverComboBox=new JComboBox();
        m_serverComboBox.setEditable(true);
		m_protocolComboBox=new JComboBox();
		m_protocolComboBox.setEditable(true);
        m_usernameComboBox=new JComboBox();
        m_usernameComboBox.setEditable(true);
        m_passwordField=new JPasswordField();

        setComboBoxValues();

        LoginAction loginAction=new LoginAction(this);
        JButton loginButton=new JButton(loginAction);
        loginAction.setButton(loginButton);
        loginButton.setEnabled(false);
        m_passwordField.getDocument().addDocumentListener(
                new PasswordChangeListener(loginButton, m_passwordField));
        m_passwordField.setAction(loginAction);

        JPanel inputPane=new JPanel();
        inputPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(6, 6, 6, 6),
                    BorderFactory.createEtchedBorder()
                ),
                BorderFactory.createEmptyBorder(6,6,6,6)
                ));
        GridBagLayout gridBag=new GridBagLayout();
        inputPane.setLayout(gridBag);
        addLabelValueRows(new JLabel[] {serverLabel, protocolLabel, usernameLabel, passwordLabel}, 
                new JComponent[] {m_serverComboBox, m_protocolComboBox, m_usernameComboBox, m_passwordField}, 
                gridBag, inputPane);

        JButton cancelButton=new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                dispose();
            }
        });
        if (Administrator.APIA==null) {
            cancelButton.setText("Exit"); // if haven't logged in yet
        } else {
            cancelButton.setText("Cancel");
        }
        JPanel buttonPane=new JPanel();
        buttonPane.add(loginButton);
        buttonPane.add(cancelButton);
        Container contentPane=getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(inputPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.SOUTH);
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent evt) {
                m_passwordField.requestFocus();
            }
        });
        pack();
        setLocation(Administrator.INSTANCE.getCenteredPos(getWidth(), getHeight()));
        setVisible(true);
    }

    // re-writes fedora-admin.properties with latest values for servers 
    // and usernames
    public void saveProperties() {
        try {
            Properties props=new Properties();
            props.setProperty("lastServer", m_lastServer);
			props.setProperty("lastProtocol", m_lastProtocol);
            props.setProperty("lastUsername", m_lastUsername);
            Iterator iter;
            int i;
            iter=m_servers.keySet().iterator();
            i=0;
            while (iter.hasNext()) {
                String name=(String) iter.next();
                props.setProperty("server" + i, name);
                i++;
            }
			iter=m_protocols.keySet().iterator();
			i=0;
			while (iter.hasNext()) {
				String name=(String) iter.next();
				props.setProperty("protocol" + i, name);
				i++;
			}
            iter=m_usernames.keySet().iterator();
            i=0;
            while (iter.hasNext()) {
                String name=(String) iter.next();
                props.setProperty("username" + i, name);
                i++;
            }
            props.store(new FileOutputStream(new File(Administrator.BASE_DIR, "fedora-admin.properties")), "Fedora Administrator saved settings");
        } catch (Exception e) {
            System.err.println("Warning: Error writing properties: " 
                    + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private void setComboBoxValues() {
        // get values from prop file, or use localhost:8080/fedoraAdmin if none
        try {
            Properties props=new Properties();
            props.load(new FileInputStream(new File(Administrator.BASE_DIR, "fedora-admin.properties")));
            Enumeration names=props.propertyNames();
            while (names.hasMoreElements()) {
                String prop=(String) names.nextElement();
                if (prop.equals("lastServer")) {
                    m_lastServer=props.getProperty(prop);
				} else if (prop.equals("lastProtocol")) {
					m_lastProtocol=props.getProperty(prop);
                } else if (prop.equals("lastUsername")) {
                    m_lastUsername=props.getProperty(prop);
                } else if (prop.startsWith("server")) {
                    m_servers.put(props.getProperty(prop), "");
				} else if (prop.startsWith("protocol")) {
					m_protocols.put(props.getProperty(prop), "");
                } else if (prop.startsWith("username")) {
                    m_usernames.put(props.getProperty(prop), "");
                }
            }
        } catch (Exception e) {
            // no problem if props file doesn't exist...we have defaults
        }
        // finally, populate them
        m_serverComboBox.addItem(m_lastServer);
        Iterator sIter=m_servers.keySet().iterator();
        while (sIter.hasNext()) {
            String a=(String) sIter.next();
            if (!a.equals(m_lastServer)) {
                m_serverComboBox.addItem(a);
            }
        }
        m_servers.put(m_lastServer, "");
        
		m_protocolComboBox.addItem(m_lastProtocol);
		Iterator protocolIter=m_protocols.keySet().iterator();
		while (protocolIter.hasNext()) {
			String a=(String) protocolIter.next();
			if (!a.equals(m_lastProtocol)) {
				m_protocolComboBox.addItem(a);
			}
		}
		m_protocols.put(m_lastProtocol, "");

        m_usernameComboBox.addItem(m_lastUsername);
        Iterator uIter=m_usernames.keySet().iterator();
        while (uIter.hasNext()) {
            String a=(String) uIter.next();
            if (!a.equals(m_lastUsername)) {
                m_usernameComboBox.addItem(a);
            }
        }
        m_usernames.put(m_lastUsername, "");

        // make all entry widgets same size
        Dimension newSize=new Dimension(
                m_serverComboBox.getPreferredSize().width+20,
                m_serverComboBox.getPreferredSize().height);
        m_serverComboBox.setPreferredSize(newSize);
		m_protocolComboBox.setPreferredSize(newSize);
        m_usernameComboBox.setPreferredSize(newSize);
        m_passwordField.setPreferredSize(newSize);
    }

    public void addLabelValueRows(JLabel[] labels, JComponent[] values,
            GridBagLayout gridBag, Container container) {
        GridBagConstraints c=new GridBagConstraints();
        c.insets=new Insets(0, 6, 6, 6);
        for (int i=0; i<labels.length; i++) {
            c.anchor=GridBagConstraints.EAST;
            c.gridwidth=GridBagConstraints.RELATIVE; //next-to-last
            c.fill=GridBagConstraints.NONE;      //reset to default
            c.weightx=0.0;                       //reset to default
            gridBag.setConstraints(labels[i], c);
            container.add(labels[i]);

            c.gridwidth=GridBagConstraints.REMAINDER;     //end row
            if (!(values[i] instanceof JComboBox)) {
                c.fill=GridBagConstraints.HORIZONTAL;
            } else {
                c.anchor=GridBagConstraints.WEST;
            }
            c.weightx=1.0;
            gridBag.setConstraints(values[i], c);
            container.add(values[i]);
        }

    }

        // sets Administrator.APIA/M if success, throws Exception if fails.
        public static void tryLogin(String protocol, String host, int port, String user, String pass) 
                throws Exception {
            Administrator.APIA=APIAStubFactory.getStub(protocol, host, port, user, pass);
            Administrator.APIM=APIMStubFactory.getStub(protocol, host, port, user, pass);
           /*
            Enumeration enum = AxisProperties.propertyNames();
        	System.err.println("1 are there any AxisProperties?=" + enum.hasMoreElements());
            while(enum.hasMoreElements()) {
            	String name = (String) enum.nextElement();
            	System.err.println("another axis property = " + name);
            }
            
            AxisProperties.setProperty("axis.socketSecureFactory", 
    		"org.apache.axis.components.net.SunFakeTrustSocketFactory");  
            enum = AxisProperties.propertyNames();
        	System.err.println("2 are there any AxisProperties?=" + enum.hasMoreElements());
            while(enum.hasMoreElements()) {
            	String name = (String) enum.nextElement();
            	System.err.println("another axis property = " + name);
            }
		*/  
            RepositoryInfo info=Administrator.APIA.describeRepository();
            if (!info.getRepositoryVersion().equals(Administrator.VERSION)) {
                throw new IOException("Server is version "
                        + info.getRepositoryVersion() + ", but this"
                        + " client only works with version" +  Administrator.VERSION);
            }
/*
            System.setProperty("axis.socketSecureFactory", 
            		"org.apache.axis.components.net.SunFakeTrustSocketFactory");  
            		"axis.socketSecureFactory" system property to "org.apache.axis.components.net.FakeTrustSocketFactory"          
          
            System.setProperty("javax.net.ssl.trustStore","c:\\j2sdk1.4.2_03\\jre\\lib\\security\\cacerts");
            System.setProperty("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");       
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());   
*/
            // do a simple API-M call, and if it doesn't come back
            // unauthorized, assume all is ok.
            try {
                UserInfo inf=Administrator.APIM.describeUser(user);
            } catch (Exception e) {
                if (e.getMessage().indexOf("Unauthorized")!=-1 || e.getMessage().indexOf("Unrecognized")!=-1) {
                    throw new IOException("Bad username or password.");
                }
            }
        }

    public class PasswordChangeListener
            implements DocumentListener {

        private JButton m_loginButton;
        private JPasswordField m_passField;

        public PasswordChangeListener(JButton loginButton, JPasswordField pf) {
            m_loginButton=loginButton;
            m_passField=pf;
        }

        public void changedUpdate(DocumentEvent e) {
            dataChanged();
        }

        public void insertUpdate(DocumentEvent e) {
            dataChanged();
        }

        public void removeUpdate(DocumentEvent e) {
            dataChanged();
        }

        public void dataChanged() {
        	if (m_passField.getPassword().length == 0) { 
                m_loginButton.setEnabled(false);
            } else {
                m_loginButton.setEnabled(true);
            }
        }

    }


    public class LoginAction
            extends AbstractAction {

        LoginDialog m_loginDialog;
        JButton m_button;

        public LoginAction(LoginDialog loginDialog) {
            super("Login");
            m_loginDialog=loginDialog;
        }

        public void setButton(JButton button) {
            m_button=button;
        }

        public void actionPerformed(ActionEvent evt) {
            if (m_button.isEnabled()) {
                FedoraAPIA oldAPIA=Administrator.APIA;
                FedoraAPIM oldAPIM=Administrator.APIM;
                try {
                    // pull out values and do a quick syntax check
                    String hostPort=(String) m_serverComboBox.getSelectedItem();
                    int colonPos=hostPort.indexOf(":");
                    if (colonPos==-1) {
                        throw new IOException("Server must be specified as host:port");
                    }
                    String[] s=hostPort.split(":");
                    String host=s[0];
                    if (host.length()==0) {
                        throw new IOException("No server name provided.");
                    }
                    int port=0;
                    try {
                        port=Integer.parseInt(s[1]);
                    } catch (NumberFormatException nfe) {
                        throw new IOException("Server port must be an integer.");
                    }
					String protocol=(String) m_protocolComboBox.getSelectedItem();
					if (protocol.equals("")) {
						throw new IOException("No protocol provided.");
					}
                    String username=(String) m_usernameComboBox.getSelectedItem();
                    if (username.equals("")) {
                        throw new IOException("No username provided.");
                    }
                    String pass = new String(m_passwordField.getPassword());
                    
                    tryLogin(protocol, host, port, username, pass);
                    // all looks ok...just save stuff and exit now
                    m_lastServer=host + ":" + port;
                    m_lastProtocol=protocol;
                    m_lastUsername=username;
                    m_loginDialog.saveProperties();
                    Administrator.INSTANCE.setLoginInfo(protocol, host, port, username, pass);
                    m_loginDialog.dispose();
                } catch (Exception e) {
                	Administrator.showErrorDialog(m_loginDialog, "Login Error", e.getMessage(), e);           	
                    Administrator.APIA=oldAPIA;
                    Administrator.APIM=oldAPIM;
                }
            }
        }
    }

}