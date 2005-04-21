package fedora.client.export;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.BorderLayout;
import javax.swing.border.TitledBorder;
import fedora.client.Administrator;

/**
 * Launch a dialog for selecting which XML format to ingest.
 * Valid options as of Fedora 2.0 are "foxml1.0" and "metslikefedora1".
 *
 * @author payette@cs.cornell.edu
 * @version 
 */

public class ExportOptionsDialog
        extends JDialog {
        
    private String selections;
    
    private JRadioButton foxmlButton;
	private JRadioButton metsfButton;	
	private final ButtonGroup fmt_buttonGroup = new ButtonGroup();
	protected String fmt_chosen;
	
	private JRadioButton publicButton;
	private JRadioButton migrateButton;
	private JRadioButton archiveButton;
	private final ButtonGroup ctx_buttonGroup = new ButtonGroup();
	protected String ctx_chosen;


    public ExportOptionsDialog(String title) {
        super(JOptionPane.getFrameForComponent(Administrator.getDesktop()), title, true);
		setSize(500, 300);
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				fmt_chosen=null;
				ctx_chosen=null;
				dispose();
			}
		});
		
		// Set up the options input panel
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridLayout(2,1));
		optionsPanel.add(setFormatPanel());
		optionsPanel.add(setContextPanel());
        
        // Set up the OK and Cancel buttons panel
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
		
		JButton helpButton=new JButton(new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				showHelp();
			}
		});
		helpButton.setText("Help");
		
        JPanel buttonPanel=new JPanel();
        buttonPanel.add(okButton);
		buttonPanel.add(helpButton);
		buttonPanel.add(cancelButton);
		
		// Put everything together on the master pane
        Container contentPane=getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(optionsPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);       
        setLocation(Administrator.INSTANCE.getCenteredPos(getWidth(), getHeight()));
        setVisible(true);
    }

	private JPanel setFormatPanel()
	{
		JPanel formatPanel = new JPanel();
		formatPanel.setLayout(new GridBagLayout());
		formatPanel.setBorder(new TitledBorder("Select the desired export FORMAT"));
		// foxml radio button
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridy = 0;
		gbc.gridx = 0;
		foxmlButton = new JRadioButton("FOXML (Fedora Object XML)", true);
		foxmlButton.setActionCommand("foxml1.0");
		foxmlButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (foxmlButton.isSelected()) {
						fmt_chosen = "foxml1.0";
					}
				}
			}
		);
		formatPanel.add(foxmlButton, gbc);
		// metsf radio button
		gbc.gridx = 1;
		metsfButton = new JRadioButton("METS (Fedora METS Extension)", false);
		metsfButton.setActionCommand("metslikefedora1");
		metsfButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (metsfButton.isSelected()) {
						fmt_chosen = "metslikefedora1";
					}
				}
			}
		);
		formatPanel.add(metsfButton, gbc);
		// button grouping and default value
		fmt_buttonGroup.add(foxmlButton);
		fmt_buttonGroup.add(metsfButton);
		fmt_chosen = "foxml1.0";
		return formatPanel;
	}
	
    
	private JPanel setContextPanel()
	{
		JPanel contextPanel = new JPanel();
		contextPanel.setLayout(new GridBagLayout());
		contextPanel.setBorder(new TitledBorder("Select the desired export CONTEXT"));
		// migrate radio button
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridy = 0;
		gbc.gridx = 0;
		migrateButton = new JRadioButton("Migrate", true);
		migrateButton.setActionCommand("migrate");
		migrateButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (migrateButton.isSelected()) {
						ctx_chosen = "migrate";
					}
				}
			}
		);
		contextPanel.add(migrateButton, gbc);
		// public radio button
		gbc.gridx = 1;
		publicButton = new JRadioButton("Public Access", false);
		publicButton.setActionCommand("public");
		publicButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (publicButton.isSelected()) {
						ctx_chosen = "public";
					}
				}
			}
		);
		contextPanel.add(publicButton, gbc);
		// archive radio button
		gbc.gridx = 2;
		archiveButton = new JRadioButton("Archive", false);
		archiveButton.setActionCommand("archive");
		archiveButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (archiveButton.isSelected()) {
						ctx_chosen = "archive";
					}
				}
			}
		);
		archiveButton.setEnabled(false);
		contextPanel.add(archiveButton, gbc);		
		
		
		// button grouping and default value
		ctx_buttonGroup.add(migrateButton);
		ctx_buttonGroup.add(publicButton);
		ctx_buttonGroup.add(archiveButton);
		ctx_chosen = "migrate";
		return contextPanel;
	}

    // null means nothing selected or selection canceled
    public String getFormatSelection() {
        return fmt_chosen;
    }
    
	// null means nothing selected or selection canceled
	public String getContextSelection() {
		return ctx_chosen;
	}

	private void showHelp()
	{
		JTextArea helptxt = new JTextArea();
		helptxt.setLineWrap(true);
		helptxt.setWrapStyleWord(true);
		helptxt.setBounds(0,0,500,50);
		helptxt.append("There are two sections to the Export option dialog that"
		  + " must be completed:\n\n"
		  + " (1) Select the export FORMAT:\n\n"
		  + "     FOXML - select this option if you want the export file\n"
		  + "             to be encoded according the FOXML XML schema.\n\n"
		  + "     METS -  select this option if you want the export file\n"
		  + "             to be encoded according the Fedora extension of\n"
		  +	"             the METS XML schema.\n\n"
		  + " *************************************************************************\n"
		  + " (2) Select the export CONTEXT:\n\n"
		  + "     Migrate - (Default) select this option if you want the export file\n"
		  + "               to be appropriate for migration of an object from one\n"
		  + "               Fedora repository to another.  Any URLs that reference\n"
		  + "               the host:port of export repository will be specially encoded\n"
		  + "               so that the URLs will recognized by Fedora as relative\n"
		  + "               to whatever repository the object is subsequently stored.\n"
		  + "               When the export file is ingested into a new Fedora repository\n"
		  + "               the Fedora ingest process will ensure that the URLs\n"
		  + "               become local to the *new* repository.\n\n"
		  + "    Public Access - select this option if you want the export file\n"
		  + "               to be appropriate for use outside the context of a Fedora\n"
		  + "               repository.  All URLs that reference datastream content or\n"
		  + "               disseminations from the Fedora repository will be public\n"
		  + "               callback URLs to the exporting repository.\n\n"
		  + "    Archive - (Future Release) select this option if you want the export file\n"
		  + "               to serve as a self-contained archive of the object, where\n"
		  + "               all datastream content is directly in the export file.\n"
		  + "               Binary content will be base64-encoded and XML content inlined.\n");

		JOptionPane.showMessageDialog(
		  this, helptxt, "Help for Export Options",
		  JOptionPane.OK_OPTION);
	}
}