package fedora.client;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Box.Filler;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;


import com.rolemodelsoft.drawlet.*;
import com.rolemodelsoft.drawlet.basics.*;
import com.rolemodelsoft.drawlet.examples.graphnode.*;
import com.rolemodelsoft.drawlet.jfc.*;
import java.awt.*;

public class DigitalObjectEditor
        extends JInternalFrame {
        
    private DigitalObject m_obj;
    private JDesktopPane m_desktop;
    
    private static JPopupMenu s_dsAddPopup;
    
    public DigitalObjectEditor(DigitalObject obj, JDesktopPane desktop) {
        super(obj.getName(),
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable
              
        m_obj=obj;
        m_desktop=desktop;

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Object", new ObjectAttributeEditor());
        tabbedPane.addTab("Datastreams", new DatastreamsEditor());
        tabbedPane.addTab("Disseminators", new DisseminatorsEditor());
        tabbedPane.setSelectedIndex(0);
        getContentPane().add(tabbedPane);
        setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/standard/table/RowDelete16.gif")));

        setSize(300,300);
    }

    /**
     *
     * ---------------------------------------------------------------------
     * |                                                                   |
     * |     SELECTED DATASTREAM (INLINE/BASIS)                            | 
     * |     ...................                                           |
     * |                                                                   |
     * |     Id:        ____________        [Edit Data]                    |
     * |                                                                   |
     * |     Mime Type: ____________        Size (bytes) xxxxx             |
     * |                                                                   |
     * |                           [Delete]                                |
     * |                                                                   |
     * |                                                                   |
     * |     INLINE DATASTREAMS (XML Only)     BASIS DATASTREAMS (ANY)     |
     * |     .........................................................     |
     * |                                                                   |
     * |     MyXMLStream1 ____________   ____> MyBinaryStream1             |
     * |                              \ /                                  |
     * |     MyXMLStream2 _____________X ____> MyBinaryStream2             |
     * |                                X                                  |
     * |     MyXMLStream3 _____________/_\___> MyBinaryStream3             |
     * |                                                                   |
     * |        [Add...]                           [Add...]                |
     * |                                                                   |
     * ---------------------------------------------------------------------
     */
    protected class DatastreamsEditor
            extends JPanel
            implements ActionListener {
            
        DrawingCanvas m_canvas;
        
        protected DatastreamsEditor() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
            JPanel dataStreamInfo=new JPanel();
            
            GridBagLayout dsl=new GridBagLayout();
            GridBagConstraints dsc=new GridBagConstraints();
            dataStreamInfo.setLayout(dsl);
            
            dsc.gridx=0; 
            dsc.gridy=0; 
            dsc.insets=new Insets(2,2,2,2);
            dsc.anchor=GridBagConstraints.NORTHWEST;
            // dataStreamInfo.setLayout(new BoxLayout(dataStreamInfo, BoxLayout.Y_AXIS));
            dataStreamInfo.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
            JLabel sel=new JLabel("Selected Datastream:", JLabel.LEFT);
            sel.setFont(sel.getFont().deriveFont(Font.BOLD));
            dsl.setConstraints(sel,dsc);
            dataStreamInfo.add(sel);
            
            
            dsc.gridy=1; 
            dsc.anchor=GridBagConstraints.NORTHEAST;
            JPanel namePanel=new JPanel();
            GridBagLayout gridBag=new GridBagLayout();
            namePanel.setLayout(gridBag);
            GridBagConstraints c=new GridBagConstraints();
            c.weightx=0.5;
            c.gridx=0;
            c.gridy=0;
            c.anchor=GridBagConstraints.EAST;
            JLabel n=new JLabel("Id: ");
            n.setEnabled(false);
            //n.setAlignmentX(Container.RIGHT_ALIGNMENT);
            gridBag.setConstraints(n,c);
            namePanel.add(n);
            c.gridx=1;
            c.weightx=0.0;
            JTextField t=new JTextField(10);
            t.setEnabled(false);
            t.setPreferredSize(t.getPreferredSize());
            gridBag.setConstraints(t,c);
            namePanel.add(t);
            namePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            Dimension smallFillDim=new Dimension(0,0);
            Dimension normalFillDim=new Dimension(0,5);
            //dataStreamInfo.add(new Box.Filler(smallFillDim, normalFillDim, normalFillDim));
            dsl.setConstraints(namePanel,dsc);
            dataStreamInfo.add(namePanel);
            
            dsc.gridy=2; 
            JPanel mimePanel=new JPanel();
            gridBag=new GridBagLayout();
            mimePanel.setLayout(gridBag);
            c.gridx=0;
            c.weightx=0.5;
            n=new JLabel("Mime Type: ");
            gridBag.setConstraints(n,c);
            mimePanel.add(n);
            c.gridx=1;
            c.weightx=0.0;
            t=new JTextField(10);
            t.setPreferredSize(t.getPreferredSize());
            gridBag.setConstraints(t,c);
            mimePanel.add(t);
            mimePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            // dataStreamInfo.add(new Box.Filler(smallFillDim, normalFillDim, normalFillDim));
            dsl.setConstraints(mimePanel,dsc);
            dataStreamInfo.add(mimePanel);
            

            JButton editDataStreamButton=new JButton("Edit Data..."); 
            dsc.gridy++;
            dsl.setConstraints(editDataStreamButton,dsc);
            dataStreamInfo.add(editDataStreamButton);
            editDataStreamButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JInternalFrame dsEditFrame=new JInternalFrame(m_obj.getName() + " - ds1",
                        true, true, true, true);
                    dsEditFrame.setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/standard/general/Edit16.gif")));
                    JTextComponent textEditor=new JTextArea();
                    textEditor.setFont(new Font("monospaced", Font.PLAIN, 12));
                    dsEditFrame.getContentPane().add(new JScrollPane(textEditor));
                    dsEditFrame.setSize(300,300);
                    dsEditFrame.setVisible(true);
                    m_desktop.add(dsEditFrame);
                    try {
                        dsEditFrame.setSelected(true);
                    } catch (java.beans.PropertyVetoException pve) {}
                }
            });
            
            
            
            dsc.gridy++;
            dsc.weighty=0.5;
            dsc.fill=GridBagConstraints.BOTH;
            JPanel fillerPanel=new JPanel();
            dsl.setConstraints(fillerPanel,dsc);
            dataStreamInfo.add(fillerPanel);
            add(dataStreamInfo, BorderLayout.WEST);

        Drawing drawing=new SimpleDrawing();
		ScrollableJDrawingCanvasComponent canvasComponent = new ScrollableJDrawingCanvasComponent(drawing);
		m_canvas = canvasComponent.getCanvas();
		// canvas.setTool(new ArrowGraphNodeTool(canvas));
		m_canvas.setTool(new DataStreamAssociationTool(m_canvas));
		canvasComponent.setSize(1024,768);

//            JTextComponent tcomp=new JTextArea();
//            tcomp.setFont(new Font("monspaced", Font.PLAIN, 12));
//            add(new JScrollPane(canvasComponent), BorderLayout.CENTER);
            JPanel rightSide=new JPanel();
            rightSide.setLayout(new BorderLayout());
            JScrollPane canvasComponentScroller=new JScrollPane(canvasComponent);
            canvasComponent.getSimpleJDrawingCanvas().setScroller(canvasComponentScroller);
            rightSide.add(canvasComponentScroller, BorderLayout.CENTER);
            
            // Inline metadata and basis
            // Inline:
            // - you don't want them broken away from the object, ever
            // Basis:
            // - the content, always a reference, not necessarily xml
            
            // custodianship: (control group...ownerid in mets, e or i, that's it)
            // internal/external?
            // 
            
            
            JPanel dsButtonPanel=new JPanel();
            dsButtonPanel.setLayout(new BorderLayout());
            dsButtonPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
            
            
            s_dsAddPopup=new JPopupMenu("Add Datastream...");
            JMenu inlineMenu=new JMenu("Fedora User Metadata");
            JMenuItem descriptiveButton=new JMenuItem("Descriptive");
            descriptiveButton.addActionListener(this);
            inlineMenu.add(descriptiveButton);
            JMenuItem rightsButton=new JMenuItem("Rights");
            rightsButton.addActionListener(this);
            inlineMenu.add(rightsButton);
            JMenuItem technicalButton=new JMenuItem("Technical");
            technicalButton.addActionListener(this);
            inlineMenu.add(technicalButton);
            JMenuItem provButton=new JMenuItem("Provenence");
            provButton.addActionListener(this);
            inlineMenu.add(provButton);
            JMenuItem sourceButton=new JMenuItem("Source");
            sourceButton.addActionListener(this);
            inlineMenu.add(sourceButton);
            s_dsAddPopup.add(inlineMenu);
            JMenuItem externalButton=new JMenuItem("Referenced External Content");
            externalButton.addActionListener(this);
            JMenuItem internalButton=new JMenuItem("Fedora Content");
            internalButton.addActionListener(this);
            s_dsAddPopup.add(internalButton);
            s_dsAddPopup.add(externalButton);
            s_dsAddPopup.pack();
            
            JButton addDatastreamButton=new JButton("Add Datastream");
            addDatastreamButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		            s_dsAddPopup.show((Component) e.getSource(), 10, 10);
                }
           });

            dsButtonPanel.add(addDatastreamButton, BorderLayout.WEST);
            rightSide.add(dsButtonPanel, BorderLayout.NORTH);
            add(rightSide, BorderLayout.CENTER);
            //add(new JButton("Add Inline"), BorderLayout.SOUTH);
            
        /*
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c;
        contentPane.setLayout(gridbag);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = GridBagConstraints.REMAINDER;
        JPanel incomingPane=new JPanel();
        */
        
        }
        
        int leftFigureY=0;
        int rightFigureY=0;
        
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Descriptive")) {
            	Figure newFigure = new DataStreamNode(DataStreamNode.METADATA);
                DrawingStyle descStyle=new SimpleDrawingStyle();
                descStyle.setFillColor(new Color(255,255,102));
                descStyle.setFont(descStyle.getFont().deriveFont(Font.BOLD));
                newFigure.setStyle(descStyle);
                newFigure.move(20, leftFigureY*50+20);
                leftFigureY++;
                m_canvas.addFigure(newFigure);
            }
            if (e.getActionCommand().equals("Rights")) {
            	Figure newFigure = new DataStreamNode(DataStreamNode.METADATA);
                DrawingStyle descStyle=new SimpleDrawingStyle();
                descStyle.setFillColor(new Color(204,255,153));
                newFigure.setStyle(descStyle);
                newFigure.move(20, leftFigureY*50+20);
                leftFigureY++;
                m_canvas.addFigure(newFigure);
            }
            if (e.getActionCommand().equals("Technical")) {
            	Figure newFigure = new DataStreamNode(DataStreamNode.METADATA);
                DrawingStyle descStyle=new SimpleDrawingStyle();
                descStyle.setFillColor(new Color(153,255,255));
                newFigure.setStyle(descStyle);
                newFigure.move(20, leftFigureY*50+20);
                leftFigureY++;
                m_canvas.addFigure(newFigure);
            }
            if (e.getActionCommand().equals("Provenence")) {
            	Figure newFigure = new DataStreamNode(DataStreamNode.METADATA);
                DrawingStyle descStyle=new SimpleDrawingStyle();
                descStyle.setFillColor(new Color(255,204,255));
                newFigure.setStyle(descStyle);
                newFigure.move(20, leftFigureY*50+20);
                leftFigureY++;
                m_canvas.addFigure(newFigure);
            }
            if (e.getActionCommand().equals("Source")) {
            	Figure newFigure = new DataStreamNode(DataStreamNode.METADATA);
                DrawingStyle descStyle=new SimpleDrawingStyle();
                descStyle.setFillColor(new Color(255,204,102));
                newFigure.setStyle(descStyle);
                newFigure.move(20, leftFigureY*50+20);
                leftFigureY++;
                m_canvas.addFigure(newFigure);
            }
            
            if (e.getActionCommand().equals("Fedora Content")) {
            	Figure newFigure = new DataStreamNode(DataStreamNode.CONTENT);
                DrawingStyle descStyle=new SimpleDrawingStyle();
                descStyle.setFillColor(new Color(255,102,0));
                newFigure.setStyle(descStyle);
                newFigure.move(300, rightFigureY*50+20);
                rightFigureY++;
                m_canvas.addFigure(newFigure);
            }
            if (e.getActionCommand().equals("Referenced External Content")) {
            	Figure newFigure = new DataStreamNode(DataStreamNode.CONTENT);
                DrawingStyle descStyle=new SimpleDrawingStyle();
                descStyle.setFillColor(new Color(204,102,102));
                newFigure.setStyle(descStyle);
                newFigure.move(300, rightFigureY*50+20);
                rightFigureY++;
                m_canvas.addFigure(newFigure);
            }
        }
    }
    
    protected class ObjectAttributeEditor 
            extends JPanel {
        protected ObjectAttributeEditor() {
            add(new JLabel("TODO:Object Attributes go here"));
        }
    }
    
    protected class DisseminatorsEditor
            extends JPanel {
        protected DisseminatorsEditor() {
            add(new JLabel("TODO:Dissemination editor goes here"));
        }
    }
    
}
