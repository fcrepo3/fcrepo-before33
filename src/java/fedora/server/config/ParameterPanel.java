package fedora.server.config;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class ParameterPanel extends JPanel {

    private ParameterListModel m_model;

    public ParameterPanel(java.util.List parameterList) {
        super(new BorderLayout());
        m_model = new ParameterListModel(parameterList);
        // WEST: (NORTH, CENTER, SOUTH)
        JPanel paramChoice = new JPanel(new BorderLayout());
        paramChoice.add(new JLabel("Parameter:"), BorderLayout.NORTH);
        JList paramList = new JList(m_model);
        paramList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paramList.setVisibleRowCount(0);
        paramChoice.add(new JScrollPane(paramList), BorderLayout.CENTER);

        JPanel paramButtonPanel = new JPanel(new BorderLayout());
        paramButtonPanel.add(new JButton("Add"), BorderLayout.NORTH);
        paramButtonPanel.add(new JButton("Delete"), BorderLayout.SOUTH);
        paramChoice.add(paramButtonPanel, BorderLayout.SOUTH);

        add(paramChoice, BorderLayout.WEST);
    }

    /**
     * Get the values from the UI into a List of Parameter objects.
     */
    public java.util.List getParameters() {
        // FIXME: implement this
        return null;
    }

    private static Parameter[] getParamArray(java.util.List params) {
        Parameter[] out = new Parameter[params.size()];
        for (int i = 0; i < params.size(); i++) {
            out[i] = (Parameter) params.get(i);
        }
        return out;
    }

}
