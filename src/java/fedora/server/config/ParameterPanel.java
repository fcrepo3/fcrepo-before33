package fedora.server.config;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

public class ParameterPanel extends JPanel implements ListSelectionListener, ActionListener {

    private JList m_paramList;
    private ParameterListModel m_model;
    private JPanel m_paramValuePanel;
    private boolean m_ignoreValueChanged;

    public ParameterPanel(java.util.List parameterList) {
        super(new BorderLayout());
        m_model = new ParameterListModel(parameterList);

        //
        // WEST: Parameter chooser with add/delete buttons
        //
        JPanel paramChoice = new JPanel(new BorderLayout());
        paramChoice.add(new JLabel("Parameter:"), BorderLayout.NORTH);
        m_paramList = new JList(m_model);
        m_paramList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_paramList.setVisibleRowCount(0);
        m_paramList.addListSelectionListener(this);
        paramChoice.add(new JScrollPane(m_paramList), BorderLayout.CENTER);
        JPanel paramButtonPanel = new JPanel(new BorderLayout());
        JButton addButton = new JButton("Add");
        addButton.addActionListener(this);
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);
        paramButtonPanel.add(addButton, BorderLayout.NORTH);
        paramButtonPanel.add(deleteButton, BorderLayout.SOUTH);
        paramChoice.add(paramButtonPanel, BorderLayout.SOUTH);

        //
        // CENTER: CardLayout, one panel per parameter
        //
        m_paramValuePanel = new JPanel(new CardLayout());
        Iterator iter = parameterList.iterator();
        while (iter.hasNext()) {
            addParamValueCard((Parameter) iter.next());
        }

        add(paramChoice, BorderLayout.WEST);
        add(m_paramValuePanel, BorderLayout.CENTER);
        m_paramList.setSelectedIndex(0);
    }

    private void addParamValueCard(Parameter param) {
        m_paramValuePanel.add(new ParamValueCard(param), param.getName());
    }

    private void deleteParamValueCard(Parameter param) {
        Component[] components = m_paramValuePanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof ParamValueCard) {
                ParamValueCard card = (ParamValueCard) components[i];
                if (card.getName().equals(param.getName())) {
                    m_paramValuePanel.remove(card);
                }
            }
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!m_ignoreValueChanged) {
            CardLayout cl = (CardLayout) (m_paramValuePanel.getLayout());
            Parameter param = (Parameter) m_model.getElementAt(m_paramList.getSelectedIndex());
            cl.show(m_paramValuePanel, param.getName());
        }
    }

    /**
     * Get the values from the UI into a List of Parameter objects.
     */
    public java.util.List getParameters() {
        // FIXME: implement this, getting each Parameter from the ParamValueCard
        return null;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Add")) {
            String paramName = JOptionPane.showInputDialog("Enter the name of the new parameter.");
            if (paramName != null) {
                // first, check if one of that name is in m_model (if so we'll just switch to it)
                Iterator iter = m_model.toList().iterator();
                Parameter param = null;
                while (iter.hasNext()) {
                    Parameter p = (Parameter) iter.next();
                    if (p.getName().equals(paramName)) param = p;
                }
                if (param == null) {
                    param = new Parameter(paramName, "Enter value here.", "Enter description here.", new HashMap());
                    m_model.addElement(param);
                    addParamValueCard(param);
                }
                // switch to the new (or already existing) parameter
                m_paramList.setSelectedValue(param, true);
            }
        } else if (e.getActionCommand().equals("Delete")) {
            // delete the currently selected item from m_model
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    m_ignoreValueChanged = true;
                    int i = m_paramList.getSelectedIndex();
                    Parameter param = (Parameter) m_paramList.getSelectedValue();
                    m_model.remove(i);
                    m_ignoreValueChanged = false;
                    // ...and set the selection to something sane
                    if (m_model.size() > 0) {
                        if (m_model.size() > i) {
                            m_paramList.setSelectedIndex(i);
                        } else {
                            i = m_model.size() - 1;
                            m_paramList.setSelectedIndex(m_model.size() - 1);
                        }
                    }
                    // finally, remove the panel from the cardlayout
                    deleteParamValueCard(param);
                }
            });
        }
    }

    public class ParamValueCard extends JPanel {

        private Parameter m_param;

        private JTextArea m_descArea;
        private JComboBox m_profileList;
        private JTextField m_valueText;
        private JButton m_addButton;
        private JButton m_deleteButton;
        private String m_name;

        public String getName() {
            return m_name;
        }

        /** 
         * A JPanel for modifying the description, value, and server profile-specific values
         * of a particular parameter.
         * The layout is accomplished through nested Panels using BorderLayouts.
         */
        public ParamValueCard(Parameter param) {
            super(new BorderLayout());
            m_name = param.getName();
            m_param = param;

            //
            // First, create all the interesting (non-layout) components
            //
            // a1
            if (param.getComment() == null) {
                m_descArea = new JTextArea();
            } else {
                m_descArea = new JTextArea(param.getComment());
            }
            // l1
            JLabel descriptionLabel = new JLabel("Description:");
            // l2
            JLabel valueLabel = new JLabel("Value:");
            // d1
            m_profileList = new JComboBox(new String[] { "<no profile>", "profile1", "profile2", "Add Profile...", "Delete Profile..." });
            // t1
            m_valueText = new JTextField(param.getValue());
            // b1
            m_addButton = new JButton("Add");
            // b2
            m_deleteButton = new JButton("Delete");

            //
            // Then, lay them out with a bunch of crazy JPanels
            //
            JPanel c1 = new JPanel(new BorderLayout());
            c1.add(new JScrollPane(m_descArea), BorderLayout.CENTER);
            c1.add(descriptionLabel, BorderLayout.NORTH);
            add(c1, BorderLayout.CENTER);

            JPanel s1 = new JPanel(new BorderLayout());
            add(s1, BorderLayout.SOUTH);
            JPanel w2 = new JPanel(new BorderLayout());
            w2.add(valueLabel, BorderLayout.NORTH); 
            JPanel c2 = new JPanel(new BorderLayout());
            JPanel w3 = new JPanel(new BorderLayout());
            w3.add(m_profileList, BorderLayout.NORTH);

            JPanel c3 = new JPanel(new BorderLayout());
            c3.add(m_valueText, BorderLayout.NORTH);

            c2.add(c3, BorderLayout.CENTER);
            c2.add(w3, BorderLayout.WEST);
            s1.add(c2, BorderLayout.CENTER);
            s1.add(w2, BorderLayout.WEST);
            add(s1, BorderLayout.SOUTH);
        }

        public Parameter getParameter() {
            return null;  // TODO: Construct a Parameter object from the widget values
        }
    
    }

}
