package fedora.client.console.management;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

import fedora.client.Administrator;
import fedora.client.console.ConsoleSendButtonListener;
import fedora.client.console.ConsoleCommand;
import fedora.client.console.ServiceConsoleCommandFactory;
import fedora.server.management.FedoraAPIMServiceLocator;
import fedora.server.management.FedoraAPIM;

public class ManagementConsole
        extends JInternalFrame {
        
    private Administrator m_mainFrame;
    private static FedoraAPIMServiceLocator m_locator=new FedoraAPIMServiceLocator();
    
    public ManagementConsole(Administrator mainFrame) {
        super("Management Console",
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable
              
        m_mainFrame=mainFrame;
        
        
       JTextArea outputArea = new JTextArea(
    "This is an editable JTextArea " +
    "that has been initialized with the setText method. " +
    "A text area is a \"plain\" text component, " +
    "which means that although it can display text " +
    "in any font, all of the text is in the same font."
);
outputArea.setFont(new Font("Serif", Font.PLAIN, 12));
outputArea.setEditable(false);

JScrollPane outputScrollPane = new JScrollPane(outputArea);
outputScrollPane.setVerticalScrollBarPolicy(
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
outputScrollPane.setPreferredSize(new Dimension(250, 250));
outputScrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,0,0));
// outputScrollPane.setBorder(...create border...);

        JPanel controlPanel=new JPanel(); 
        controlPanel.setLayout(new BorderLayout());
        JPanel hostPortPanel=new JPanel();
        
        hostPortPanel.setLayout(new BorderLayout());
        JPanel hostPanel=new JPanel();
        hostPanel.setLayout(new BorderLayout());
        hostPanel.add(new JLabel("Host : "), BorderLayout.WEST);
        hostPanel.add(new JTextField("localhost", 13), BorderLayout.EAST);
        
        JPanel portPanel=new JPanel();
        portPanel.setLayout(new BorderLayout());
        portPanel.add(new JLabel("  Port : "), BorderLayout.WEST);
        portPanel.add(new JTextField("8080", 4), BorderLayout.EAST);
       
        hostPortPanel.add(hostPanel, BorderLayout.WEST);
        hostPortPanel.add(portPanel, BorderLayout.EAST);
        
        JPanel commandPanel=new JPanel();
        commandPanel.setLayout(new BorderLayout());
        commandPanel.add(new JLabel("  Command : "), BorderLayout.WEST);
        
/**
        String[] commands=new String[] {
                "String createObject()",
                "String ingestObject(byte[] METSXML)",
                "byte[] getObjectXML(String PID)",
                "byte[] exportObject(String PID)",
                "void withdrawObject(String PID)",
                "void deleteObject(String PID)",
                "void purgeObject(String PID)",
                "void obtainLock(String PID)",
                "void releaseLock(String PID, String logMessage, boolean commit)",
                "String getLockingUser(String PID)",
                "String getObjectState(String PID)",
                "Calendar getObjectCreateDate(String PID)",
                "Calendar getObjectLastModDate(String PID)",
                "AuditRecord[] getObjectAuditTrail(String PID)",
                "String[] listObjectPIDs(String state)",
                "String addDatastreamExternal(String PID, String dsLabel, String dsLocation)",
                "String addDatastreamManagedContent(String PID, String dsLabel, String MIMEType, byte[] dsContent)",
                "String addDatastreamXMLMetadata(String PID, String dsLabel, String MDType, byte[] dsInlineMetadata)",
                "String addDisseminator(String PID, String bMechPID, String dissLabel, .DatastreamBindingMap bindingMap)",
                "void modifyDisseminator(String PID, String disseminatorID, String bMechPID, String dissLabel, .DatastreamBindingMap bindingMap)",
                "void modifyDatastreamExternal(String PID, String datastreamID, String dsLabel, String dsLocation)",
                "void modifyDatastreamManagedContent(String PID, String datastreamID, String dsLabel, String MIMEType, byte[] dsContent)",
                "void modifyDatastreamXMLMetadata(String PID, String datastreamID, String dsLabel, String MDType, byte[] dsInlineMetadata)",
                "void withdrawDatastream(String PID, String datastreamID)",
                "void deleteDatastream(String PID, String datastreamID)",
                "Calendar[] purgeDatastream(String PID, String datastreamID, Calendar startDT, java.util.Calendar endDT)",
                "void withdrawDisseminator(String PID, String disseminatorID)",
                "void deleteDisseminator(String PID, String disseminatorID)",
                "Calendar[] purgeDisseminator(String PID, String disseminatorID, Calendar startDT, Calendar endDT)",
                "Datastream getDatastream(String PID, String datastreamID, Calendar asOfDateTime)",
                "Datastream[] getDatastreams(String PID, Calendar asOfDateTime)",
                "String[] listDatastreamIDs(String PID, String state)",
                "ComponentInfo[] getDatastreamHistory(String PID, String datastreamID)",
                "Disseminator getDisseminator(String PID, String disseminatorID, Calendar asOfDateTime)",
                "Disseminator[] getDisseminators(String PID, Calendar asOfDateTime)",
                "String[] listDisseminatorIDs(String PID, String state)",
                "ComponentInfo[] getDisseminatorHistory(String PID, String disseminatorID)"
        };
*/
        ConsoleCommand[] commands=null;
        try {
            commands=
                    ServiceConsoleCommandFactory.getConsoleCommands(
                    Class.forName("fedora.server.management.FedoraAPIM"), null);
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Could not find server management interface, FedoraAPIM.");
            System.exit(0);
        }

        
        JComboBox commandComboBox=new JComboBox(commands);
        commandComboBox.setSelectedIndex(0);
        commandPanel.add(commandComboBox);
        JButton sendButton=new JButton(" Send.. ");
        sendButton.addActionListener(new ConsoleSendButtonListener(commandComboBox.getModel(), m_mainFrame));


        commandPanel.add(sendButton, BorderLayout.EAST);
        
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        controlPanel.add(hostPortPanel, BorderLayout.WEST);
        controlPanel.add(commandPanel);
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(controlPanel, BorderLayout.NORTH);
        getContentPane().add(outputScrollPane);

        setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/standard/development/Host16.gif")));

        setSize(400,400);
    }
    
}
