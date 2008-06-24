/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

import fedora.common.Constants;

/**
 * Launch a dialog for selecting which XML format to ingest. Valid options are
 * FOXML1_1.uri, FOXML1_0.uri and METS_EXT1_1.uri.
 * 
 * @author Sandy Payette
 */
public class ObjectFormatDialog
        extends JDialog
        implements ActionListener, Constants {

    private static final long serialVersionUID = 1L;

    private final JRadioButton foxml11Button;
    
    private final JRadioButton foxml10Button;

    private final JRadioButton metsfButton;
    
    private final JRadioButton atomButton;

    private final ButtonGroup fmt_buttonGroup = new ButtonGroup();

    private final JLabel warningLabel;
    
    protected String fmt_chosen;

    public ObjectFormatDialog(String title) {
        super(JOptionPane.getFrameForComponent(Administrator.getDesktop()),
              title,
              true);
        setSize(350, 320);
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent we) {
                fmt_chosen = null;
                dispose();
            }
        });
        
        JPanel textPane = new JPanel();
        textPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        String warningText = 
            "<html>"
             + "Select the XML format of files to be ingested.<br>"
             + "Files not in FOXML 1.1 format will be updated<br>"
             + "to FOXML 1.1 on ingest. This conversion process<br>"
             + "may not retain all information available in the<br>"
             + "original format."
          + "</html>";
        warningLabel = new JLabel(warningText);
        warningLabel.setHorizontalTextPosition(JLabel.CENTER);
        textPane.add(warningLabel);
        
        JPanel inputPane = new JPanel();
        inputPane
                .setBorder(BorderFactory
                        .createCompoundBorder(BorderFactory
                                                      .createCompoundBorder(BorderFactory
                                                                                    .createEmptyBorder(12,
                                                                                                       12,
                                                                                                       12,
                                                                                                       12),
                                                                            BorderFactory
                                                                                    .createEtchedBorder()),
                                              BorderFactory
                                                      .createEmptyBorder(12,
                                                                         12,
                                                                         12,
                                                                         12)));

        inputPane.setLayout(new GridLayout(0, 1));
        foxml11Button = new JRadioButton("FOXML (Fedora Object XML) version 1.1", true);
        foxml11Button.setActionCommand(FOXML1_1.uri);
        foxml11Button.addActionListener(this);
        foxml10Button = new JRadioButton("FOXML (Fedora Object XML) version 1.0", true);
        foxml10Button.setActionCommand(FOXML1_0.uri);
        foxml10Button.addActionListener(this);
        metsfButton = new JRadioButton("METS (Fedora METS Extension)", false);
        metsfButton.setActionCommand(METS_EXT1_1.uri);
        metsfButton.addActionListener(this);
        atomButton = new JRadioButton("Atom (Fedora Atom)", false);
        atomButton.setActionCommand(ATOM1_1.uri);
        atomButton.addActionListener(this);
       
        fmt_buttonGroup.add(foxml11Button);
        fmt_buttonGroup.add(foxml10Button);
        fmt_buttonGroup.add(metsfButton);
        fmt_buttonGroup.add(atomButton);
        fmt_chosen = FOXML1_1.uri;
        
        inputPane.add(foxml11Button);
        inputPane.add(foxml10Button);
        inputPane.add(metsfButton);
        inputPane.add(atomButton);

        JButton okButton = new JButton(new AbstractAction() {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent evt) {
                dispose();
            }
        });
        okButton.setText("OK");

        JButton cancelButton = new JButton(new AbstractAction() {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent evt) {
                fmt_chosen = null;
                dispose();
            }
        });
        cancelButton.setText("Cancel");
        JPanel buttonPane = new JPanel();
        buttonPane.add(okButton);
        buttonPane.add(cancelButton);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(textPane, BorderLayout.NORTH);
        contentPane.add(inputPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.SOUTH);

        //pack();
        setLocation(Administrator.INSTANCE.getCenteredPos(getWidth(),
                                                          getHeight()));
        setVisible(true);
    }

    // null means nothing selected or selection canceled
    public String getSelection() {
        return fmt_chosen;
    }

    /** Listens to the radio buttons. */
    public void actionPerformed(ActionEvent e) {
        if (foxml11Button.isSelected()) {
            fmt_chosen = FOXML1_1.uri;
        } else if (foxml10Button.isSelected()) {
            fmt_chosen = FOXML1_0.uri;
        } else if (metsfButton.isSelected()) {
            fmt_chosen = METS_EXT1_1.uri;
        } else if (atomButton.isSelected()) {
            fmt_chosen = ATOM1_1.uri;
        }
    }
}
