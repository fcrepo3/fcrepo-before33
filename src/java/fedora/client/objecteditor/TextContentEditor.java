package fedora.client.objecteditor;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.xml.sax.InputSource;

/**
 * A general-purpose text editor/viewer with XML pretty-printing.
 */
public class TextContentEditor
        extends ContentEditor 
        implements DocumentListener {

    /** This class handles all the common text MIME types by default. */
    public static String[] s_types=new String[] {
            "text/xml", "text/plain", "text/html", "text/css", "text/html", 
            "text/sgml", "text/tab-separated-values", 
            "text/xml-external-parsed-entity" };

    protected boolean m_dirty;
    protected ActionListener m_dataChangeListener;
    protected JTextComponent m_editor;
    protected JScrollPane m_component;
    protected boolean m_xml=false;
    protected String m_origContent;

    private static boolean s_registered=false;

    public TextContentEditor() {
        if (!s_registered) {
            ContentHandlerFactory.register(this);
            s_registered=true;
        }
    }

    public String[] getTypes() {
        return s_types;
    }

    public void init(String type, InputStream data, boolean viewOnly) 
            throws IOException {
        if (type.endsWith("xml")) {
            m_xml=true;
        }
        m_editor=new JTextArea();
        m_editor.setFont(new Font("monospaced", Font.PLAIN, 12));
        setContent(data);
        m_editor.setEditable(!viewOnly);
        m_component=new JScrollPane(m_editor);
    }

    public void setContent(InputStream data) 
            throws IOException {
        // get a string from the inputstream, assume it's UTF-8
        boolean formattedXML=true;
        if (m_xml) {
            try {
                // use xerces to pretty print the xml to the editor
                OutputFormat fmt=new OutputFormat("XML", "UTF-8", true);
                fmt.setOmitXMLDeclaration(true);
                fmt.setIndent(2);
                fmt.setLineWidth(120);
                fmt.setPreserveSpace(false);
                ByteArrayOutputStream buf=new ByteArrayOutputStream();
                XMLSerializer ser=new XMLSerializer(buf, fmt);
                DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                DocumentBuilder builder=factory.newDocumentBuilder();
                Document doc=builder.parse(data);
                ser.serialize(doc);
                m_origContent=new String(buf.toByteArray(), "UTF-8");
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getClass().getName() + " : " + e.getMessage());
            }
        } else {
            StringBuffer out=new StringBuffer();
            BufferedReader in=new BufferedReader(new InputStreamReader(data));
            String thisLine;
            while ((thisLine=in.readLine())!=null) {
                out.append(thisLine + "\n");
            }
            in.close();
            m_origContent=out.toString();
        }
        m_editor.setText(m_origContent);
        m_editor.setCaretPosition(0);
    }

    public JComponent getComponent() {
        return m_component;
    }

    public void changesSaved() {
        m_origContent=m_editor.getText();
        dataChanged();
    }

    public void undoChanges() {
        m_editor.setText(m_origContent);
        m_editor.setCaretPosition(0);
        dataChanged();
    }

    public boolean isDirty() {
        return (!m_origContent.equals(m_editor.getText()));
    }

    public void setContentChangeListener(ActionListener listener) {
        m_dataChangeListener=listener;
        m_editor.getDocument().addDocumentListener(this);
    }

    public InputStream getContent() 
            throws IOException {
        try {
            if (m_xml) {
                // if it's xml, throw an exception if it's not well-formed
                DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
                DocumentBuilder builder=factory.newDocumentBuilder();
                Document doc=builder.parse(new InputSource(new StringReader(
                        m_editor.getText())));
            }
            return new ByteArrayInputStream(m_editor.getText().getBytes("UTF-8"));
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    // Forward DocumentListener's events to the passed-in ActionListener
    public void changedUpdate(DocumentEvent e) {
        dataChanged();
    }

    public void insertUpdate(DocumentEvent e) {
        dataChanged();
    }

    public void removeUpdate(DocumentEvent e) {
        dataChanged();
    }

    private void dataChanged() {
        if (m_dataChangeListener!=null) 
            m_dataChangeListener.actionPerformed(new ActionEvent(this, 0, "dataChanged"));
    }

}