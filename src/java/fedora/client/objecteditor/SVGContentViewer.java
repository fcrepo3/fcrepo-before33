package fedora.client.objecteditor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import javax.swing.JComponent;

import org.apache.batik.swing.JSVGCanvas;

import fedora.server.utilities.StreamUtility;

/**
 * Views content of SVG images in a JComponent.
 */
public class SVGContentViewer 
        extends ContentViewer {

    private JSVGCanvas m_svgCanvas;
    private boolean s_registered;

    public SVGContentViewer() {
        if (!s_registered) {
            ContentHandlerFactory.register(this);
            s_registered=true;
        }
    }

    /**
     * Get the JComponent.
     */
    public JComponent getComponent() {
        return m_svgCanvas;
    }

    /**
     * Returns a list of content types that this component can handle.
     * This will usually be a list of MIME Types, but may also include
     * other notions of type known to be understood by the users of
     * ContentHandlerFactory.
     */
    public String[] getTypes() {
        return new String[] {"image/svg+xml"};
    }

    /**
     * Initializes the handler.  This should only be called once per instance,
     * and is guaranteed to have been called when this component is provided
     * by the ContentHandlerFactory.
     *
     * The viewOnly parameter signals to ContentEditor implementations that
     * editing capabilities are not desired by the caller.
     */
    public void init(String type, InputStream data, boolean viewOnly)
            throws IOException {
        setContent(data);
    }

    /**
     * Re-initializes the handler given new input data.
     * The old data can be discarded.
     */
    public void setContent(InputStream data) 
            throws IOException {
        try {
            File tempFile=File.createTempFile("fedora-view-svg-", null);
            tempFile.deleteOnExit();
            StreamUtility.pipeStream(data, new FileOutputStream(tempFile), 4096);
            if (m_svgCanvas==null) {
                m_svgCanvas=new JSVGCanvas();
                m_svgCanvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
                m_svgCanvas.setURI(tempFile.toURL().toString());
            } else {
                m_svgCanvas.setURI(tempFile.toURL().toString());
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

}