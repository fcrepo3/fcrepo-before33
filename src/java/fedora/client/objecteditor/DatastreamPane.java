package fedora.client.objecteditor;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fedora.client.Administrator;

import fedora.server.types.gen.Datastream;
import fedora.server.utilities.StreamUtility;

/**
 * Displays a datastream's attributes, allowing the editing of its state,
 * and some of the most recent version's attributes.  Also provides buttons
 * for working with the content of the datastream, depending on its type.
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
public class DatastreamPane
        extends EditingPane 
        implements ChangeListener {

    private static SimpleDateFormat s_formatter=
            new SimpleDateFormat("yyyy-MM-dd' at 'hh:mm:ss");

    private String m_pid;
    private Datastream m_mostRecent;

    private JComboBox m_stateComboBox;
    private JSlider m_versionSlider;
    private JPanel m_valuePane;
    private CardLayout m_versionCardLayout;
    private CurrentVersionPane m_currentVersionPane;

    /**
     * Build the pane.
     */
    public DatastreamPane(String pid, Datastream mostRecent)
            throws Exception {
		m_pid=pid;
        m_mostRecent=mostRecent;
        new TextContentEditor();  // causes it to be registered if not already

        // mainPane(commonPane, versionPane)

            // NORTH: commonPane(state, mimeType, controlGroup, infoType)

                    // LEFT: labels
                    JLabel stateLabel=new JLabel("State");
                    JLabel mimeTypeLabel=new JLabel("MIME Type");
                    JLabel controlGroupLabel=new JLabel("Control Group");
                    JLabel infoTypeLabel=new JLabel("Info Type");
                    JLabel[] leftCommonLabels=new JLabel[] {stateLabel, mimeTypeLabel};
                    JLabel[] rightCommonLabels=new JLabel[] {controlGroupLabel, infoTypeLabel};

                    // RIGHT: values
                    String[] comboBoxStrings={"Active", "Inactive", "Deleted"};
                    m_stateComboBox=new JComboBox(comboBoxStrings);
                    if (mostRecent.getState().equals("A")) {
                        m_stateComboBox.setSelectedIndex(0);
                    } else if (mostRecent.getState().equals("I")) {
                        m_stateComboBox.setSelectedIndex(1);
                    } else {
                        m_stateComboBox.setSelectedIndex(2);
                    }
                    m_stateComboBox.addActionListener(dataChangeListener);
                    JLabel mimeTypeValueLabel=new JLabel(mostRecent.getMIMEType());
                    JLabel controlGroupValueLabel=new JLabel(
                            getControlGroupString(
                                    mostRecent.getControlGroup().toString())
                            );
                    JLabel infoTypeValueLabel=new JLabel(mostRecent.getInfoType());
                    JComponent[] leftCommonValues=new JComponent[] {m_stateComboBox, mimeTypeValueLabel};
                    JComponent[] rightCommonValues=new JComponent[] {controlGroupValueLabel, infoTypeValueLabel};
    
                JPanel leftCommonPane=new JPanel();
                GridBagLayout leftCommonGridBag=new GridBagLayout();
                leftCommonPane.setLayout(leftCommonGridBag);
                addLabelValueRows(leftCommonLabels, leftCommonValues, 
                        leftCommonGridBag, leftCommonPane);
            
                JPanel rightCommonPane=new JPanel();
                GridBagLayout rightCommonGridBag=new GridBagLayout();
                rightCommonPane.setLayout(rightCommonGridBag);
                addLabelValueRows(rightCommonLabels, rightCommonValues, 
                        rightCommonGridBag, rightCommonPane);

            JPanel commonPane=new JPanel();
            commonPane.setLayout(new FlowLayout());
            commonPane.add(leftCommonPane);
            commonPane.add(rightCommonPane);

            // CENTER: versionPane(m_versionSlider, m_valuePane)

                // NORTH: m_versionSlider

                Calendar[] dates=Administrator.APIM.getDatastreamHistory(pid, 
                        mostRecent.getID());
                Datastream[] versions=new Datastream[dates.length];

                // retrieve and add in descending order, by date
                Arrays.sort(dates, new CalendarComparator());
                versions[0]=mostRecent;
                for (int i=1; i<dates.length; i++) {
                    versions[i]=Administrator.APIM.getDatastream(pid,
                            mostRecent.getID(), dates[dates.length-i-1]);
                }
                if (versions.length>1) {
                    m_versionSlider=new JSlider(JSlider.HORIZONTAL,
                            0, versions.length-1, 0);
                    m_versionSlider.addChangeListener(this);
                    m_versionSlider.setMajorTickSpacing(1);
                    m_versionSlider.setSnapToTicks(true);
                    m_versionSlider.setPaintTicks(true);
                    Hashtable labelTable=new Hashtable();
                    labelTable.put(new Integer(0), new JLabel("Current"));
                    if (versions.length>1) {
                        labelTable.put(new Integer(versions.length-1), 
                                new JLabel("First"));
                    }
                    m_versionSlider.setLabelTable(labelTable);
                    m_versionSlider.setPaintLabels(true);
                }

                // CENTER: m_valuePane(one card for each version)

                m_valuePane=new JPanel();
                m_versionCardLayout=new CardLayout();
                m_valuePane.setLayout(m_versionCardLayout);
                JPanel[] valuePanes=new JPanel[versions.length];

                    // CARD: valuePanes[0](versionValuePane, versionActionPane)

                    m_currentVersionPane=new CurrentVersionPane(mostRecent);
                    valuePanes[0]=m_currentVersionPane;

                m_valuePane.add(valuePanes[0], "0");

                    // CARD: valuePanes[1 to i](versionValuePane, versionActionPane)

                    for (int i=1; i<versions.length; i++) {
                        valuePanes[i]=new PriorVersionPane(versions[i]);
    
                        m_valuePane.add(valuePanes[i], "" + i);
                    }

            JPanel versionPane=new JPanel();
            versionPane.setLayout(new BorderLayout());
            String versionPaneLabel="1 Version";
            if (versions.length>1) {
                versionPaneLabel=versions.length + " Versions";
            }
            versionPane.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(6,0,0,0),
                    BorderFactory.createTitledBorder(
                            BorderFactory.createEtchedBorder(),
                            versionPaneLabel
                            )
                    ));
            if (versions.length>1) {
                versionPane.add(m_versionSlider, BorderLayout.NORTH);
            }
            versionPane.add(m_valuePane, BorderLayout.CENTER);

        mainPane.setLayout(new BorderLayout());
        mainPane.add(commonPane, BorderLayout.NORTH);
        mainPane.add(versionPane, BorderLayout.CENTER);
    }

    public void stateChanged(ChangeEvent e) {
       JSlider source=(JSlider)e.getSource();
       if (!source.getValueIsAdjusting()) {
           m_versionCardLayout.show(m_valuePane, "" + source.getValue());
       }
    }

    public boolean isDirty() {
        int stateIndex=0;
        if (m_mostRecent.getState().equals("I")) {
            stateIndex=1;
        }
        if (m_mostRecent.getState().equals("D")) {
            stateIndex=2;
        }
        if (stateIndex!=m_stateComboBox.getSelectedIndex()) {
            return true;
        }
        if (m_currentVersionPane.isDirty()) {
            return true;
        }
        return false;
    }

    private String getControlGroupString(String abbrev) {
        if (abbrev.equals("M")) {
            return "Internal Managed Content";
        } else if (abbrev.equals("X")) {
            return "Internal XML Metadata";
        } else if (abbrev.equals("R")) {
            return "External Redirect";
        } else {
            return "External Reference";
        }
    }

    public void saveChanges(String logMessage) 
            throws Exception {

// defer this stuff to the below jpanel...todo
        String state=null;
        int i=m_stateComboBox.getSelectedIndex();
        if (i==0)
           state="A";
        if (i==1)
           state="I";
        if (i==2)
           state="D";
		if (m_currentVersionPane.isDirty()) {
		    // defer to the currentVersionPane if anything else changed
		    m_currentVersionPane.saveChanges(state, logMessage);
		} else {
		    // since only state changed, we can take care of it here
			Administrator.APIM.setDatastreamState(m_pid, m_mostRecent.getID(),
			        state, logMessage);
		}
    }

    public void changesSaved() {
        int i=m_stateComboBox.getSelectedIndex();
        if (i==0)
           m_mostRecent.setState("A");
        if (i==1)
           m_mostRecent.setState("I");
        if (i==2)
           m_mostRecent.setState("D");
        m_currentVersionPane.changesSaved();
    }

    public void undoChanges() {
        if (m_mostRecent.getState().equals("A"))
            m_stateComboBox.setSelectedIndex(0);
        if (m_mostRecent.getState().equals("I"))
            m_stateComboBox.setSelectedIndex(1);
        if (m_mostRecent.getState().equals("D"))
            m_stateComboBox.setSelectedIndex(2);
        m_currentVersionPane.undoChanges();
    }


    public class CalendarComparator
            implements Comparator {

        public int compare(Object o1, Object o2) {
            long ms1=((Calendar) o1).getTime().getTime();
            long ms2=((Calendar) o1).getTime().getTime();
            if (ms1<ms2) return -1;
            if (ms1>ms2) return 1;
            return 0;
        }
    }

    public class CurrentVersionPane
            extends JPanel
            implements PotentiallyDirty {

        private Datastream m_ds;
        private JTextField m_locationTextField;
        private JTextField m_labelTextField;
        private JButton m_editButton;
        private JButton m_viewButton;
        private JButton m_importButton;
        private JButton m_exportButton;

        private ContentEditor m_editor;
        private ContentViewer m_viewer;
        private boolean m_canEdit;
        private boolean m_canView;
        private File m_importFile;
        private JLabel m_importLabel;

        private boolean X;
        private boolean M;
        private boolean E;
        private boolean R;

        public CurrentVersionPane(Datastream ds) {
            m_ds=ds;
            if (ds.getControlGroup().toString().equals("X")) {
                X=true;
            } else if (ds.getControlGroup().toString().equals("M")) {
                M=true;
            } else if (ds.getControlGroup().toString().equals("E")) {
                E=true;
            } else if (ds.getControlGroup().toString().equals("R")) {
                R=true;
            }
            // editing is possible if it's XML or Managed content and 
            // not a special datastream and hasEditor(mimeType)
            if ((X || M) 
                    && ( !ds.getID().equals("METHODMAP") 
                            && !ds.getID().equals("DSINPUTSPEC") 
                            && !ds.getID().equals("WSDL") 
                       )
               ) {
                m_canEdit=ContentHandlerFactory.hasEditor(ds.getMIMEType());
            }
            m_canView=ContentHandlerFactory.hasViewer(ds.getMIMEType());
            // whether they're used or not, create these here
            m_editButton=new JButton("Edit");
            m_viewButton=new JButton("View");
            m_importButton=new JButton("Import...");
            m_exportButton=new JButton("Export...");
            // How we set this JPanel up depends on:
            // what control group it is in and
            // whether it can be edited or viewed
            setLayout(new BorderLayout());

            // do the field panel (NORTH)
            JLabel dateLabel=new JLabel("Created");
            JLabel labelLabel=new JLabel("Label");
            JLabel[] labels;
            if (R || E) {
                JLabel locationLabel=new JLabel("Reference");
                labels=new JLabel[] {dateLabel, labelLabel, locationLabel};
            } else {
                labels=new JLabel[] {dateLabel, labelLabel};
            }
            JLabel dateValue=new JLabel(s_formatter.format(ds.getCreateDate().getTime()));
            m_labelTextField=new JTextField(ds.getLabel());
            m_labelTextField.getDocument().addDocumentListener(
                    dataChangeListener);
            if (ds.getID().equals("METHODMAP")
                    || ds.getID().equals("DSINPUTSPEC")
                    || ds.getID().equals("WSDL")) {
                // disable label changes for special datastreams
                m_labelTextField.setEnabled(false);
            }

            JComponent[] values;
            if (R || E) {
                m_locationTextField=new JTextField(m_ds.getLocation());
                m_locationTextField.getDocument().addDocumentListener(
                    dataChangeListener);
                values=new JComponent[] {dateValue, m_labelTextField, m_locationTextField};
            } else {
                values=new JComponent[] {dateValue, m_labelTextField};
            }

            JPanel fieldPane=new JPanel();
            GridBagLayout grid=new GridBagLayout();
            fieldPane.setLayout(grid);
            addLabelValueRows(labels, values, 
                    grid, fieldPane);
            add(fieldPane, BorderLayout.NORTH);

            JPanel actionPane=new JPanel();
            actionPane.setLayout(new BoxLayout(actionPane, BoxLayout.Y_AXIS));
            if (m_canEdit) {
                // we know it's editable... add a button
                m_editButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        // add the editor, and disable the button
                        try {
                        startEditor();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(Administrator.getDesktop(),
                                    e.getMessage(), "Content Edit Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                actionPane.add(m_editButton);
            } else if (m_canView) {
                // it's not editable, but it's VIEWable... add a button
                m_viewButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        // add the viewer, and disable the view button
                        try {
                        startViewer();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(Administrator.getDesktop(),
                                    e.getMessage(), "Content View Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                actionPane.add(m_viewButton);                 
            }
            // should we add the Import button?  If we can set content, yes.
            if ((X || M) 
                    && ( !ds.getID().equals("METHODMAP") 
                        && !ds.getID().equals("DSINPUTSPEC") 
                        && !ds.getID().equals("WSDL") 
                   ) ) {
                actionPane.add(m_importButton);
                m_importButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        // remember what we did so we can back out if needed
                        boolean startedEditor=false;
                        boolean startedViewer=false;
                        try {
                            // prompt for the file or URL:
                            ImportDialog imp=new ImportDialog();

                            if (imp.file!=null) {
                                File file = imp.file;
                                String url=imp.url;
                                Administrator.setLastDir(file.getParentFile()); // remember the dir for next time
                                if (m_canEdit) {
                                    if (m_editButton.isEnabled()) {
                                        startEditor();
                                        startedEditor=true;
                                    } 
                                    // set content of existing edit widget
                                    m_editor.setContent(new FileInputStream(file));
                                } else if (m_canView) {
                                    if (m_viewButton.isEnabled()) {
                                        startViewer();
                                        startedViewer=true;
                                    }
                                    // set the content of the existing viewer widget
                                    m_viewer.setContent(new FileInputStream(file));
                                    // if that went ok, then remember the file
                                    m_importFile=file;
                                    // and send the signal
                                    dataChangeListener.dataChanged();
                                } else {
                                    // can't view or edit, so put a label
                                    if (url!=null) {
                                        m_importLabel=new JLabel("Will import " + url);
                                    } else {
                                        m_importLabel=new JLabel("Will import " + file.getPath());
                                    }
                                    add(m_importLabel, BorderLayout.CENTER);
                                    validate();
                                    // if that went ok, then remember the file
                                    m_importFile=file;
                                    // and send the signal
                                    dataChangeListener.dataChanged();
                                }
                            }
                        } catch (Exception e) {
                            if (startedEditor) {
                                // restore the original ui state
                                m_editButton.setEnabled(true);
                                remove(m_editor.getComponent());
                                m_editor=null;
                            }
                            if (startedViewer) {
                                // restore the original ui state
                                m_viewButton.setEnabled(true);
                                remove(m_viewer.getComponent());
                                m_viewer=null;
                            }
                            JOptionPane.showMessageDialog(Administrator.getDesktop(),
                                    e.getMessage(), "Content Import Failure",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            }
            // export is always possible!
            actionPane.add(m_exportButton);
            m_exportButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                try {
                    JFileChooser browse;
                    if (Administrator.getLastDir()==null) {
                        browse=new JFileChooser();
                    } else {
                        browse=new JFileChooser(Administrator.getLastDir());
                    }
                    browse.setApproveButtonText("Export");
                    browse.setApproveButtonMnemonic('E');
                    browse.setApproveButtonToolTipText("Exports to the selected file.");
                    browse.setDialogTitle("Export Datastream Content to...");
                    int returnVal = browse.showOpenDialog(Administrator.getDesktop());
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = browse.getSelectedFile();
                        Administrator.setLastDir(file.getParentFile()); // remember the dir for next time
                        Administrator.DOWNLOADER.getDatastreamContent(m_pid, 
                                m_ds.getID(), m_ds.getCreateDate().getTime(),
                                new FileOutputStream(file));
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(Administrator.getDesktop(),
                            e.getMessage(), "Content Export Failure",
                            JOptionPane.ERROR_MESSAGE);
                }
                }
            });
            add(actionPane, BorderLayout.EAST);
        }

        public Datastream getDatastream() {
            return m_ds;
        }

        /**
         * Bring up the editing pane, initialized with this datastream's
         * content.
         */
        private void startEditor() throws Exception {
                            m_editor=ContentHandlerFactory.getEditor(
                                    m_ds.getMIMEType(), 
                                    Administrator.DOWNLOADER.getDatastreamContent(
                                            m_pid, m_ds.getID(), 
                                            m_ds.getCreateDate().getTime()));
                            m_editor.setContentChangeListener(dataChangeListener);
                            add(m_editor.getComponent(), BorderLayout.CENTER);
                            m_editButton.setEnabled(false);
                            validate();
        }

        public void startViewer() throws Exception {
                            m_viewer=ContentHandlerFactory.getViewer(
                                    m_ds.getMIMEType(), 
                                    Administrator.DOWNLOADER.getDatastreamContent(
                                            m_pid, m_ds.getID(), 
                                            m_ds.getCreateDate().getTime()));
                            add(m_viewer.getComponent(), BorderLayout.CENTER);
                            m_viewButton.setEnabled(false);
                            validate();
        }

	public void saveChanges(String state, String logMessage)
            throws Exception {
        String label=m_labelTextField.getText();
		if (X) {
		    byte[] content=new byte[0];
		    if (m_editor!=null && m_editor.isDirty()) {
			    InputStream in=m_editor.getContent();
			    ByteArrayOutputStream out=new ByteArrayOutputStream();
				StreamUtility.pipeStream(in, out, 4096);
				content=out.toByteArray();
			}
		    Administrator.APIM.modifyDatastreamByValue(m_pid, m_ds.getID(), 
		            label, logMessage, content, state);
		} else if (M) {
            String loc=null; // if not set, server will not change content
            if (m_importFile!=null) {
                // TODO: upload the import file and set the ref
                loc=Administrator.UPLOADER.upload(m_importFile);
            } else if (m_editor!=null && m_editor.isDirty()) {
                // They've edited managed content... use its content
                loc=Administrator.UPLOADER.upload(m_editor.getContent());
            }
            Administrator.APIM.modifyDatastreamByReference(m_pid, m_ds.getID(),
                    label, logMessage, loc, state);
        } else {
            Administrator.APIM.modifyDatastreamByReference(m_pid, m_ds.getID(),
                    label, logMessage, m_locationTextField.getText(), state);
		}
    }
        public boolean isDirty() {
            if (!m_ds.getLabel().equals(m_labelTextField.getText())) {
                 return true;
            }
            if (m_locationTextField!=null 
                    && !m_locationTextField.getText().equals(m_ds.getLocation())) {
                 return true;
            }
            if (m_importFile!=null) return true;
            return (m_editor!=null && m_editor.isDirty());
        }

        public void changesSaved() {
            m_ds.setLabel(m_labelTextField.getText());
            if (m_locationTextField!=null) m_ds.setLocation(m_locationTextField.getText());
            if (m_editor!=null) m_editor.changesSaved();
            // TODO: would deal with m_importFile here, too... 
            if (m_importFile!=null) {
                // update display, and set to null
                m_importFile=null;
                remove(m_importLabel);
                m_importLabel=null;
            }


            // But all this may be a moot point, since it may be best to just
            // re-load the entire datastream history to get a fresh view with
            // the new version being the editable one
        }

        public void undoChanges() {
            m_labelTextField.setText(m_ds.getLabel());
            if (m_locationTextField!=null) m_locationTextField.setText(m_ds.getLocation());
            if (m_editor!=null) m_editor.undoChanges();
            if (m_importFile!=null) {
                m_importFile=null;
                // and remove the viewer if it's up, and re-enable the view
                // button
                if (m_canView) {
                    // must be viewing, so remove the viewer and re-enable the
                    // view button
                    m_viewButton.setEnabled(true);
                    remove(m_viewer.getComponent());
                    m_viewer=null;
                } else {
                    // remove the JLabel
                    remove(m_importLabel);
                    m_importLabel=null;
                }
            }
        }
    }

    public class PriorVersionPane
            extends JPanel {

        private ContentViewer v;
        private Datastream m_ds;

        public PriorVersionPane(Datastream ds) {
            m_ds=ds;
            setLayout(new BorderLayout());
			JLabel label=new JLabel(ds.getLabel()+ "((" + ds.getVersionID() + "))");
			add(label, BorderLayout.NORTH);
            if (ds.getControlGroup().toString().equals("X")) {
                try {
                v=ContentHandlerFactory.getViewer("text/xml", new ByteArrayInputStream(ds.getContentStream()));
                add(v.getComponent(), BorderLayout.CENTER);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        public Datastream getDatastream() {
            return m_ds;
        }

    }
}