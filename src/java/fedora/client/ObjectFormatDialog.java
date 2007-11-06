/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import fedora.client.Administrator;

import fedora.common.Constants;

/**
 * Launch a dialog for selecting which XML format to ingest.
 * Valid options are FOXML1_1.uri and METS_EXT1_1.uri.
 *
 * @author payette@cs.cornell.edu
 */
public class ObjectFormatDialog
        extends JDialog 
        implements ActionListener, Constants {
        
	private static final long serialVersionUID = 1L;
    private String selections;
    
    private JRadioButton foxmlButton;
	private JRadioButton metsfButton;
	private final ButtonGroup fmt_buttonGroup = new ButtonGroup();
	protected String fmt_chosen;


    public ObjectFormatDialog(String title) {
        super(JOptionPane.getFrameForComponent(Administrator.getDesktop()), title, true);
		setSize(300, 200);
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				fmt_chosen=null;
				dispose();
			}
		});
        JPanel inputPane=new JPanel();
        inputPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(12, 12, 12, 12),
                    BorderFactory.createEtchedBorder()
                ),
                BorderFactory.createEmptyBorder(12,12,12,12)
                ));
                
        inputPane.setLayout(new GridLayout(0, 1));
		foxmlButton = new JRadioButton("FOXML (Fedora Object XML)", true);
		foxmlButton.setActionCommand(FOXML1_1.uri);
		foxmlButton.addActionListener(this);
		metsfButton = new JRadioButton("METS (Fedora METS Extension)", false);
		metsfButton.setActionCommand(METS_EXT1_1.uri);
		metsfButton.addActionListener(this);
		fmt_buttonGroup.add(foxmlButton);
		fmt_buttonGroup.add(metsfButton);
		fmt_chosen = FOXML1_1.uri;
        inputPane.add(foxmlButton);
        inputPane.add(metsfButton);
        
        JButton okButton=new JButton(new AbstractAction() {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent evt) { 
                dispose();
            }
        });
        okButton.setText("OK");
        
		JButton cancelButton=new JButton(new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent evt) {
				fmt_chosen=null;
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
        
        //pack();
        setLocation(Administrator.INSTANCE.getCenteredPos(getWidth(), getHeight()));
        setVisible(true);
    }

    // null means nothing selected or selection canceled
    public String getSelection() {
        return fmt_chosen;
    }

	/** Listens to the radio buttons. */
	public void actionPerformed(ActionEvent e) {
		if (foxmlButton.isSelected()) {
			fmt_chosen = FOXML1_1.uri;
		} else if (metsfButton.isSelected()) {
			fmt_chosen = METS_EXT1_1.uri;
		} 
	}
}
