/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.deployment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.InputStream;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fedora.client.Administrator;
import fedora.client.deployment.data.BObjTemplate;
import fedora.client.deployment.data.DCElement;
import fedora.client.deployment.data.Datastream;
import fedora.client.deployment.data.Method;
import fedora.client.deployment.data.MethodParm;
import fedora.client.deployment.xml.ServiceDefinitionMETSSerializer;
import fedora.client.deployment.xml.DCGenerator;
import fedora.client.deployment.xml.MethodMapGenerator;
import fedora.client.utility.ingest.AutoIngestor;

import static fedora.common.Constants.METS_EXT1_1;

/**
 * @author Sandy Payette
 */
public class ServiceDefinitionBuilder
        extends JInternalFrame {

    private static final long serialVersionUID = 1L;

    private final JTabbedPane tabpane;

    protected BObjTemplate newSDef;

    private File s_lastDir = null;

    private String currentTabName;

    private int currentTabIndex;

    public static void main(String[] args) {
        try {
            if (args.length == 5) {
                JFrame frame = new JFrame("SDefBuilder Test");
                String protocol = args[0];
                String host = args[1];
                int port = new Integer(args[2]).intValue();
                String user = args[3];
                String pass = args[4];
                File dir = null;
                frame.addWindowListener(new WindowAdapter() {

                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });
                frame.getContentPane().add(new ServiceDefinitionBuilder(protocol,
                                                           host,
                                                           port,
                                                           user,
                                                           pass,
                                                           dir),
                                           BorderLayout.CENTER);
                frame.setSize(700, 500);
                frame.setVisible(true);
            } else {
                System.out
                        .println("SDefBuilder main method requires 5 arguments.");
                System.out
                        .println("Usage: SDefBuilder protocol host port user pass");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public ServiceDefinitionBuilder(String protocol,
                       String host,
                       int port,
                       String user,
                       String pass,
                       File dir) {
        super("Service Definition Builder");
        s_lastDir = dir;
        setClosable(true);
        setMaximizable(true);
        setSize(700, 500);
        getContentPane().setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        newSDef = new BObjTemplate();

        tabpane = new JTabbedPane();
        tabpane.setBackground(Color.GRAY);
        tabpane.addTab("General", createGeneralPane());
        tabpane.addTab("Abstract Methods", createMethodsPane());
        tabpane.addTab("Documentation", createDocPane());
        // set up listener for JTabbedPane object
        tabpane.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                currentTabIndex = tabpane.getSelectedIndex();
                currentTabName = tabpane.getTitleAt(currentTabIndex);
                //System.out.println("index = " + currentTabIndex);
                //System.out.println("tabname = " + currentTabName);
            }
        });

        // General Buttons Panel
        JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        JButton ingest = new JButton("Ingest");
        ingest.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ingest();
            }
        });
        JButton help = new JButton("Help");
        help.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        JPanel gbuttonPanel = new JPanel();
        gbuttonPanel.setBackground(Color.WHITE);
        gbuttonPanel.add(save);
        gbuttonPanel.add(ingest);
        gbuttonPanel.add(help);
        gbuttonPanel.add(cancel);

        getContentPane().add(tabpane, BorderLayout.CENTER);
        getContentPane().add(gbuttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    public BObjTemplate getBObjTemplate() {
        return newSDef;
    }

    public void save() {
        ServiceDefinitionMETSSerializer mets = savePanelInfo();
        File file = null;
        if (mets != null) {
            JFileChooser chooser = new JFileChooser(s_lastDir);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            XMLFileChooserFilter filter = new XMLFileChooserFilter();
            chooser.setFileFilter(filter);
            if (chooser.showSaveDialog(tabpane) == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
                s_lastDir = file.getParentFile(); // remember the dir for next time
                String ext = filter.getExtension(file);
                if (ext == null || !ext.equalsIgnoreCase("xml")) {
                    file = new File((file.getPath() + ".xml"));
                }
                try {
                    mets.writeMETSFile(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    assertTabPaneMsg(("SDefBuilder: Error saving METS file for sdef: " + e
                                             .getMessage()),
                                     "SDefBuilder");
                }
            } else {
                assertTabPaneMsg("SDefBuilder: You did not specify a file to Save.",
                                 "SDefBuilder");
            }
        }
    }

    public void ingest() {
        InputStream in = null;
        String pid = null;
        ServiceDefinitionMETSSerializer mets = savePanelInfo();
        if (mets != null) {
            try {
                in = mets.writeMETSStream();
            } catch (Exception e) {
                e.printStackTrace();
                assertTabPaneMsg(("SDefBuilder: Error saving METS to stream for sdef: " + e
                                         .getMessage()),
                                 "SDefBuilder");
            }
            try {
                AutoIngestor ingestor =
                        new AutoIngestor(Administrator.APIA, Administrator.APIM);
                pid =
                        ingestor
                                .ingestAndCommit(in,
                                                 METS_EXT1_1.uri,
                                                 "ingest sdef object via SDefBuilder tool");
            } catch (Exception e) {
                e.printStackTrace();
                assertTabPaneMsg(("SDefBuilder: error ingesting sdef object: " + e
                                         .getMessage()),
                                 null);
            }
            assertTabPaneMsg(("New PID = " + pid), "Successful Ingest");
        }
    }

    public void showHelp() {
        if (currentTabIndex == 0) {
            showGeneralHelp();
        } else if (currentTabIndex == 1) {
            showMethodsHelp();
        } else if (currentTabIndex == 2) {
            showDocumentsHelp();
        }
    }

    public void cancel() {
        setVisible(false);
        dispose();
    }

    public ServiceDefinitionMETSSerializer savePanelInfo() {

        Component[] tabs = tabpane.getComponents();
        //System.out.println("tabs count: " + tabs.length);
        for (Component element : tabs) {
            //System.out.println("tab name: " + tabs[i].getName());
            if (element.getName().equalsIgnoreCase("GeneralTab")) {
                if (validGeneralTab((GeneralPane) element)) {
                    GeneralPane gp = (GeneralPane) element;
                    if (gp.rb_chosen.equalsIgnoreCase("retainPID")) {
                        newSDef.setbObjPID(gp.getBObjectPID());
                    } else {
                        newSDef.setbObjPID(null);
                    }
                    newSDef.setbObjLabel(gp.getBObjectLabel());
                    newSDef.setbObjName(gp.getBObjectName());
                    newSDef.setDCRecord(gp.getDCElements());
                } else {
                    return null;
                }
            } else if (element.getName().equalsIgnoreCase("MethodsTab")) {
                if (validMethodsTab((MethodsPane) element)) {
                    MethodsPane mp = (MethodsPane) element;
                    newSDef.setMethodsHashMap(mp.getMethodMap());
                    newSDef.setMethods(mp.getMethods());
                } else {
                    return null;
                }
            } else if (element.getName().equalsIgnoreCase("DocumentsTab")) {
                if (validDocsTab((DocumentsPane) element)) {
                    DocumentsPane docp = (DocumentsPane) element;
                    newSDef.setDocDatastreams(docp.getDocDatastreams());
                } else {
                    return null;
                }
            }
        }
        DCGenerator dcg = null;
        MethodMapGenerator mmg = null;
        try {
            dcg = new DCGenerator(newSDef);
            //dcg.printDC();
        } catch (Exception e) {
            e.printStackTrace();
            assertTabPaneMsg("SDefBuilder: error generating dc record.", null);
        }
        try {
            mmg = new MethodMapGenerator(newSDef);
            //mmg.printMethodMap();
        } catch (Exception e) {
            e.printStackTrace();
            assertTabPaneMsg("SDefBuilder: error generating method map.", null);
        }
        ServiceDefinitionMETSSerializer mets = null;
        try {
            mets =
                    new ServiceDefinitionMETSSerializer(newSDef, dcg.getRootElement(), mmg
                            .getRootElement());
        } catch (Exception e) {
            e.printStackTrace();
            assertTabPaneMsg("SDefBuilder: error in creating METS for sdef.",
                             null);
        }
        //mets.printMETS();
        return mets;
    }

    private JComponent createGeneralPane() {
        GeneralPane gpane = new GeneralPane(this);
        gpane.setName("GeneralTab");
        return gpane;
        //return new JLabel("Insert general stuff here.");
    }

    private JComponent createMethodsPane() {
        MethodsPane mpane = new MethodsPane(this);
        mpane.setName("MethodsTab");
        return mpane;
    }

    private JComponent createDocPane() {
        DocumentsPane docpane = new DocumentsPane();
        docpane.setName("DocumentsTab");
        return docpane;
    }

    private void print() {
        System.out.println("FROM GENERAL TAB===============================");
        System.out.println("sDefPID: " + newSDef.getbObjPID());
        System.out.println("sDefLabel: " + newSDef.getbObjLabel());
        System.out.println("DCRecord: ");
        DCElement[] dcrecord = newSDef.getDCRecord();
        for (DCElement element : dcrecord) {
            System.out.println(">>> " + element.elementName + "="
                    + element.elementValue);
        }
        System.out.println("FROM METHODS TAB===============================");
        System.out.println("methods: ");
        HashMap m2 = newSDef.getMethodsHashMap();
        Collection methods = m2.values();
        Iterator it_methods = methods.iterator();
        while (it_methods.hasNext()) {
            Method method = (Method) it_methods.next();
            System.out.println("  method name: " + method.methodName + "\n"
                    + "  method desc: " + method.methodLabel + "\n"
                    + "  method URL: " + method.methodProperties.methodFullURL
                    + "\n" + "  method protocol"
                    + method.methodProperties.protocolType + "\n");
            System.out.println("  method parms:");
            int parmcnt = method.methodProperties.methodParms.length;
            for (int i = 0; i < parmcnt; i++) {
                MethodParm mp = method.methodProperties.methodParms[i];
                System.out.println(">>>parmName: " + mp.parmName + "\n"
                        + ">>>parmType: " + mp.parmType + "\n"
                        + ">>>parmLabel: " + mp.parmLabel + "\n"
                        + ">>>parmDefaultValue: " + mp.parmDefaultValue + "\n"
                        + ">>>parmPassBy: " + mp.parmPassBy + "\n"
                        + ">>>parmRequired: " + mp.parmRequired + "\n"
                        + ">>>parmDomainValues: " + mp.parmDomainValues + "\n");
            }
        }
    }

    private boolean validGeneralTab(GeneralPane gp) {
        if (gp.rb_chosen.equalsIgnoreCase("retainPID")
                && (gp.getBObjectPID() == null || gp.getBObjectPID().trim()
                        .equals(""))) {
            assertTabPaneMsg("The test PID value is missing on General Tab.",
                             gp.getName());
            return false;
        } else if (gp.getBObjectLabel() == null
                || gp.getBObjectLabel().trim().equals("")) {
            assertTabPaneMsg("Service Deployment Label is missing on General Tab.",
                             gp.getName());
            return false;
        } else if (gp.getBObjectName() == null
                || gp.getBObjectName().trim().equals("")) {
            assertTabPaneMsg("Service Deployment Nickname is missing on General Tab.",
                             gp.getName());
            return false;
        } else if (gp.getDCElements().length <= 0) {
            assertTabPaneMsg("You must enter at least one DC element on General Tab.",
                             gp.getName());
            return false;
        }
        return true;
    }

    private boolean validMethodsTab(MethodsPane mp) {
        if (mp.getMethods().length <= 0) {
            assertTabPaneMsg("You must enter at least one method on AbstractMethods Tab.",
                             mp.getName());
            return false;
        } else {
            return true;
        }
    }

    private boolean validDocsTab(DocumentsPane docp) {
        Datastream[] docs = docp.getDocDatastreams();
        if (docs.length < 1) {
            assertTabPaneMsg(new String("You must enter at least one document"
                                     + " that describes the service definition in the Documents Tab."),
                             docp.getName());
            return false;
        }

        for (Datastream element : docs) {
            if (element.dsLabel == null) {
                assertTabPaneMsg(new String("You must enter a Label for all documents"
                                         + "listed on the Documents Tab."),
                                 docp.getName());
                return false;
            } else if (element.dsMIMEType == null) {
                assertTabPaneMsg(new String("You must enter a MIME type for all documents"
                                         + "listed on the Documents Tab."),
                                 docp.getName());
                return false;
            }
        }
        return true;
    }

    private void showGeneralHelp() {
        JTextArea helptxt = new JTextArea();
        helptxt.setLineWrap(true);
        helptxt.setWrapStyleWord(true);
        helptxt.setBounds(0, 0, 550, 20);
        helptxt
                .append("There are two sections to the General Tab that"
                        + " must be completed:\n\n"
                        + " Object Description:\n"
                        + " >>> Service Object PID: either select the button for the"
                        + " repository system to generate one, or enter your own"
                        + " with the prefix 'test:' or 'demo:'\n\n"
                        + " >>> Service Object Name:  enter a single word to name the object."
                        + " This name is used in various places within inline metadata that"
                        + " is generated by the tool.\n\n"
                        + " >>> Service Object Label: enter a meaningful label for theobject.\n\n"
                        + " \n" + " Dublin Core Metadata:\n"
                        + ">>> Enter at least one DC element to describe"
                        + " the Service Definition Object.");

        JOptionPane.showMessageDialog(this,
                                      helptxt,
                                      "Help for General Tab",
                                      JOptionPane.OK_OPTION);
    }

    private void showMethodsHelp() {
        JTextArea helptxt = new JTextArea();
        helptxt.setLineWrap(true);
        helptxt.setWrapStyleWord(true);
        helptxt.setBounds(0, 0, 550, 20);
        helptxt
                .append("The Methods Tab is used to define a 'Service contract'"
                        + " which is a set of abstract method definitions.  Define the method names and"
                        + " any user-supplied parameters to those methods.  Later you will create"
                        + " one or more Service Deployment Objects that define concrete service"
                        + " bindings to fulfill these methods.  Together the 'Service contract'"
                        + " of the Service Definition Object, and the service bindings of a"
                        + " Service Deployment Object will be used to create Disseminators on"
                        + " Fedora Data Objects.");

        JOptionPane.showMessageDialog(this,
                                      helptxt,
                                      "Help for Abstract Methods Tab",
                                      JOptionPane.OK_OPTION);
    }

    private void showDocumentsHelp() {
        JTextArea helptxt = new JTextArea();
        helptxt.setLineWrap(true);
        helptxt.setWrapStyleWord(true);
        helptxt.setBounds(0, 0, 550, 20);
        helptxt.append("insert documents help\n\n");
        helptxt.append("\n\n");
        helptxt.append("\n\n");

        JOptionPane.showMessageDialog(this,
                                      helptxt,
                                      "Help for Documents Tab",
                                      JOptionPane.OK_OPTION);
    }

    private void assertTabPaneMsg(String msg, String tabpane) {
        JOptionPane.showMessageDialog(this, new String(msg), new String(tabpane
                + " Message"), JOptionPane.INFORMATION_MESSAGE);
    }
}