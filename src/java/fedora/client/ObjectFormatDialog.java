package fedora.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import fedora.client.Administrator;

/**
 * Launch a dialog for selecting which XML format to ingest.
 * Valid options as of Fedora 2.0 are "foxml1.0" and "metslikefedora1".
 *
 * @author payette@cs.cornell.edu
 * @version 
 */

public class ObjectFormatDialog
        extends JDialog implements ActionListener {
        
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
		foxmlButton.setActionCommand("foxml1.0");
		foxmlButton.addActionListener(this);
		metsfButton = new JRadioButton("METS (Fedora METS Extension)", false);
		metsfButton.setActionCommand("metslikefedora1");
		metsfButton.addActionListener(this);
		fmt_buttonGroup.add(foxmlButton);
		fmt_buttonGroup.add(metsfButton);
		fmt_chosen = "foxml1.0";
        inputPane.add(foxmlButton);
        inputPane.add(metsfButton);
        
        JButton okButton=new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent evt) { 
                dispose();
            }
        });
        okButton.setText("OK");
        
		JButton cancelButton=new JButton(new AbstractAction() {
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
			fmt_chosen = "foxml1.0";
		} else if (metsfButton.isSelected()) {
			fmt_chosen = "metslikefedora1";
		} 
	}
}