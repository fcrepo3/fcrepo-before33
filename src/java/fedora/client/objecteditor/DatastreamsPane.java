package fedora.client.objecteditor;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fedora.client.Administrator;

import fedora.server.types.gen.Datastream;

/**
 * Shows a tabbed pane, one for each datastream in the object.
 * Also show add and purge buttons at the bottom.
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class DatastreamsPane
        extends JPanel
        implements PotentiallyDirty {

    private String m_pid;
    private JTabbedPane m_tabbedPane;
    private DatastreamPane[] m_datastreamPanes;

    public String[] ALL_KNOWN_MIMETYPES = new String[] {"text/xml", "text/xml",
            "text/plain", "text/html", "text/html+xml", "text/svg+xml",
            "image/jpeg", "image/gif", "image/bmp", "application/postscript",
            "application/ms-word", "application/pdf", "application/zip"};

    /**
     * Build the pane.
     */
    public DatastreamsPane(String pid)
            throws Exception {
        m_pid=pid;

        // this(m_tabbedPane)

            // m_tabbedPane(DatastreamPane[])

            m_tabbedPane=new JTabbedPane(SwingConstants.LEFT);
            Datastream[] currentVersions=Administrator.APIM.
                    getDatastreams(pid, null, null);
            m_datastreamPanes=new DatastreamPane[currentVersions.length];
            for (int i=0; i<currentVersions.length; i++) {
                m_datastreamPanes[i]=new DatastreamPane(
                        pid, 
                        Administrator.APIM.getDatastreamHistory(
                                pid,
                                currentVersions[i].getID()),
                        this);
                m_tabbedPane.add(currentVersions[i].getID(), m_datastreamPanes[i]);
            }
            m_tabbedPane.add("New...", new JPanel());

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(6,6,0,6));
        add(m_tabbedPane, BorderLayout.CENTER);

        doNew(ALL_KNOWN_MIMETYPES, false);
    }

    /**
     * Set the content of the "New..." JPanel to a fresh new datastream
     * entry panel, and switch to it, if needed.
     */
    public void doNew(String[] dropdownMimeTypes, boolean makeSelected) {
        int i=m_tabbedPane.indexOfTab("New...");
        m_tabbedPane.setComponentAt(i, new NewDatastreamPane(dropdownMimeTypes));
        if (makeSelected) {
            m_tabbedPane.setSelectedIndex(i);
        }
    }

    /**
     * Refresh the content of the tab for the indicated datastream with the
     * latest information from the server.
     */
    protected void refresh(String dsID) {
        int i=m_tabbedPane.indexOfTab(dsID);
        try {
            Datastream[] versions=Administrator.APIM.getDatastreamHistory(m_pid, dsID);
            m_tabbedPane.setComponentAt(i, new DatastreamPane(m_pid, versions, this));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Administrator.getDesktop(),
                    e.getMessage() + "\nTry re-opening the object viewer.", 
                    "Error while refreshing",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Add a new tab with a new datastream.
     */
    protected void addDatastreamTab(String dsID) throws Exception {
        DatastreamPane[] newArray=new DatastreamPane[m_datastreamPanes.length+1];
        for (int i=0; i<m_datastreamPanes.length; i++) {
            newArray[i]=m_datastreamPanes[i];
        }
        newArray[m_datastreamPanes.length]=new DatastreamPane(
                        m_pid, 
                        Administrator.APIM.getDatastreamHistory(m_pid, dsID),
                        this);
        // swap the arrays
        m_datastreamPanes=newArray;
        int newIndex=m_tabbedPane.indexOfTab("New...");
        m_tabbedPane.add(m_datastreamPanes[m_datastreamPanes.length-1], newIndex);
        m_tabbedPane.setTitleAt(newIndex, dsID);
        m_tabbedPane.setSelectedIndex(newIndex);
        doNew(ALL_KNOWN_MIMETYPES, false);
    }

    protected void remove(String dsID) {
        int i=m_tabbedPane.indexOfTab(dsID);
        m_tabbedPane.remove(i);
    }

    public boolean isDirty() {
        for (int i=0; i<m_datastreamPanes.length; i++) {
            if (m_datastreamPanes[i].isDirty()) return true;
        }
        return false;
    }

    public void addRows(JComponent[] left, JComponent[] right,
            GridBagLayout gridBag, Container container) {
        GridBagConstraints c=new GridBagConstraints();
        c.insets=new Insets(0, 6, 6, 6);
        for (int i=0; i<left.length; i++) {
            c.anchor=GridBagConstraints.NORTHEAST;
            c.gridwidth=GridBagConstraints.RELATIVE; //next-to-last
            c.fill=GridBagConstraints.NONE;      //reset to default
            c.weightx=0.0;                       //reset to default
            gridBag.setConstraints(left[i], c);
            container.add(left[i]);

            c.gridwidth=GridBagConstraints.REMAINDER;     //end row
            if (!(right[i] instanceof JComboBox)) {
                c.fill=GridBagConstraints.HORIZONTAL;
            } else {
                c.anchor=GridBagConstraints.WEST;
            }
            c.weightx=1.0;
            gridBag.setConstraints(right[i], c);
            container.add(right[i]);
        }

    }

    public class NewDatastreamPane 
            extends JPanel implements ActionListener {

        JTextField m_labelTextField;
        JTextArea m_controlGroupTextArea;
        JComboBox m_mimeComboBox;
        JComboBox m_mdClassComboBox;
        JComboBox m_mdTypeComboBox;
        CardLayout m_contentCard;
        JPanel m_specificPane;
        TextContentEditor m_xEditor=null;
        TextContentEditor m_mEditor=null;

        String m_controlGroup;

        static final String X_DESCRIPTION="Metadata that is stored and managed inside the "
                + "repository.  This must be well-formed XML and will be "
                + "stripped of processing instructions and comments."
                + "Use of XML namespaces is optional and schema validity is "
                + "not enforced by the repository.";
        static final String M_DESCRIPTION="Arbitary content that is stored and managed inside the "
                + "repository.  This is similar to internal XML metadata, but it does not have "
                + "any format restrictions, and is delieved as-is from the repository.";
        static final String E_DESCRIPTION="Content that is not managed by Fedora, "
                + "and is ultimately hosted on some other server.  Each time the "
                + "content is accessed, Fedora will request it from its host and "
                + "send it to the client.";
        static final String R_DESCRIPTION="Fedora will send clients a redirect to the URL "
                + "you specify for this datastream.  This is useful in situations where the content "
                + "must be delivered by a special streaming server, it contains "
                + "relative hyperlinks, or there are licensing restrictions that prevent "
                + "it from being proxied.";

        public NewDatastreamPane(String[] dropdownMimeTypes) {

            JComponent[] left=new JComponent[] { new JLabel("Label"), 
                                                 new JLabel("MIME Type"), 
                                                 new JLabel("Control Group") };

            m_labelTextField=new JTextField("Enter a label here.");
            m_mimeComboBox=new JComboBox(dropdownMimeTypes);
            m_mimeComboBox.setEditable(true);
            JPanel controlGroupPanel=new JPanel();
            JRadioButton xButton=new JRadioButton("Internal XML Metadata");
            xButton.setSelected(true);
            m_controlGroup="X";
            xButton.setActionCommand("X");
            xButton.addActionListener(this);
            JRadioButton mButton=new JRadioButton("Managed Content");
            mButton.setActionCommand("M");
            mButton.addActionListener(this);
            JRadioButton eButton=new JRadioButton("External Referenced Content");
            eButton.setActionCommand("E");
            eButton.addActionListener(this);
            JRadioButton rButton=new JRadioButton("Redirect");
            rButton.setActionCommand("R");
            rButton.addActionListener(this);
            ButtonGroup group=new ButtonGroup();
            group.add(xButton); group.add(mButton); group.add(eButton); group.add(rButton);
            controlGroupPanel.setLayout(new GridLayout(0, 1));
            controlGroupPanel.add(xButton);
            controlGroupPanel.add(mButton);
            controlGroupPanel.add(eButton);
            controlGroupPanel.add(rButton);
            JPanel controlGroupOuterPanel=new JPanel(new BorderLayout());
            controlGroupOuterPanel.add(controlGroupPanel, BorderLayout.WEST);
            m_controlGroupTextArea=new JTextArea(X_DESCRIPTION);
            m_controlGroupTextArea.setLineWrap(true);
            m_controlGroupTextArea.setEditable(false);
            m_controlGroupTextArea.setWrapStyleWord(true);
            m_controlGroupTextArea.setBackground(controlGroupOuterPanel.getBackground());

            controlGroupOuterPanel.add(m_controlGroupTextArea, BorderLayout.CENTER);

            JComponent[] right=new JComponent[] { m_labelTextField, m_mimeComboBox, controlGroupOuterPanel };

            JPanel commonPane=new JPanel();
            GridBagLayout grid=new GridBagLayout();
            commonPane.setLayout(grid);
            addRows(left, right, grid, commonPane);

            // XPANE: need metadata class and mdType
            left=new JComponent[] { new JLabel("Classification"),
                                    new JLabel("Metadata Type") };
            m_mdClassComboBox=new JComboBox(new String[] { "descriptive",
                                                           "digital provenance",
                                                           "source", "rights",
                                                           "technical" });
            m_mdTypeComboBox=new JComboBox(new String[] { "DC", "DDI", "EAD",
                                                          "FGDC", "LC_AV", "MARC",
                                                          "NISOIMG", "TEIHDR",
                                                          "VRA" });
            m_mdTypeComboBox.setEditable(true);
            right=new JComponent[] { m_mdClassComboBox, m_mdTypeComboBox };
            JPanel xTopPane=new JPanel();
            grid=new GridBagLayout();
            xTopPane.setLayout(grid);
            addRows(left, right, grid, xTopPane);
            try {
                m_xEditor=new TextContentEditor();
                m_xEditor.init("text/plain", new ByteArrayInputStream(
                        new String("Enter XML here, or click \"Import\" below.").
                        getBytes("UTF-8")), false);
                m_xEditor.setXML(true); // inline xml is always going to be xml,
                                        // initted as text/plain because empty!=valid xml
            } catch (Exception e) { }
            JPanel xBottomPane=new JPanel();
            xBottomPane.setLayout(new FlowLayout());
            JButton xImportButton=new JButton("Import...");
            xImportButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    ImportDialog imp=new ImportDialog();
                    if (imp.file!=null) {
                        try {
                            m_xEditor.setContent(new FileInputStream(imp.file));
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(Administrator.getDesktop(),
                                    e.getMessage(), "Import Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
            xBottomPane.add(xImportButton);
            JPanel xPane=new JPanel();
            xPane.setLayout(new BorderLayout());
            xPane.add(xTopPane, BorderLayout.NORTH);
            xPane.add(m_xEditor.getComponent(), BorderLayout.CENTER);
            xPane.add(xBottomPane, BorderLayout.SOUTH);

            JPanel mPane=new JPanel();
            mPane.add(new JLabel("mPane"));
            JPanel erPane=new JPanel();
            erPane.add(new JLabel("erPane"));
            m_specificPane=new JPanel();
            m_contentCard=new CardLayout();
            m_specificPane.setLayout(m_contentCard);
            m_specificPane.add(xPane, "X");
            m_specificPane.add(mPane, "M");
            m_specificPane.add(erPane, "ER");

            JPanel entryPane=new JPanel();
            entryPane.setLayout(new BorderLayout());
            entryPane.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(),
                    BorderFactory.createEmptyBorder(6,6,6,6)
                    ));
            entryPane.add(commonPane, BorderLayout.NORTH);
            entryPane.add(m_specificPane, BorderLayout.CENTER);

            JButton saveButton=new JButton("Save");
            saveButton.setActionCommand("Save");
            saveButton.addActionListener(this);

            JPanel buttonPane=new JPanel();
            buttonPane.setLayout(new FlowLayout());
            buttonPane.add(saveButton);

            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
            add(entryPane, BorderLayout.CENTER);
            add(buttonPane, BorderLayout.SOUTH);
        }

        public void actionPerformed(ActionEvent evt) {
            String cmd=evt.getActionCommand();
            if (cmd.equals("X")) {
                m_controlGroupTextArea.setText(X_DESCRIPTION);
                m_contentCard.show(m_specificPane, "X");
                m_controlGroup="X";
            } else if (cmd.equals("M")) {
                m_controlGroupTextArea.setText(M_DESCRIPTION);
                m_contentCard.show(m_specificPane, "M");
                m_controlGroup="M";
            } else if (cmd.equals("E")) {
                m_controlGroupTextArea.setText(E_DESCRIPTION);
                m_contentCard.show(m_specificPane, "ER");
                m_controlGroup="E";
            } else if (cmd.equals("R")) {
                m_controlGroupTextArea.setText(R_DESCRIPTION);
                m_contentCard.show(m_specificPane, "ER");
                m_controlGroup="R";
            } else if (cmd.equals("Save")) {
                try {
                    // try to save... first set common values for call
                    String pid=m_pid;
                    String label=m_labelTextField.getText();
                    String mimeType=(String) m_mimeComboBox.getSelectedItem();
                    String location=null;
                    String mdClass=null;
                    String mdType=null;
                    if (m_controlGroup.equals("X")) {
                        // m_mdClassComboBox
                        mdClass=(String) m_mdClassComboBox.getSelectedItem();
                        // m_mdTypeComboBox
                        mdType=(String) m_mdTypeComboBox.getSelectedItem();
                        // m_xEditor
                        location=Administrator.UPLOADER.upload(m_xEditor.getContent());
                    } else {
                        throw new IOException("Not implemented for this control group");
                    }
                    String newID=Administrator.APIM.addDatastream(pid, label, 
                            mimeType, location, m_controlGroup, mdClass, mdType);
                    addDatastreamTab(newID);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(Administrator.getDesktop(),
                            e.getMessage(), "Error saving new datastream",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}