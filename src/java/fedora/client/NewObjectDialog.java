package fedora.client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import fedora.client.actions.ViewObject;
import fedora.client.ingest.AutoIngestor;

import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.RepositoryInfo;
import fedora.server.types.gen.UserInfo;
import fedora.server.utilities.StreamUtility;

/**
 * Launch a dialog for entering information for a new object
 * (title, content model, and possibly a specified pid),
 * then create the object on the server and launch an editor on it.
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */

public class NewObjectDialog
        extends JDialog
        implements ItemListener {

    private JTextField m_labelField;
    private JTextField m_cModelField;
    private JCheckBox m_customPIDCheckBox;
    private JTextField m_customPIDField;
    private JButton m_okButton;

    private FedoraAPIM m_apim;

    // for the checkbox
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange()==ItemEvent.DESELECTED) {
            // disable text entry
            m_customPIDField.setEditable(false);
        } else if (e.getStateChange()==ItemEvent.SELECTED) {
            // enable text entry
            m_customPIDField.setEditable(true);
        }
    }

    public NewObjectDialog() {
        super(JOptionPane.getFrameForComponent(Administrator.getDesktop()), "New Object", true);

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

        JLabel labelLabel=new JLabel("Label");
        JLabel cModelLabel=new JLabel("Content Model");
        m_customPIDCheckBox=new JCheckBox("Use Custom PID");
        m_customPIDCheckBox.addItemListener(this);

        m_labelField=new JTextField("Enter a one-line description of the object.");
        m_cModelField=new JTextField("");
        m_customPIDField=new JTextField();
        m_customPIDField.setEditable(false);

        addLabelValueRows(new JComponent[] { labelLabel,
                                             cModelLabel,
                                             m_customPIDCheckBox },
                          new JComponent[] { m_labelField,
                                             m_cModelField,
                                             m_customPIDField },
                          gridBag,
                          inputPane);

        CreateAction createAction = new CreateAction();
        CreateListener createListener = new CreateListener(createAction);
        JButton okButton=new JButton(createAction);
        okButton.registerKeyboardAction(createListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
            JButton.WHEN_IN_FOCUSED_WINDOW);
        okButton.setText("Create");
        JButton cancelButton=new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                dispose();
            }
        });
        cancelButton.setText("Cancel");
        JPanel buttonPane=new JPanel();
        buttonPane.add(okButton);
        buttonPane.add(cancelButton);
        Container contentPane=getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(inputPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.SOUTH);

        pack();
        setLocation(Administrator.INSTANCE.getCenteredPos(getWidth(), getHeight()));
        setVisible(true);
    }



    public void addLabelValueRows(JComponent[] labels, JComponent[] values,
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

    public class CreateAction extends AbstractAction
    {

      public void actionPerformed(ActionEvent evt) {
          try {
              String pid="";
              String label=m_labelField.getText();
              String cModel=m_cModelField.getText();
              boolean ok=true;
              if ( m_labelField.getText().equals("") ) {
                  JOptionPane.showMessageDialog(Administrator.getDesktop(),
                      "Label must be non-empty",
                      "Error",
                      JOptionPane.ERROR_MESSAGE);
                  ok=false;
              }
              if ( m_customPIDCheckBox.isSelected() ) {
                  pid=m_customPIDField.getText();
                  if (m_customPIDField.getText().indexOf(":")<1) {
                      JOptionPane.showMessageDialog(Administrator.getDesktop(),
                           "Custom PID should be of the form \"namespace:1234\"",
                           "Error",
                           JOptionPane.ERROR_MESSAGE);
                      ok=false;
                  }
              }

              if (ok) {
	          	dispose();
	              // now that things look ok, give it a try
	              StringBuffer xml=new StringBuffer();
	              xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	              xml.append("<METS:mets xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
	              xml.append("           xmlns:METS=\"http://www.loc.gov/METS/\"\n");
	              xml.append("           xmlns:fedoraAudit=\"http://www.fedora.info/definitions/audit\"\n");
	              xml.append("           xmlns:xlink=\"http://www.w3.org/TR/xlink\"\n");
	              xml.append("           xsi:schemaLocation=\"http://www.loc.gov/standards/METS/ http://www.fedora.info/definitions/1/0/mets-fedora-ext.xsd\"\n");
	              xml.append("           TYPE=\"FedoraObject\"\n");
	              xml.append("           OBJID=\"" + StreamUtility.enc(pid) + "\"\n");
	              xml.append("           LABEL=\"" + StreamUtility.enc(label) + "\"\n");
	              xml.append("           PROFILE=\"" + StreamUtility.enc(cModel) + "\">\n");
	              xml.append("</METS:mets>");
	              String objXML=xml.toString();

	              /**
				StringBuffer xml=new StringBuffer();
				xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				xml.append("<foxml:digitalObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
				xml.append("           xmlns:foxml=\"info:fedora/def:foxml1.0\"\n");
				xml.append("           xsi:schemaLocation=\"info:fedora/def:foxml1.0 http://www.fedora.info/definitions/1/0/foxml1.0.xsd\"\n");
				xml.append("           PID=\"" + StreamUtility.enc(pid) + "\">\n");
				xml.append("  <foxml:objectProperties>\n");
				xml.append("    <foxml:property NAME=\"info:fedora/def:dobj:type\">FedoraObject</foxml:property>\n");
				xml.append("    <foxml:property NAME=\"info:fedora/def:dobj:label\">" + label + "</foxml:property>\n");
				xml.append("    <foxml:property NAME=\"info:fedora/def:dobj:cmodel\">" + cModel + "</foxml:property>\n");
				xml.append("  </foxml:objectProperties>\n");
				xml.append("</foxml:digitalObject>");
				String objXML=xml.toString();
	            System.out.println("Ingesting new object:");
	            System.out.println(objXML);
	              **/

	            ByteArrayInputStream in=new ByteArrayInputStream(
	                    objXML.getBytes("UTF-8"));
	            String newPID=AutoIngestor.ingestAndCommit(
	            		Administrator.APIA,
	                    Administrator.APIM,
	                    in,
	            //        "foxml1.0",
	                    "metslikefedora1",
	                    "Created with Admin GUI \"New Object\" command");
	            new ViewObject(newPID).launch();
              }
          } catch (Exception e) {
              String msg=e.getMessage();
              if (msg==null) msg=e.getClass().getName();
              JOptionPane.showMessageDialog(Administrator.getDesktop(),
                  msg,
                  "Error Creating Object",
                  JOptionPane.ERROR_MESSAGE);
          }
      }

    }



    public class CreateListener implements ActionListener
    {

        private CreateAction m_createAction;

        public CreateListener(CreateAction createAction)
        {
            this.m_createAction = createAction;
        }

        public void actionPerformed(ActionEvent e)
        {
            m_createAction.actionPerformed(e);
        }
    }

}